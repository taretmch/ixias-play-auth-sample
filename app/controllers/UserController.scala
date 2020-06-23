package controllers

import lib.model.{ User, UserPassword }
import lib.persistence.default.{ UserRepository, UserPasswordRepository }
import model.component.auth._
import model.component.auth.FormErrors._

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
      case None       => Ok(views.html.auth.Signup(new ViewValueSignupForm))
    }
  }

  // ログインフォーム
  def showLoginForm() = AuthenticatedOrNot(authProfile) { implicit req =>
    val userOpt = authProfile.loggedIn
    userOpt match {
      case Some(user) => Redirect(routes.HomeController.index())
      case None       => Ok(views.html.auth.Login(new ViewValueSignupForm))
    }
  }

  // アカウント登録
  def signup() = Action.async { implicit req =>
    signupForm.bindFromRequest.fold(
      // フォームエラー
      formWithErrors => {
        val vv = ViewValueSignupForm(form = formWithErrors)
        Future.successful(BadRequest(views.html.user.Signup(vv)))
      },
      userData => {
        val hash = UserPassword.hash(userData.password)
        val newUser = User(userData.name, userData.email)
        (for {
          // email がユニークかどうか
          emailOpt <- UserRepository.getByEmail(user.email)
        } yield emailOpt) flatMap {
          case Some(email) => Future.successful(BadRequest(views.html.auth.Signup(
            new ViewValueSignupForm(form = signupForm)
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
        val vv = ViewValueLoginForm(form = formWithErrors)
        Future.successful(BadRequest(views.html.auth.Login(vv)))
      },
      loginUser => {
        (for {
          // email からユーザー取得
          userOpt <- UserRepository.getByEmail(loginUser.email)
        } yield userOpt) flatMap {
          case Some(user) => for {
            pass <- UserPasswordRepository.get(user.id)
            // パスワードをチェックする
            verified <- Future.successful(UserPassword.verify(loginUser.password, pass.v.hash))
          } yield verified
          case None       => Future.successful(BadRequest(views.html.auth.Login(
            ViewValueLoginForm(form = loginForm.withError(errorEmailDuplicated))
          )))
        } flatMap {
          case true   => authProfile.loginSucceeded(user.id, { token =>
            Redirect(routes.HomeController.index())
          })
          case false  => Future.successful(BadRequest(views.html.auth.Login(
            ViewValueLoginForm(form = loginForm.withError(errorPasswordInvalid))
          )))
          case result => result
        }
      }
    )
  }

  // ログアウト
  def logout() = Authenticated(authProfile).async { implicit req =>
    authProfile.loggedIn { user =>
      authProfile.logoutSucceeded(user.id, {
        val vv = ViewValueHome()
        vv.authenticate(false)
        Redirect(routes.HomeController.index())
      })
    }
  }
}

