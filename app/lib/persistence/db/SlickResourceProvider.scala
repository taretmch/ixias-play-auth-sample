package lib.persistence.db

import slick.jdbc.JdbcProfile

// Tableを扱うResourceのProvider
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
trait SlickResourceProvider[P <: JdbcProfile] {

  implicit val driver: P

  // テーブル定義
  //~~~~~~~~~~~~~
  object PostTable extends PostTable

  // --[ テーブル定義 ] --------------------------------------
  lazy val AllTables = Seq(
    PostTable
  )
}