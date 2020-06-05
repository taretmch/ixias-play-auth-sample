package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile

import lib.model.Post
import ixias.persistence.model.Table

// PostTable: PostTable へのマッピングを行う
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
case class PostTable[P <:JdbcProfile]()(implicit val driver: P)
  extends Table[Post, P] {
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
  class Table(tag: Tag) extends BasicTable(tag, "post"){
    import Post._
    // 列名
    /* @1 */ def id        = column[Post.Id]       ("id",          O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */ def title     = column[String]        ("title",       O.Utf8Char255)
    /* @3 */ def body      = column[String]        ("body",        O.Utf8Char255)
    /* @4 */ def public    = column[Boolean]       ("public",      O.Boolean)
    /* @5 */ def updatedAt = column[LocalDateTime] ("updated_at",  O.TsCurrent)
    /* @6 */ def createdAt = column[LocalDateTime] ("created_at",  O.Ts)

    type TableElementTuple = (
      Option[Post.Id], String, String, Boolean, LocalDateTime, LocalDateTime
    )

    // DB <=> Scala 相互のマッピング定義
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    def * = (id.?, title, body, public, updatedAt, createdAt) <> (
      // Tuple(table) => Model
      (t: TableElementTuple) => Post(
        t._1, t._2, t._3, t._4, t._5, t._6
      ),
      // Model => Tuple(table)
      (v: TableElementType) => Post.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, LocalDateTime.now(), t._6
      )}
    )
  }
}
