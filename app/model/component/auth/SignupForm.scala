package model.component.auth

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import scala.util.matching.Regex
import lib.model.{ User, UserPassword }

case class SignupFormData(name: String, email: String, password: String) {

  def createUser(): User#WithNoId = User(None, name, email).toWithNoId

  def createPassword(uid: User.Id): UserPassword#EmbeddedId =
    UserPassword.build(uid, password)
}

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

  val dataMapping = 
    mapping(
      "name"     -> nonEmptyText,
      "email"    -> email,
      "password" -> text.verifying(passwordConstraint)
    )(SignupFormData.apply)(SignupFormData.unapply)

  // Form.data: Map[String, String] => SignupFormData
  def createFormDataFromMap(data: Map[String, String]): SignupFormData =
    SignupFormData(
      data("name"),
      data("email"),
      data("password")
    )
}
