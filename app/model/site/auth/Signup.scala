package model.site.auth

import model.component.auth._
import play.api.data.Form
import play.api.mvc.Call

case class ViewValueSignup(
  title:  String              = "サインアップ",
  action: Call                = controllers.routes.UserController.signup(),
  submit: String              = "サインアップ",
  form:   Form[SignupFormData]
)

