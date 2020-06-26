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

  def from(data: Post#EmbeddedId): ViewValuePost =
    ViewValuePost(
      id     = data.id,
      uid    = data.v.uid,
      title  = data.v.title,
      body   = data.v.body,
      public = data.v.public
    )
}
