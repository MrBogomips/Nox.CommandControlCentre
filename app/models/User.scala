package models

import play.api.Logger
import play.api.db._
import play.api.Play.current

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation


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


case class User private[models](id: Option[Int], login: String, password: Option[Password], status: UserStatus, suspensionReason: Option[UserSuspensionReason]) {
  def this (login: String, password: Option[Password], status: UserStatus, suspensionReason: Option[UserSuspensionReason]) =
      this (None, login, password, status, suspensionReason)
  
  /**
   * Material implication
   */
  private implicit def extendedBoolean(a : Boolean) = new {
    def implies(b : => Boolean) = !a || b
  }
  
  require(login.length >= 6, "login must be at least 6 characters")
  require(status == UserStatus.SUSPENDED implies suspensionReason != None)
  
  /**
   * Verify that the password is correct
   * 
   * @param secretPassword is a base64 encoded string of the sha1 of the clear password
   */
  def verifyPassword(secretPassword: String): Boolean = 
    password.map( p => p.checkSecretPassword(secretPassword) ).getOrElse(false)

  /**
   * Verify that the user can login in the system provided the password
   * 
   * @param secretPassword is a base64 encoded string of the sha1 of the clear password
   */
  def canLogin(secretPassword: String) = 
    status == UserStatus.ACTIVE && verifyPassword(secretPassword: String)
  
  def setStatus(status: UserStatus): User = User(id, login, password, status, suspensionReason)
  
  def setSuspensionReason(reason: UserSuspensionReason): User = User(id, login, password, UserStatus.SUSPENDED, Some(reason))
  
  def save() = {
    
  }
}

/**
 * Represents an unauthenticated user
 */
object Anonymous extends User(None, "Anonymous", None, UserStatus.ACTIVE, None)


object Users extends Table[User]("users") {
  val db = Database.forDataSource(DB.getDataSource())
  implicit val statusMapper = MappedTypeMapper.base[UserStatus, String] (_.toString, UserStatus.withName(_)) 
  implicit val suspensionMapper = MappedTypeMapper.base[UserSuspensionReason, String] (_.toString, UserSuspensionReason.withName(_))
  implicit val passwordMapper = MappedTypeMapper.base[Password, String] (_.secretPassword, SecretPassword(_))
  
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")
  def password = column[Password]("password")
  def status = column[UserStatus]("status")
  def suspensionReason = column[UserSuspensionReason]("suspension_reason")
  
  def * = id.? ~ login ~ password.? ~ status ~ suspensionReason.? <> (User, User.unapply _)
  def forInsert = login ~ password.? ~ status ~ suspensionReason.? <> (
      { t => User(None, t._1, t._2, t._3, t._4)}, 
      { (u: User) => Some((u.login, u.password, u.status, u.suspensionReason))})
  
  // -- Queries

  /**
   * Retrieve a User from login.
   */
  def findByLogin(login: String): Option[User] = {
    db withSession {
      val q = Query(Users).filter(_.login === login)
      Logger.debug(q.selectStatement)
      q.firstOption
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = db withSession {Query(Users).list}

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
   */
  def create(login: String, password: String): User = {
    //User(0, login, password)
    Anonymous
  }
}