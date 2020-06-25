package lib.model

import model.component.auth.FormErrors
import play.api.data.FormError
import ixias.model._
import ixias.security.PBKDF2
import java.time.LocalDateTime

// UserPassword モデルの定義
import UserPassword._
case class UserPassword(
  id:        Option[User.Id], // ユーザー Id
  hash:      String,          // パスワード
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[User.Id]

// コンパニオンオブジェクト
object UserPassword {
  def apply(id: User.Id, password: String): UserPassword#WithNoId = {
    new UserPassword(
      id   = Some(id),
      hash = hash(password)
    ).toWithNoId
  }

  // パスワードをハッシュ化する
  def hash(password: String): String = PBKDF2.hash(password)

  // パスワードをチェックする
  def verify(input: String, hash: String): Either[FormError, Boolean] =
    PBKDF2.compare(input, hash) match {
      case true  => Right(true)
      case false => Left(FormErrors.errorPasswordInvalid)
    }
}
