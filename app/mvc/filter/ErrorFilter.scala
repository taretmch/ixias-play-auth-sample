package mvc.filter

import model.site.common.ViewValueError

import javax.inject.Inject
import akka.stream.Materializer
import play.api.Logging
import play.api.mvc._
import play.api.mvc.Results._
import play.api.http.Status._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import ixias.play.api.mvc.Errors._

class ErrorFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] =
    nextFilter(requestHeader).map { result =>
      result.header.status match {
        case NOT_FOUND =>
          NotFound(views.html.common.Error(
            ViewValueError(reason = "404 Not Found")
          ))
        case UNAUTHORIZED =>
          Redirect(controllers.routes.UserController.showLoginForm)
        case _         =>
          result
      }
    }
}

