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

case class User private[models](id: Option[Int], login: String, status: UserStatus, suspensionReason: Option[UserSuspensionReason]) {
  def this (login: String, status: UserStatus, suspensionReason: Option[UserSuspensionReason]) =
      this (None, login, status, suspensionReason)
  
  /**
   * Material implication
   */
  private implicit def extendedBoolean(a : Boolean) = new {
    def implies(b : => Boolean) = !a || b
  }
  
  require(status == UserStatus.SUSPENDED implies suspensionReason != None)
  
  /**
   * Verify that the password is correct
   */
  def verifyPassword(passwordHash: String): Boolean = true //passwordHash == password

  /**
   * Verify that the user can login in the system provided the password
   */
  def canLogin(passwordHash: String) = verifyPassword(passwordHash: String)
  
  def setStatus(status: UserStatus): User = User(id, login, status, suspensionReason)
  
  def setSuspensionReason(reason: UserSuspensionReason): User = User(id, login, UserStatus.SUSPENDED, Some(reason))
  
  def save() = {
    
  }
}

/**
 * Represents an unauthenticated user
 */
object Anonymous extends User(None, "Anonymous", UserStatus.ACTIVE, None)


object Users extends Table[User]("user") {
  val db = Database.forDataSource(DB.getDataSource())
  implicit val statusMapper = MappedTypeMapper.base[UserStatus, String] (_.toString, UserStatus.withName(_)) 
  implicit val suspensionMapper = MappedTypeMapper.base[UserSuspensionReason, String] (_.toString, UserSuspensionReason.withName(_))
  
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")
  def status = column[UserStatus]("status")
  def suspensionReason = column[UserSuspensionReason]("suspension_reason")
  
  def * = id.? ~ login ~ status ~ suspensionReason.? <> (User, User.unapply _)
  def forInsert = login ~ status ~ suspensionReason.? <> (
      { t => User(None, t._1, t._2, t._3)}, 
      { (u: User) => Some((u.login, u.status, u.suspensionReason))})
  
  // -- Queries

  /**
   * Retrieve a User from login.
   */
  def findByLogin(login: String): Option[User] = {
    db withSession {
      /*
      implicit val getResult = SELECT_*
      sql"SELECT user_id, login, password FROM security.user WHERE login = $login".as[User].firstOption
      */
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