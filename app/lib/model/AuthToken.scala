package lib.model

import ixias.model._
import ixias.play.api.auth.token.Token.AuthenticityToken
import java.time.LocalDateTime
import scala.concurrent.duration.Duration

// AuthToken Model
import AuthToken._
case class AuthToken(
  id:        Option[Id],
  uid:       User.Id,
  token:     AuthenticityToken,
  expiry:    Duration      = Duration.Inf,
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

// Companion Object
object AuthToken {
  val  Id = the[Identity[Id]]
  type Id = Long @@ AuthToken

  def apply(uid: User.Id, token: AuthenticityToken, expiry: Duration): AuthToken#WithNoId =
    new AuthToken(
      id     = None,
      uid    = uid,
      token  = token,
      expiry = expiry
    ).toWithNoId
}
