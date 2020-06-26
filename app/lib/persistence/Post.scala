package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import slick.jdbc.JdbcProfile
import lib.model.{ Post, User }

// PostRepository: Post テーブルへのクエリ発行を行う Repository 層の定義
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
case class PostRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[Post.Id, Post, P]
  with db.SlickResourceProvider[P] {

  import api._

  /**
   * Get post dataset by user id
   */
  def findAllByUser(uid: User.Id): Future[Seq[EntityEmbeddedId]] =
    RunDBAction(PostTable, "slave") { _
      .filter(_.uid === uid)
      .result
    }

  /**
    * Get post data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(PostTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }

  /**
   * Get post data by user id
   */
  def getByUserId(id: Id, uid: User.Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(PostTable, "slave") { _
      .filter(_.id === id)
      .filter(_.uid === uid)
      .result.headOption
    }

  /**
    * Add post data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(PostTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update post data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(PostTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.update(entity.v)
        }
      } yield old
    }

  /**
   * Delete post data
   */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(PostTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }
}
