package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import slick.jdbc.JdbcProfile
import lib.model.User

// UserRepository: Userテーブルへのクエリ発行を行う Repository 層の定義
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
case class UserRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[User.Id, User, P]
  with db.SlickResourceProvider[P] {

  import api._

  /**
    * Get user data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }

  /**
    * Add user data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(UserTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update user data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserTable) { slick =>
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
   * Delete user data
   */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserTable) { slick =>
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
