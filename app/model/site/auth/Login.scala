package model.site.auth

import model.component.auth._
import play.api.data.Form
import play.api.mvc.Call

case class ViewValueLoginForm(
  title:  String              = "ログイン",
//  action: Call                = controllers.routes.UserController.login(),
  submit: String              = "ログイン",
  form:   Form[LoginFormData] = LoginForm.form
)

