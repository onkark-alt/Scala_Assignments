package models

import slick.jdbc.MySQLProfile.api._

case class User(
                 id: Option[Long],
                 username: String,
                 hashedPassword: String,
                 role: String
               )

class UsersTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def hashedPassword = column[String]("hashed_password")
  def role = column[String]("role")

  def * = (id.?, username, hashedPassword, role) <> ((User.apply _).tupled, User.unapply)
}

object UsersTable {
  val table = TableQuery[UsersTable]
}
