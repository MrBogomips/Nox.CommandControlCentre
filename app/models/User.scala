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

import models.Backend

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

case class User private[models] (id: Option[Int], login: String, password: Option[Password], status: UserStatus, suspensionReason: Option[UserSuspensionReason], recordInfo: Option[RecordInfo])
  extends MaybePersisted {
  def this(login: String, password: Option[Password] = None) =
    this(None, login, password, UserStatus.INACTIVE, None, None)
  def this(login: String, password: Option[Password], status: UserStatus, suspensionReason: Option[UserSuspensionReason]) =
    this(None, login, password, status, suspensionReason, None)

  /**
   * Material implication
   */
  private implicit def extendedBoolean(a: Boolean) = new {
    def implies(b: => Boolean) = !a || b
  }

  require(login.length >= 6, "login must be at least 6 characters")
  require(status == UserStatus.SUSPENDED implies suspensionReason.isDefined, "suspended users requires a reason")
  require(suspensionReason.isDefined implies status == UserStatus.SUSPENDED, "suspeension requires that the user is suspended")

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
  /**
   * Returns a new user object with the status updated
   */
  def setStatus(status: UserStatus): User =
    User(id, login, password, status, suspensionReason, recordInfo)

  /**
   * Returns a new user object with the suspension reason updated
   */
  def setSuspensionReason(reason: UserSuspensionReason): User =
    User(id, login, password, UserStatus.SUSPENDED, Some(reason), recordInfo)
  /**
   * Returns a new user with the password changed
   */
  def setPassword(clearPassword: String): User =
    User(id, login, Some(ClearPassword(clearPassword)), status, suspensionReason, recordInfo)

  /**
   * Save current user without checking the version of the record, that means that other updates
   * occurred between the fetch of the record and this update are silently ignored
   *
   * @return true if the update was executed successfully
   */
  def save() = Users.update(this)

  /**
   * Save current user checking the version of the record, that means that no other updates
   * have been occurred since the fetch of the record
   *
   * @return true if the update was executed successfully
   */
  def saveWithVersion() = Users.updateWithVersion(this)

  /**
   *  Retrieve a refreshed version of the user from the DB
   */
  def refetch() = {
    requirePersistance
    Users.findById(id.get)
  }
}

/**
 * Represents an unauthenticated user
 */
object Anonymous extends User(None, "Anonymous", None, UserStatus.ACTIVE, None, None)

object Users extends Table[User]("users") with Backend {
  implicit val statusMapper = MappedTypeMapper.base[UserStatus, String](_.toString, UserStatus.withName(_))
  implicit val suspensionMapper = MappedTypeMapper.base[UserSuspensionReason, String](_.toString, UserSuspensionReason.withName(_))
  implicit val passwordMapper = MappedTypeMapper.base[Password, String](_.secretPassword, SecretPassword(_))

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")
  def password = column[Password]("password")
  def status = column[UserStatus]("status")
  def suspensionReason = column[UserSuspensionReason]("suspension_reason")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def * = id.? ~ login ~ password.? ~ status ~ suspensionReason.? ~ _ctime ~ _mtime ~ _ver <> (
    { t => User(t._1, t._2, t._3, t._4, t._5, Some(RecordInfo(t._6, t._7, t._8))) },
    { (u: User) => Some((u.id, u.login, u.password, u.status, u.suspensionReason, u.recordInfo.get.creationTime, u.recordInfo.get.modificationTime, u.recordInfo.get.version)) } //User, User.unapply _
    )
  def forInsert = login ~ password.? ~ status ~ suspensionReason.? <> (
    { t => User(None, t._1, t._2, t._3, t._4, None) },
    { (u: User) => Some((u.login, u.password, u.status, u.suspensionReason)) })

  // -- Queries

  /**
   * Retrieve a User by id.
   */
  def findById(id: Int): Option[User] = db withSession {
    val q = Query(Users).filter(_.id === id)
    Logger.debug(q.selectStatement)
    q.firstOption
  }

  /**
   * Retrieve a User by login.
   */
  def findByLogin(login: String): Option[User] = db withSession {
    val q = Query(Users).filter(_.login === login)
    Logger.debug(q.selectStatement)
    q.firstOption
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = db withSession { Query(Users).list }

  /**
   * Authenticate a User.
   */
  def authenticate(login: String, password: String): Option[User] =
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
  def create(user: User): Int = db withSession {
    val q = Users.forInsert returning Users.id
    Logger.debug(q.insertStatement)
    q insert user
  }
  /**
   * Create a user
   *
   * @param login the login name
   * @param password the password in clear
   * @param status the initial status of the user (default: INACTIVE)
   *
   * @return The user id
   */
  def create(login: String, password: String, status: UserStatus = UserStatus.INACTIVE): Int = {
    val u = new User(login, Some(ClearPassword(password)), status, None)
    create(u)
  }
  /**
   * Update a user data without checking the version of the record, that means that other updates
   * occurred between the fetch of the record and this update are silently ignored
   *
   * @return true if the update was executed successfully
   */
  def update(user: User): Boolean = db withSession {
    user.requirePersistance

    val updateQuery = sqlu"""
    UPDATE "users"
       SET "password" = ${user.password.map(_.secretPassword)},
    	   "status" = ${user.status.toString},
           "suspension_reason" = ${user.suspensionReason.map(_.toString)},
           "_mtime" = NOW(),
           "_ver" = "_ver" + 1
	 WHERE "id" = ${user.id}"""
    Logger.debug(updateQuery.getStatement)
    updateQuery.first == 1
  }
  /**
   * Update a user checking the version of the record, that means that no other updates
   * have been occurred since the fetch of the record
   *
   * @return true if the update was executed successfully
   */
  def updateWithVersion(user: User): Boolean = db withSession {
    user.requirePersistance

    val updateQuery = sqlu"""
    UPDATE "users"
       SET "password" = ${user.password.map(_.secretPassword)},
    	   "status" = ${user.status.toString},
           "suspension_reason" = ${user.suspensionReason.map(_.toString)},
           "_mtime" = NOW(),
           "_ver" = "_ver" + 1
	 WHERE "id" = ${user.id}
	   AND "_ver" = ${user.recordInfo.get.version}"""
    Logger.debug(updateQuery.getStatement)
    updateQuery.first == 1
  }
}