package model.site.post

import model.component.auth.ViewValueUser
import model.component.post.ViewValuePost

case class ViewValuePostList(
  title: String = "ブログ記事一覧",
  user:  ViewValueUser,
  posts: Seq[ViewValuePost]
)
