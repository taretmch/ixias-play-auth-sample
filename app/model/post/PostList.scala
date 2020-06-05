package model.post

case class ViewValuePostList(
  title: String = "ブログ記事一覧",
  posts: Seq[ViewValuePost]
)
