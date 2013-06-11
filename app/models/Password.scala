package models

import java.security.MessageDigest
import sun.misc.BASE64Encoder

/**
 * Password
 */
trait Password {
  val secretPassword:String
  /**
   * Calculate the password by hashing the clear text with SHA1 and encoding it in Base64
   */
  def calculateSecret(clearPassword: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    val b64enc = new BASE64Encoder
    var bytes = clearPassword.getBytes("UTF-8");
    bytes = md.digest(bytes)
    b64enc.encode(bytes)
  }
  def checkSecretPassword(secretPassword: String) = this.secretPassword == secretPassword
  def checkClearPassword(clearPassword: String) = checkSecretPassword(calculateSecret(clearPassword))
}
/**
 * Represents a clear password
 */
case class ClearPassword(val clearPassword: String) extends Password  {
  require(clearPassword.length >= 8, "password must be at least 8 charaters")
  lazy val secretPassword = calculateSecret(clearPassword)
  override def toString="ClearPassword(***)"
}
/**
 * Represents a secret password
 */
case class SecretPassword(val secretPassword: String) extends Password {
  override def toString="SecretPassword(***)"
}