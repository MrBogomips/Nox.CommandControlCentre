package models

import patterns.models._
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

trait UserTrait extends Validatable {
  import scala.language.{ implicitConversions, reflectiveCalls }

  val login0: String
  val login: String = login0.toLowerCase()
  val displayName0: Option[String]
  val displayName: String = displayName0.map(v => v).getOrElse(login)
  val password: Option[Password]
  val status: UserStatus
  val suspensionReason: Option[UserSuspensionReason]

  def validate {
    validateMinLength("login", login, 6)
    if (login == Anonymous.login)
      addValidationError("login", s"""login "${Anonymous.login}" is reserved""")
    if (status == UserStatus.SUSPENDED implies suspensionReason.isDefined)
      addValidationError("status", "suspended users requires a reason")
    if (suspensionReason.isDefined implies status == UserStatus.SUSPENDED)
      addValidationError("suspensionReason", "suspension requires that the user is suspended")
  }

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
case class User(login0: String, displayName0: Option[String], password: Option[Password] = None, status: UserStatus = UserStatus.INACTIVE, suspensionReason: Option[UserSuspensionReason] = None)
  extends UserTrait
  with Model[UserTrait] {

  def this(login: String, password: Option[Password] = None, status: UserStatus = UserStatus.INACTIVE, suspensionReason: Option[UserSuspensionReason] = None) =
    this(login, Some(login), password, status, suspensionReason)

  def this(login: String, password: Password) = this(login, Some(login), Some(password), UserStatus.ACTIVE, None)

  override def toString = s"User($login,$displayName,$password,$status,$suspensionReason)"
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
case class UserPersisted(id: Int, login0: String, displayName0: Option[String], password: Option[Password], status: UserStatus, suspensionReason: Option[UserSuspensionReason], creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends UserTrait
  with Persisted[User] {

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
  extends Table[UserPersisted]("Users")
  with Backend
  with NameEntityCrudOperations[UserTrait, User, UserPersisted] {
  implicit val statusMapper = MappedTypeMapper.base[UserStatus, String](_.toString, UserStatus.withName(_))
  implicit val suspensionMapper = MappedTypeMapper.base[UserSuspensionReason, String](_.toString, UserSuspensionReason.withName(_))
  implicit val passwordMapper = MappedTypeMapper.base[Password, String](_.secretPassword, SecretPassword(_))

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")
  def displayName = column[String]("displayName")
  def password = column[Password]("password")
  def status = column[UserStatus]("status")
  def suspensionReason = column[UserSuspensionReason]("suspensionReason")
  def creationTime = column[Timestamp]("creationTime")
  def modificationTime = column[Timestamp]("modificationTime")
  def version = column[Int]("version")

  def * = id ~ login ~ displayName.? ~ password.? ~ status ~ suspensionReason.? ~ creationTime ~ modificationTime ~ version <> (UserPersisted.apply _, UserPersisted.unapply _)

  /**
    * Exception mapper
    *
    * Maps a native postgres exception to a ValidationException.
    */
  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("users_login_key"))
      throw new ValidationException(e, "login", "Already in use")
    else
      throw e;
  }
  // -- Queries

  /**
    * Retrieve a User by id.
    */
  def findById(id: Int): Option[UserPersisted] = db withSession {
    val qy = for { u <- Users if (u.id === id) } yield u
    qy.firstOption
  }

  /**
    * Retrieve a User by login.
    */
  def findByLogin(login: String): Option[UserPersisted] = db withSession {
    val qy = for { u <- Users if (u.login === login) } yield u
    qy.firstOption
  }
  def findByName(login: String): Option[UserPersisted] = findByLogin(login)

  def find(enabled: Option[Boolean] = None): Seq[UserPersisted] = db withSession {
    val qy = enabled match {
      case None     => for { u <- Users } yield u
      case Some(en) => for { u <- Users if (u.status === UserStatus.ACTIVE) } yield u
    }
    qy.list
  }

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
  def insert(uuser: User): Int = WithValidation(uuser) { user =>
    db withSession {
      val sql = sql"""
    INSERT INTO #$tableName (
    		login, 
    		displayName,
    		password, 
    		status, 
    		suspensionReason,
    		creationTime,
    		modificationTime,
    		version
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
  }

  /**
    * Update a user data without checking the version of the record, that means that other updates
    * occurred between the fetch of the record and this update are silently ignored
    *
    * @return true if the update was executed successfully
    */
  def update(uuser: UserPersisted): Boolean = WithValidation(uuser) { user =>
    db withSession {
      val sql = sqlu"""
    UPDATE #$tableName
       SET displayName = ${user.displayName},
    	   password = ${user.password.map(_.secretPassword)},
    	   status = ${user.status.toString},
           suspensionReason = ${user.suspensionReason.map(_.toString)},
           modificationTime = NOW(),
           version = version + 1 
	 WHERE id = ${user.id}
	 """
      executeUpdate("user $user", sql) == 1
    }
  }
  /**
    * Update a user checking the version of the record, that means that no other updates
    * have been occurred since the fetch of the record
    *
    * @return true if the update was executed successfully
    */
  def updateWithVersion(uuser: UserPersisted): Boolean = WithValidation(uuser) { user =>
    db withSession {
      val sql = sqlu"""
    UPDATE #$tableName
       SET displayName = ${user.displayName},
    	   password = ${user.password.map(_.secretPassword)},
    	   status = ${user.status.toString},
           suspensionReason = ${user.suspensionReason.map(_.toString)},
           modificationTime = NOW(),
           version = version + 1
	 WHERE id = ${user.id}
	   AND version = ${user.version}"""
      executeUpdate("user $user with version check", sql) == 1
    }
  }
  /**
    * Delete permanently the user identified by ``id`` from the DB
    *
    * @return true if the delete was executed successfully
    */
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"DELETE FROM #$tableName WHERE id = $id"
    executeDelete("Deleting from #$tableName record identified by $id", sql) == 1
  }
}