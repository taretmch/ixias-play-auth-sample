package mvc.auth

import lib.model.{ User, AuthToken }
import lib.persistence.default.AuthTokenRepository

import javax.inject._
import play.api.mvc.RequestHeader
import scala.concurrent.{ Future, ExecutionContext }
import scala.concurrent.duration.Duration

import ixias.model._
import ixias.security.TokenGenerator
import ixias.play.api.auth.container.Container
import ixias.play.api.auth.token.Token
import ixias.play.api.auth.token.Token.AuthenticityToken

import cats.data.OptionT
import cats.implicits._

case class AuthTokenContainer @Inject() (
)(implicit ec: ExecutionContext) extends Container[User.Id] {

  val TOKEN_LENGTH = 30
  val executionContext: ExecutionContext = ec

  // トークンを生成し、ユーザーIDを紐づける
  def open(uid: Id, expiry: Duration)
    (implicit request: RequestHeader): Future[AuthenticityToken] = {
      (for {
        authTokenOpt <- AuthTokenRepository.getByUserId(uid)
      } yield authTokenOpt) flatMap {
        case Some(authToken) => Future.successful(authToken.v.token)
        case None            => {
          val token     = AuthenticityToken(TokenGenerator().next(TOKEN_LENGTH))
          val authToken = AuthToken(uid, token, expiry)
          for {
            _ <- AuthTokenRepository.add(authToken)
          } yield token
        }
      }
    }
      
  // トークンのタイムアウトを設定する
  def setTimeout(token: AuthenticityToken, expiry: Duration)
    (implicit request: RequestHeader): Future[Unit] =
      ((for {
        authToken <- OptionT(AuthTokenRepository.getByToken(token))
      } yield authToken) semiflatMap { authToken =>
        for {
          _ <- AuthTokenRepository.update(authToken.map(_.copy(expiry = expiry)))
        } yield ()
      }).getOrElse(())

  // トークンからユーザーIDを取得する
  def read(token: AuthenticityToken)
    (implicit request: RequestHeader): Future[Option[Id]] =
      for {
        optAuthToken <- AuthTokenRepository.getByToken(token)
      } yield optAuthToken.map(_.v.uid)

  // トークンを削除する
  def destroy(token: AuthenticityToken)
    (implicit request: RequestHeader): Future[Unit] =
      ((for {
        authToken <- OptionT(AuthTokenRepository.getByToken(token))
      } yield authToken) semiflatMap { authToken =>
        for {
          _ <- AuthTokenRepository.remove(authToken.id)
        } yield ()
      }).getOrElse(())
}

