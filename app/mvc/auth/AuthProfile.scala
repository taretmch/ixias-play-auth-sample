package auth

import lib.model.User
import lib.persistence.default.UserRepository

import javax.inject._
import scala.concurrent.duration.Duration
import scala.concurrent.{ Future, ExecutionContext }

import play.api.Environment
import play.api.mvc.{ Result, RequestHeader }
import play.api.mvc.Results.Redirect

import ixias.play.api.auth.token.Token
import ixias.play.api.auth.token.TokenViaSession
import ixias.play.api.auth.container.Container
import ixias.play.api.mvc.Errors._

case class AuthProfile @Inject() (
  val container: AuthTokenContainer
)(implicit ec: ExecutionContext) extends ixias.play.api.auth.mvc.AuthProfile[User.Id, User, Unit] {

  val env: Environment                   = Environment.simple()
  val tokenAccessor: Token               = TokenViaSession("user")
  val datastore: Container[Id]           = container
  val executionContext: ExecutionContext = ec

  // 取得したユーザーIDからユーザーを取得する
  def resolve(id: Id)(implicit rh: RequestHeader): Future[Option[AuthEntity]] =
    UserRepository.get(id)
}

