package controllers

import lib.model.Post
import lib.persistence.default.PostRepository
import model.component.post.ViewValuePost
import model.site.post._

import play.api.mvc._
import play.api.i18n.I18nSupport

import javax.inject._
import scala.concurrent._
import slick.jdbc._

@Singleton
class PostController @Inject()(
  val controllerComponents: ControllerComponents
) (implicit ec: ExecutionContext) extends BaseController with I18nSupport{

  // 記事一覧の取得
  def index = Action.async { implicit req =>
    for {
      posts <- PostRepository.findAll()
    } yield Ok(views.html.post.PostList(ViewValuePostList(
      posts = posts.map(ViewValuePost(_))
    )))
  }

  // 記事の取得
  def get(id: Long) = Action.async { implicit req =>
    for {
      Some(post) <- PostRepository.get(Post.Id(id))
    } yield Ok(views.html.post.Post(ViewValueGetPost(
      post = ViewValuePost(post)
    )))
  }
}
