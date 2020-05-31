package lib.model

import ixias.model._
import ixias.util.EnumStatus
import java.time.LocalDateTime

// Post モデルの定義
import Post._
case class Post(
  id:        Option[Id],
  title:     String,
  body:      String,
  public:    Boolean,
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
object Post {
  
  val  Id         = the[Identity[Id]]
  type Id         = Long @@ Post
  type WithNoId   = Entity.WithNoId[Id, Post]
  type EmbeddedId = Entity.EmbeddedId[Id, Post]

  def apply(title: String, body: String, public: Boolean): WithNoId = {
    new Entity.WithNoId(
      new Post(
        title  = title,
        body   = body,
        public = public
      )
    )
  }
}
