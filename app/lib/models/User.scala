package lib.model

import ixias.model._
import ixias.security.PBKDF2
import java.time.LocalDateTime

// User モデルの定義
import User._
case class User(
  id:        Option[Id], // ユーザー Id
  name:      String,     // ユーザー名
  email:     String,     // メールアドレス
  hash:      String,     // パスワード
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
object User {
  val  Id = the[Identity[Id]]
  type Id = Long @@ User

  def apply(name: String, email: String, password: String): User#WithNoId = {
    new User(
      id    = None,
      name  = name,
      email = email,
      hash  = hash(password)
    ).toWithNoId
  }

  // パスワードをハッシュ化する
  def hash(password: String): String = PBKDF2.hash(password)

  // パスワードをチェックする
  def verify(input: String, hash: String): Boolean = PBKDF2.compare(input, hash)
}
