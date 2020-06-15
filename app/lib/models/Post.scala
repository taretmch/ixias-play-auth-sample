package lib.model

import ixias.model._
import java.time.LocalDateTime

// Post モデルの定義
import Post._
case class Post(
  id:        Option[Id], // 記事 Id
  uid:       User.Id,    // ユーザー Id
  title:     String,     // 記事のタイトル
  body:      String,     // 記事の本文
  public:    Boolean,    // 公開しているか
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
object Post {
  val  Id = the[Identity[Id]]
  type Id = Long @@ Post

  def apply(uid: User.Id, title: String, body: String, public: Boolean): Post#WithNoId = {
    new Post(
      id     = None,
      uid    = uid,
      title  = title,
      body   = body,
      public = public
    ).toWithNoId
  }
}
