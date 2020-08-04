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

import cats.data.{ EitherT, OptionT }
import cats.implicits._
import ixias.play.api.auth.mvc.AuthExtensionMethods
import ixias.play.api.mvc.BaseExtensionMethods

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  val authProfile:          UserAuthProfile,
) (implicit ec: ExecutionContext) extends AuthExtensionMethods
  with BaseExtensionMethods
  with BaseController
  with I18nSupport
{

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
        UserRepository.getByEmail(user.v.email) flatMap {
          // email が登録済なら、フォームを再表示する
          case Some(email) => Future.successful(BadRequest(views.html.auth.Signup(
            new ViewValueSignup(form = signupForm.withError(errorEmailDuplicated).fill(userData))
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
      userData => {
        (OptionT(UserRepository.getByEmail(userData.email)) flatMapF {
          case user => for {
            passOpt <- UserPasswordRepository.get(user.id)
          } yield passOpt.map((user, _))
        } semiflatMap {
          case (user, pass) => for {
            verified <- Future.successful(UserPassword.verify(userData.password, pass.v.hash))
          } yield user.id
        } semiflatMap {
          case uid =>
            authProfile.loginSucceeded(uid, { token =>
              Redirect(routes.HomeController.index())
            })
        }).toRight(
          BadRequest(views.html.auth.Login(ViewValueLogin(
            form = loginForm.withError(errorLoginEmail).withError(errorLoginPassword).fill(userData)
          )))
        ).value
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

