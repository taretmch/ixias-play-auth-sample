package lib.model

import ixias.model._
import ixias.play.api.auth.token.Token.AuthenticityToken
import java.time.LocalDateTime
import java.time.Duration

// AuthToken Model
import AuthToken._
case class AuthToken(
  id:        Option[Id],
  uid:       User.Id,
  token:     AuthenticityToken,
  expiry:    Option[Duration] = None,
  updatedAt: LocalDateTime    = NOW,
  createdAt: LocalDateTime    = NOW
) extends EntityModel[Id]

// Companion Object
object AuthToken {
  val  Id = the[Identity[Id]]
  type Id = Long @@ AuthToken
}
