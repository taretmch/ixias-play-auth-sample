package model.component.post

import lib.model.Post

case class ViewValuePost(
  id:     Long,
  uid:    Long,
  title:  String,
  body:   String,
  public: Boolean
)

object ViewValuePost {
  def apply(post: Post#EmbeddedId): ViewValuePost =
    new ViewValuePost(
      id     = post.id,
      uid    = post.v.uid,
      title  = post.v.title,
      body   = post.v.body,
      public = post.v.public
    )
}
