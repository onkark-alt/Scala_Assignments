package services

import javax.inject.Inject
import models.User
import repositories.UserRepository
import utils.PasswordUtil
import scala.concurrent.{ExecutionContext, Future}

class AuthService @Inject()(
                             userRepo: UserRepository
                           )(implicit ec: ExecutionContext) {

  /** REGISTER NEW USER **/
  def register(username: String, plainPassword: String, role: String): Future[Long] = {
    val hash = PasswordUtil.hashPassword(plainPassword)

    val user = User(
      id = None,
      username = username,
      hashedPassword = hash,
      role = role
    )

    userRepo.insert(user)
  }

  /** LOGIN USER **/
  def login(username: String, plainPassword: String): Future[Option[User]] = {
    userRepo.findByUsername(username).map {
      case Some(user) if PasswordUtil.verifyPassword(plainPassword, user.hashedPassword) =>
        Some(user)
      case _ =>
        None
    }
  }
}
