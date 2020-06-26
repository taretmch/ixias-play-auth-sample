package controllers

import lib.model.Post
import lib.persistence.default.PostRepository
import model.component.post.ViewValuePost
import model.component.auth.ViewValueUser
import model.site.post._
import mvc.auth.UserAuthProfile

import play.api.mvc._
import play.api.i18n.I18nSupport

import javax.inject._
import scala.concurrent._
import slick.jdbc._
import ixias.play.api.auth.mvc.AuthExtensionMethods

@Singleton
class PostController @Inject()(
  val controllerComponents: ControllerComponents,
  val authProfile:          UserAuthProfile
) (implicit ec: ExecutionContext) extends AuthExtensionMethods with BaseController with I18nSupport {

  // 記事一覧の取得
  def index = Authenticated(authProfile).async { implicit req =>
    authProfile.loggedIn { user =>
      for {
        posts <- PostRepository.findAllByUser(user.id)
      } yield Ok(views.html.post.PostList(ViewValuePostList(
        user  = ViewValueUser.from(user),
        posts = posts.map(ViewValuePost.from)
      )))
    }
  }

  // 記事の取得
  def get(id: Long) = Authenticated(authProfile).async { implicit req =>
    authProfile.loggedIn { user =>
      for {
        Some(post) <- PostRepository.getByUserId(Post.Id(id), user.id)
      } yield Ok(views.html.post.Post(ViewValueGetPost(
        user = ViewValueUser.from(user),
        post = ViewValuePost.from(post)
      )))
    }
  }
}
