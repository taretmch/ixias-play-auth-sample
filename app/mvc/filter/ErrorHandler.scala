package mvc.filter

import model.site.common.ViewValueError

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent._
import javax.inject.Singleton

import java.util.NoSuchElementException

@Singleton
class ErrorHandler extends HttpErrorHandler {
  // クライアントエラー
  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(
      Status(statusCode)("A client error occurred: " + message)
    )
  }

  // サーバーエラー
  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception match {
      case e: NoSuchElementException =>
        // None.get などの例外
        Future.successful(NotFound(views.html.common.Error(
          ViewValueError(reason = "404 Not Found: 指定されたデータが存在しません")
        )))
      case _                         =>
        // それ以外の例外
        Future.successful(InternalServerError(views.html.common.Error(
          ViewValueError(reason = "Internal Server Error: " + exception.getMessage)
        )))
    }
  }
}

