package lib

package object persistence {

  val default = onMySQL
  
  object onMySQL {
    implicit lazy val driver = slick.jdbc.MySQLProfile
    object UserRepository         extends UserRepository
    object UserPasswordRepository extends UserPasswordRepository
    object PostRepository         extends PostRepository
  }
}
