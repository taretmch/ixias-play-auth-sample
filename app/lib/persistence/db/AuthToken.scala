package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.Table
import ixias.play.api.auth.token.Token.AuthenticityToken 
import lib.model.{ AuthToken, User }
import java.time.Duration

// AuthTokenTable: AuthTokenTable へのマッピングを行う
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
case class AuthTokenTable[P <:JdbcProfile]()(implicit val driver: P)
  extends Table[AuthToken, P] {
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
  class Table(tag: Tag) extends BasicTable(tag, "auth_token"){
    import AuthToken._
    // 列名
    /* @1 */ def id        = column[AuthToken.Id]      ("id",         O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */ def uid       = column[User.Id]           ("user_id",    O.UInt64)
    /* @3 */ def token     = column[AuthenticityToken] ("token",      O.Utf8Char255)
    /* @4 */ def expiry    = column[Option[Duration]]  ("expiry",     O.Utf8Char255)
    /* @5 */ def updatedAt = column[LocalDateTime]     ("updated_at", O.TsCurrent)
    /* @6 */ def createdAt = column[LocalDateTime]     ("created_at", O.Ts)

    type TableElementTuple = (
      Option[AuthToken.Id], User.Id, AuthenticityToken, Option[Duration], LocalDateTime, LocalDateTime
    )

    // DB <=> Scala 相互のマッピング定義
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    def * = (id.?, uid, token, expiry, updatedAt, createdAt) <> (
      // Tuple(table) => Model
      (AuthToken.apply _).tupled,
      // Model => Tuple(table)
      (AuthToken.unapply _).andThen(_.map(_.copy(
        _5 = LocalDateTime.now
      )))
    )
  }
}
