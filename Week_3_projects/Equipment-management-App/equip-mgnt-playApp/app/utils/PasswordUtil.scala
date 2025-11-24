package utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtil {

  def hashPassword(plain: String): String =
    BCrypt.hashpw(plain, BCrypt.gensalt())

  def verifyPassword(plain: String, hashed: String): Boolean =
    BCrypt.checkpw(plain, hashed)
}
