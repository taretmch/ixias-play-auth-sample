package lib.model

import ixias.model._
import java.time.LocalDateTime

// User モデルの定義
import User._
case class User(
  id:        Option[Id], // ユーザー Id
  name:      String,     // ユーザー名
  email:     String,     // メールアドレス
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
object User {
  val  Id = the[Identity[Id]]
  type Id = Long @@ User
}
