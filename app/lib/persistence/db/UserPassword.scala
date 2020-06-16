package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile

import lib.model.{ User, UserPassword }
import ixias.persistence.model.Table

// UserPasswordTable: UserPasswordTable へのマッピングを行う
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
case class UserPasswordTable[P <:JdbcProfile]()(implicit val driver: P)
  extends Table[UserPassword, P] {
  import api._

  // データソース名の定義
  //~~~~~~~~~~~~~~~~~~~~~
  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/blog"),
    "slave"  -> DataSourceName("ixias.db.mysql://slave/blog" )
  )

  // クエリの定義
  //~~~~~~~~~~~~~
  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  // テーブル定義
  //~~~~~~~~~~~~~
  class Table(tag: Tag) extends BasicTable(tag, "user_password"){
    import UserPassword._
    // 列名
    /* @1 */ def id        = column[User.Id]       ("user_id",    O.UInt64, O.PrimaryKey)
    /* @2 */ def hash      = column[String]        ("hash",       O.Utf8Char255)
    /* @3 */ def updatedAt = column[LocalDateTime] ("updated_at", O.TsCurrent)
    /* @4 */ def createdAt = column[LocalDateTime] ("created_at", O.Ts)

    type TableElementTuple = (
      Option[User.Id], String, LocalDateTime, LocalDateTime
    )

    // DB <=> Scala 相互のマッピング定義
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    def * = (id.?, hash, updatedAt, createdAt) <> (
      // Tuple(table) => Model
      (t: TableElementTuple) => UserPassword(
        t._1, t._2, t._3, t._4
      ),
      // Model => Tuple(table)
      (v: TableElementType) => UserPassword.unapply(v).map { t => (
        t._1, t._2, LocalDateTime.now(), t._4
      )}
    )
  }
}

