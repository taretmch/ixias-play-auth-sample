package model.component.auth

import play.api.data._
import play.api.data.Forms._

case class LoginFormData(email: String, password: String)

// ログイン用フォーム
object LoginForm {

  val dataMapping = mapping(
    "email"    -> email,
    "password" -> text(minLength = 8)
  )(LoginFormData.apply)(LoginFormData.unapply)

  val emptyDataMap = Map(
    "email"    -> "",
    "password" -> ""
  )
  
  // Form.data: Map[String, String] => LoginFormData
  def createFormDataFromMap(data: Map[String, String]): LoginFormData =
    LoginFormData(
      data("email"),
      data("password")
    )
}

