package models

import play.api.Logger
import play.api.db._
import play.api.Play.current

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

import org.postgresql.util.PSQLException

/**
 * User status control the access to the system
 */
object UserStatus extends Enumeration {
  type UserStatus = Value
  val ACTIVE = Value("active")
  val SUSPENDED = Value("suspended")
  val INACTIVE = Value("inactive")
}
import UserStatus._

object UserSuspensionReason extends Enumeration {
  type UserSuspensionReason = Value
  val TOO_MANY_LOGIN_ATTEMPT = Value("too_many_login_attempt")
}
import UserSuspensionReason._

trait UserTrait {
  
  val login: String
  val displayName: String
  val password: Option[Password]
  val status: UserStatus
  val suspensionReason: Option[UserSuspensionReason]

  /**
   * Verify that the password is correct
   *
   * @param secretPassword is a base64 encoded string of the sha1 of the clear password
   */
  def verifyPassword(secretPassword: String): Boolean =
    password.map(p => p.checkSecretPassword(secretPassword)).getOrElse(false)

  /**
   * Verify that the password is correct
   *
   * @param clearPassword is a base64 encoded string of the sha1 of the clear password
   */
  def verifyClearPassword(clearPassword: String): Boolean =
    password.map(p => p.checkClearPassword(clearPassword)).getOrElse(false)

  /**
   * Verify that the user can login in the system provided the password
   *
   * @param secretPassword is a base64 encoded string of the sha1 of the clear password
   */
  def canLogin(secretPassword: String) =
    status == UserStatus.ACTIVE && verifyPassword(secretPassword: String)
}

/**
 * Represents an User
 *
 * @param initLogin the desired login. Logins are case-insensitive and internally are managed as lowercase strings
 * @param displayName the preferred string used in presentation
 */
case class User(private val initLogin: String, displayName: String, password: Option[Password] = None, status: UserStatus = UserStatus.INACTIVE, suspensionReason: Option[UserSuspensionReason] = None)
  extends UserTrait {
  
  import scala.language.{ implicitConversions, reflectiveCalls }
  
  def this(login: String, password: Option[Password] = None, status: UserStatus = UserStatus.INACTIVE, suspensionReason: Option[UserSuspensionReason] = None) =
    this(login, login, password, status, suspensionReason)

  def this(login: String, password: Password) = this(login, login, Some(password), UserStatus.ACTIVE, None)

  val login = initLogin.toLowerCase

  /**
   * Material implication
   */
  private implicit def extendedBoolean(a: Boolean) = new {
    def implies(b: => Boolean) = !a || b
  }
  require(login.length >= 6, "login must be at least 6 characters")
  require(status == UserStatus.SUSPENDED implies suspensionReason.isDefined, "suspended users requires a reason")
  require(suspensionReason.isDefined implies status == UserStatus.SUSPENDED, "suspeension requires that the user is suspended")

  require(login != "Anonymous", s"""login "${Anonymous.login}" is reserved""")

  override def toString = s"User($login,$displayName,$password,$status,$suspensionReason)"

  def copy(login: String = this.login, displayName: String = this.displayName, password: Option[Password] = this.password, status: UserStatus = this.status, suspensionReason: Option[UserSuspensionReason] = this.suspensionReason) =
    User(login, displayName, password, status, suspensionReason)
}

/**
 * Represents an unauthenticated user
 */
//object Anonymous extends UserTrait 
object Anonymous extends User("Anonymous", None, UserStatus.ACTIVE, None)

//User(login = "Anonymous", None, UserStatus.ACTIVE, None)
/**
 * Represents an User persisted on the backend
 */
case class UserPersisted private[models] (id: Int, login: String, displayName: String, password: Option[Password], status: UserStatus, suspensionReason: Option[UserSuspensionReason], creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends UserTrait
  with Persisted[UserPersisted, User] {

  def copy(displayName: String = this.displayName, password: Option[Password] = this.password, status: UserStatus = this.status, suspensionReason: Option[UserSuspensionReason] = this.suspensionReason) =
      UserPersisted(id, login, displayName, password, status, suspensionReason, creationTime, modificationTime, version)

  /**
   * Save current user without checking the version of the record, that means that other updates
   * occurred between the fetch of the record and this update are silently ignored
   *
   * @return true if the update was executed successfully
   */
  def update() = Users.update(this)

  /**
   * Save current user checking the version of the record, that means that no other updates
   * have been occurred since the fetch of the record
   *
   * @return true if the update was executed successfully
   */
  def updateWithVersion() = Users.updateWithVersion(this)

  /**
   *  Retrieve a refreshed version of the user from the DB
   */
  def refetch() = Users.findById(id)

  def delete() = Users.deleteById(id)
}

object Users
  extends PersistedTable[UserPersisted, User]("users") {
  implicit val statusMapper = MappedTypeMapper.base[UserStatus, String](_.toString, UserStatus.withName(_))
  implicit val suspensionMapper = MappedTypeMapper.base[UserSuspensionReason, String](_.toString, UserSuspensionReason.withName(_))
  implicit val passwordMapper = MappedTypeMapper.base[Password, String](_.secretPassword, SecretPassword(_))

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")
  def displayName = column[String]("display_name")
  def password = column[Password]("password")
  def status = column[UserStatus]("status")
  def suspensionReason = column[UserSuspensionReason]("suspension_reason")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def * = id ~ login ~ displayName ~ password.? ~ status ~ suspensionReason.? ~ _ctime ~ _mtime ~ _ver <> (UserPersisted.apply _, UserPersisted.unapply _)

  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = {e => ???}
  
  // -- Queries

  /**
   * Retrieve a User by id.
   */
  def findById(id: Int): Option[UserPersisted] = db withSession {
    val q = Query(Users).filter(_.id === id)
    Logger.debug(q.selectStatement)
    q.firstOption
  }

  /**
   * Retrieve a User by login.
   */
  def findByLogin(login: String): Option[UserPersisted] = db withSession {
    val q = Query(Users).filter(_.login === login)
    Logger.debug(q.selectStatement)
    q.firstOption
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[UserPersisted] = db withSession { Query(Users).list }

  /**
   * Authenticate a User.
   */
  def authenticate(login: String, password: String): Option[UserPersisted] =
    findByLogin(login).filter(_.canLogin(password))

  /**
   * Check whether the User is member of any of the passed groups
   */
  def isMemberOfAny(groups: Seq[String]): Boolean = ???

  /**
   * Check whether the User is member of all the passed groups
   */
  def isMemberOfAll(groups: Seq[String]): Boolean = ???

  /**
   * Create a User.
   *
   * @return The user id
   */
  def insert(user: User): Int = db withSession {
    val sql = sql"""
    INSERT INTO users (
    		login, 
    		display_name,
    		password, 
    		status, 
    		suspension_reason,
    		_ctime,
    		_mtime,
    		_ver
    ) 
    VALUES (
    		${user.login},
    		${user.displayName},
    		${user.password.map(_.secretPassword)},
    		${user.status.toString},
    		${user.suspensionReason.map(_.toString)},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """

    executeSql(BackendOperation.INSERT, "user $user", sql.as[Int]) { _.first }
  }

  /**
   * Update a user data without checking the version of the record, that means that other updates
   * occurred between the fetch of the record and this update are silently ignored
   *
   * @return true if the update was executed successfully
   */
  def update(user: UserPersisted): Boolean = db withSession {
      val sql = sqlu"""
    UPDATE users
       SET display_name = ${user.displayName},
    	   password = ${user.password.map(_.secretPassword)},
    	   status = ${user.status.toString},
           suspension_reason = ${user.suspensionReason.map(_.toString)},
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${user.id}
	 """
      executeUpdate("user $user", sql) == 1
    }
  /**
   * Update a user checking the version of the record, that means that no other updates
   * have been occurred since the fetch of the record
   *
   * @return true if the update was executed successfully
   */
  def updateWithVersion(user: UserPersisted): Boolean = db withSession {
      val sql = sqlu"""
    UPDATE users
       SET display_name = ${user.displayName},
    	   password = ${user.password.map(_.secretPassword)},
    	   status = ${user.status.toString},
           suspension_reason = ${user.suspensionReason.map(_.toString)},
           _mtime = NOW(),
           _ver = _ver + 1
	 WHERE id = ${user.id}
	   AND _ver = ${user.version}"""
      executeUpdate("user $user with version check", sql) == 1
    }
  /**
   * Delete permanently the user identified by ``id`` from the DB
   *
   * @return true if the delete was executed successfully
   */
  def deleteById(id: Int) = db withSession {
    val sql = sqlu"DELETE FROM users WHERE id = $id"
    executeDelete("user identified by $id", sql) == 1
  }
  /**
   * Delete permanently the user
   *
   * @return true if the delete was executed successfully
   */
  def delete(user: UserPersisted) = deleteById(user.id)
  /**
   * Delete permanently the user
   *
   * @return true if the delete was executed successfully
   */
  def delete(user: User): Boolean = findByLogin(user.login).map(u => deleteById(u.id)).getOrElse(false)

}