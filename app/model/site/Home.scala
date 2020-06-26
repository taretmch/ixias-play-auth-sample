package model.site

import model.component.auth.ViewValueUser

case class ViewValueHome(
  title: String                = "Welcome to My Blog!",
  user:  Option[ViewValueUser] = None
) {
  lazy val userName = user.fold("")(_.name)
}
