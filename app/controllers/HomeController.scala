package controllers

import model.site.ViewValueHome
import model.component.auth.ViewValueUser
import mvc.auth.UserAuthProfile

import javax.inject._
import play.api._
import play.api.mvc._
import ixias.play.api.auth.mvc.AuthExtensionMethods

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents,
  val authProfile:          UserAuthProfile
) extends AuthExtensionMethods with BaseController {

  def index() = AuthenticatedOrNot(authProfile) { implicit request =>
    Ok(views.html.index(ViewValueHome(
      user = authProfile.loggedIn.map(ViewValueUser.from)
    )))
  }
}
