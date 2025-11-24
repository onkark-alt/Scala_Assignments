package repositories

import models.{User, UsersTable}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

@Singleton
class UserRepository @Inject()(
                                protected val dbConfigProvider: DatabaseConfigProvider
                              )(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val users = UsersTable.table

  def findByUsername(username: String): Future[Option[User]] =
    db.run(users.filter(_.username === username).result.headOption)

  def insert(user: User): Future[Long] =
    db.run((users returning users.map(_.id)) += user)

  def findById(id: Long): Future[Option[User]] =
    db.run(users.filter(_.id === id).result.headOption)
}
