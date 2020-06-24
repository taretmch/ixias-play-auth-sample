package mvc.auth

import lib.model.User
import lib.model.AuthToken
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

case class AuthTokenContainer @Inject() (
)(implicit ec: ExecutionContext) extends Container[User.Id] {

  val TOKEN_LENGTH = 30
  val executionContext: ExecutionContext = ec

  // トークンを生成し、ユーザーIDを紐づける
  def open(uid: Id, expiry: Duration)
    (implicit request: RequestHeader): Future[AuthenticityToken] = {
      // トークンを生成する
      val token = AuthenticityToken(TokenGenerator().next(TOKEN_LENGTH))
      // トークンとユーザーIDを紐づける
      val authToken = AuthToken(uid, token, expiry)
      for {
        // トークンのDBへの保存
        _ <- AuthTokenRepository.add(authToken)
      } yield token
    }
      
  // トークンのタイムアウトを設定する
  def setTimeout(token: AuthenticityToken, expiry: Duration)
    (implicit request: RequestHeader): Future[Unit] =
      for {
        optAuthToken <- AuthTokenRepository.getByToken(token)
        _            <- optAuthToken match {
          case Some(token) => AuthTokenRepository.update(token.map(_.copy(expiry = expiry)))
          case None        => Future.successful(None)
        }
      } yield ()

  // トークンからユーザーIDを取得する
  def read(token: AuthenticityToken)
    (implicit request: RequestHeader): Future[Option[Id]] =
      for {
        optAuthToken <- AuthTokenRepository.getByToken(token)
      } yield optAuthToken.map(authToken => authToken.v.uid)

  // トークンを削除する
  def destroy(token: AuthenticityToken)
    (implicit request: RequestHeader): Future[Unit] =
      for {
        optAuthToken <- AuthTokenRepository.getByToken(token)
        _            <- optAuthToken match {
          case Some(token) => AuthTokenRepository.remove(token.id)
          case None        => Future.successful(None)
        }
      } yield ()
}

