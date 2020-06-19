package model.site.post

import model.component.post.ViewValuePost

case class ViewValuePostList(
  title: String = "ブログ記事一覧",
  posts: Seq[ViewValuePost]
)
