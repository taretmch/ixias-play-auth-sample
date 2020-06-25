package controllers

import lib.model.{ User, UserPassword }
import lib.persistence.default.{ UserRepository, UserPasswordRepository }
import model.component.auth._
import model.component.auth.FormErrors._
import model.site.auth._
import mvc.auth.UserAuthProfile

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.i18n.I18nSupport
import scala.concurrent._

import cats.data.EitherT
import cats.implicits._
import ixias.play.api.auth.mvc.AuthExtensionMethods

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  val authProfile:          UserAuthProfile,
) (implicit ec: ExecutionContext) extends AuthExtensionMethods with  BaseController with I18nSupport {

  val signupForm = Form(SignupForm.dataMapping)
  val loginForm  = Form(LoginForm .dataMapping)

  // アカウント登録フォーム
  def showSignupForm() = AuthenticatedOrNot(authProfile) { implicit req =>
    authProfile.loggedIn match {
      case Some(user) => Redirect(routes.HomeController.index())
      case None       => Ok(views.html.auth.Signup(new ViewValueSignup(form = signupForm)))
    }
  }

  // ログインフォーム
  def showLoginForm() = AuthenticatedOrNot(authProfile) { implicit req =>
    authProfile.loggedIn match {
      case Some(user) => Redirect(routes.HomeController.index())
      case None       => Ok(views.html.auth.Login(new ViewValueLogin(form = loginForm)))
    }
  }

  // アカウント登録
  def signup() = Action.async { implicit req =>
    signupForm.bindFromRequest.fold(
      formWithErrors => {
        val vv = ViewValueSignup(form = formWithErrors)
        Future.successful(BadRequest(views.html.auth.Signup(vv)))
      },
      userData => {
        val hash = UserPassword.hash(userData.password)
        val user = User(userData.name, userData.email)
        (for {
          emailOpt <- UserRepository.getByEmail(user.v.email)
        } yield emailOpt) flatMap {
          // email が登録済なら、フォームを再表示する
          case Some(email) => Future.successful(BadRequest(views.html.auth.Signup(
            new ViewValueSignup(form = signupForm.withError(errorEmailDuplicated))
          )))
          case None        => for {
            // email がユニークなら、ユーザー登録する
            uid1   <- UserRepository.add(user)
            // パスワードを登録する
            uid2   <- UserPasswordRepository.add(UserPassword(uid1, hash))
            // アカウント登録が成功した場合、セッション情報を付与し
            // ホーム画面へ遷移する
            result <- authProfile.loginSucceeded(uid1, { token =>
              Redirect(routes.HomeController.index())
            })
          } yield result
        }
      })
  }
    
  // ログイン
  def login() = Action.async { implicit req =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        val vv = ViewValueLogin(form = formWithErrors)
        Future.successful(BadRequest(views.html.auth.Login(vv)))
      },
      loginUser => {
        val either: EitherT[Future, FormError, Result] = for {
          // email からユーザーを取得する
          user     <- EitherT(UserRepository.getByEmail(loginUser.email).map(_.toRight(errorUserNotFound)))
          pass     <- EitherT(UserPasswordRepository.get(user.id).map(_.toRight(errorPasswordInvalid)))
          // パスワードをチェックする
          verified <- EitherT(Future.successful(UserPassword.verify(loginUser.password, pass.v.hash)))
          // ログイン情報を付与して Home 画面を表示する
          result   <- EitherT.right(authProfile.loginSucceeded(user.id, { token =>
            Redirect(routes.HomeController.index())
          }).map(Right(_)))
        } yield result
        either.leftMap(e => BadRequest(views.html.auth.Login(
          ViewValueLogin(form = loginForm.withError(e))
        ))).value
      }
    )
  }

  // ログアウト
  def logout() = Authenticated(authProfile).async { implicit req =>
    authProfile.loggedIn { user =>
      authProfile.logoutSucceeded(user.id, {
        Redirect(routes.HomeController.index())
      })
    }
  }
}

