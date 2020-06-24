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
import scala.concurrent._
import play.api.i18n.I18nSupport
import ixias.play.api.auth.mvc.AuthExtensionMethods

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  val authProfile:          UserAuthProfile,
) (implicit ec: ExecutionContext) extends AuthExtensionMethods with  BaseController with I18nSupport {

  val signupForm = Form(SignupForm.dataMapping)
  val loginForm  = Form(LoginForm.dataMapping)

  // アカウント登録フォーム
  def showSignupForm() = AuthenticatedOrNot(authProfile) { implicit req =>
    val userOpt = authProfile.loggedIn
    userOpt match {
      case Some(user) => Redirect(routes.HomeController.index())
      case None       => Ok(views.html.auth.Signup(new ViewValueSignup(form = signupForm)))
    }
  }

  // ログインフォーム
  def showLoginForm() = AuthenticatedOrNot(authProfile) { implicit req =>
    val userOpt = authProfile.loggedIn
    userOpt match {
      case Some(user) => Redirect(routes.HomeController.index())
      case None       => Ok(views.html.auth.Login(new ViewValueLogin(form = loginForm)))
    }
  }

  // アカウント登録
  def signup() = Action.async { implicit req =>
    signupForm.bindFromRequest.fold(
      // フォームエラー
      formWithErrors => {
        val vv = ViewValueSignup(form = formWithErrors)
        Future.successful(BadRequest(views.html.auth.Signup(vv)))
      },
      userData => {
        val hash = UserPassword.hash(userData.password)
        val user = User(userData.name, userData.email)
        (for {
          // email がユニークかどうか
          emailOpt <- UserRepository.getByEmail(user.v.email)
        } yield emailOpt) flatMap {
          case Some(email) => Future.successful(BadRequest(views.html.auth.Signup(
            new ViewValueSignup(form = signupForm)
          )))
          case None        => for {
            // email がユニークなら、ユーザー登録する
            uid1   <- UserRepository.add(user)
            // パスワードを登録する
            uid2   <- UserPasswordRepository.add(UserPassword(uid1, hash))
            // アカウント登録が成功した場合、セッション情報を付与し
            // ホーム画面へ遷移する
            result <- authProfile.loginSucceeded(uid1, {
              token =>
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
        (for {
          // email からユーザー取得
          userOpt <- UserRepository.getByEmail(loginUser.email)
        } yield userOpt) flatMap {
          case None       => Future.successful(Left(BadRequest(views.html.auth.Login(
            ViewValueLogin(form = loginForm.withError(errorUserNotFound))
          ))))
          case Some(user) => for {
            passOpt  <- UserPasswordRepository.get(user.id)
            verified <- passOpt match {
              case Some(pass) => Future.successful(UserPassword.verify(loginUser.password, pass.v.hash))
              case None       => Future.successful(false)
            }
          } yield verified match {
            case true  => Right(user)
            case false => Left(BadRequest(views.html.auth.Login(
              ViewValueLogin(form = loginForm.withError(errorPasswordInvalid))
            )))
          }
        } flatMap {
          case Left(result) => Future.successful(result)
          case Right(user)  => authProfile.loginSucceeded(user.id, { token =>
            Redirect(routes.HomeController.index())
          })
        }
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

