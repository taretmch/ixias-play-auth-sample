package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import slick.jdbc.JdbcProfile
import lib.model.{ User, UserPassword }

// UserPasswordRepository: UserPassword テーブルへのクエリ発行を行う Repository 層の定義
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
case class UserPasswordRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[User.Id, UserPassword, P]
  with db.SlickResourceProvider[P] {

  import api._

  /**
    * Get user data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserPasswordTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }

  /**
    * Add user data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(UserPasswordTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update user data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserPasswordTable) { slick =>
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
    RunDBAction(UserPasswordTable) { slick =>
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

