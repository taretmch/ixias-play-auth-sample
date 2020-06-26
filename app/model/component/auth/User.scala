package model.component.auth

import lib.model.User

case class ViewValueUser(
  id:    User.Id,
  name:  String,
  email: String
)

object ViewValueUser {

  def from(data: User#EmbeddedId): ViewValueUser =
    ViewValueUser(
      id    = data.id,
      name  = data.v.name,
      email = data.v.email
    )
}
