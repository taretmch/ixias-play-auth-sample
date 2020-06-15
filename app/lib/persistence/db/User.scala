package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile

import lib.model.User
import ixias.persistence.model.Table

// UserTable: UserTable へのマッピングを行う
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
case class UserTable[P <:JdbcProfile]()(implicit val driver: P)
  extends Table[User, P] {
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
  class Table(tag: Tag) extends BasicTable(tag, "user"){
    import User._
    // 列名
    /* @1 */ def id        = column[User.Id]       ("id",         O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */ def name      = column[String]        ("name",       O.AsciiChar64)
    /* @3 */ def email     = column[String]        ("email",      O.AsciiChar255)
    /* @4 */ def hash      = column[String]        ("hash",       O.Utf8Char255)
    /* @5 */ def updatedAt = column[LocalDateTime] ("updated_at", O.TsCurrent)
    /* @6 */ def createdAt = column[LocalDateTime] ("created_at", O.Ts)

    type TableElementTuple = (
      Option[User.Id], String, String, String, LocalDateTime, LocalDateTime
    )

    // DB <=> Scala 相互のマッピング定義
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    def * = (id.?, name, email, hash, updatedAt, createdAt) <> (
      // Tuple(table) => Model
      (t: TableElementTuple) => User(
        t._1, t._2, t._3, t._4, t._5, t._6
      ),
      // Model => Tuple(table)
      (v: TableElementType) => User.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, LocalDateTime.now(), t._6
      )}
    )
  }
}
