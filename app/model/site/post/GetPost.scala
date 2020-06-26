package model.site.post

import model.component.auth.ViewValueUser
import model.component.post.ViewValuePost

case class ViewValueGetPost(
  title: String = "ブログ記事",
  user:  ViewValueUser,
  post:  ViewValuePost
)
