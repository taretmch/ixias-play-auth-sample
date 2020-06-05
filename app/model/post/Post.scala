package model.post

import lib.model.Post

case class ViewValuePost(
  id:     Long,
  title:  String,
  body:   String,
  public: Boolean
)

object ViewValuePost {
  def apply(post: Post#EmbeddedId): ViewValuePost =
    new ViewValuePost(
      id     = post.id,
      title  = post.v.title,
      body   = post.v.body,
      public = post.v.public
    )
}
