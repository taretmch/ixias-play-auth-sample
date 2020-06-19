package model.component.auth

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import scala.util.matching.Regex

case class SignupFormData(name: String, email: String, password: String)

// アカウント登録用フォーム
object SignupForm {
  // パスワードのパターン: 8桁以上、Ascii 文字列のみ
  val passwordPattern = """^[\x20-\x7e]{8,}$""".r

  val passwordConstraint: Constraint[String] =
    Constraint("constraints.passwordpatterncheck")({ password =>
      passwordPattern.findFirstMatchIn(password) match {
        case Some(_) => Valid
        case None    => Invalid(Seq(ValidationError(
          "無効なパスワードです。8字以上入力してください。"
        )))
      }
    })

  val mappingData = 
    mapping(
      "name"     -> nonEmptyText,
      "email"    -> email,
      "password" -> text.verifying(passwordConstraint)
    )(SignupFormData.apply)(SignupFormData.unapply)

  val form = Form(
    mappingData
  )

  // Form.data: Map[String, String] => SignupFormData
  def createFormDataFromMap(data: Map[String, String]): SignupFormData =
    SignupFormData(
      data("name"),
      data("email"),
      data("password")
    )
}
