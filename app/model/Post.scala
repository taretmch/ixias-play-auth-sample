package model

import lib.model.Post

case class ViewValuePost(
  id:     Long,
  title:  String,
  body:   String,
  public: Boolean
)

case class ViewValueGetPost(
  title: String = "ブログ記事",
  post:  ViewValuePost
)

case class ViewValuePostList(
  title: String = "ブログ記事一覧",
  posts: Seq[ViewValuePost]
)
