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
          // email が未登録なら、登録処理を実行する
          case None        => for {
            // 1. ユーザーを DB に保存する
            uid1   <- UserRepository.add(user)
            // 2. ユーザーパスワードを DB に保存する
            uid2   <- UserPasswordRepository.add(UserPassword(uid1, hash))
            // 3. トークンを Cookie に付与してホーム画面へリダイレクトする
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
        // 1. ユーザーが存在するかどうかを取得する
        // 2. パスワードを検証する
        // 3. トークンを Cookie に付与してホーム画面へリダイレクトする
        ((for {
          user <- OptionT(UserRepository.getByEmail(userData.email))
          pass <- OptionT(UserPasswordRepository.get(user.id))
          _    <- OptionT(Future.successful(UserPassword.verifyOption(userData.password, pass.v.hash)))
        } yield user.id) semiflatMap {
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

