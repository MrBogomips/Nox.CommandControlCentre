package models

import play.api.db._
import play.api.Play.current

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

/**
 * Represents a user
 */
case class User private[models] (user_id: Int, login: String, password: String) {
  /**
   * Verify that the password is correct
   */
  def verifyPassword(passwordHash: String) = passwordHash == password

  /**
   * Verify that the user can login in the system provided the password
   */
  def canLogin(passwordHash: String) = verifyPassword(passwordHash: String)
}
/**
 * Represents an unauthenticated user
 */
object Anonymous extends User(-1, "Anonymous", "")

/**
 * @author Giovanni Costagliola
 */
object User {

  private val SELECT_* = GetResult(r => User(
    r.nextInt,				// user_id
    r.nextString,			// login
    r.nextString			// password
  ))

  // -- Parsers

  /**
   * Parse a User from a ResultSet
   */
  /*
  val simple = {
    get[Int]("user.user_id") ~
    get[String]("user.login") ~
    get[String]("user.password") map {
      case user_id~login~password => User(user_id, login, password)
    }
  }
  * */

  // -- Queries

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    Database.forDataSource(DB.getDataSource()) withSession {
      implicit val getResult = SELECT_*
      sql"SELECT user_id, login, password FROM security.user WHERE login = $email".as[User].firstOption
    }
  }

  /**
   * Retrieve a User from login.
   */
  def findByLogin(login: String): Option[User] = login match {
    case "giovanni" => Some(User(1, "giovanni", "password"))
    case _ => None
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    /*
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
    *
    */
    ???
  }

  /**
   * Authenticate a User.
   */
  def authenticate(login: String, password: String): Option[User] =
    findByLogin(login).filter(_.canLogin(password))

  /**
   * Check whether the User is member of any of the passed groups
   */
  def isMemberOfAny(groups: Seq[String]): Boolean = {
    ???
  }

  /**
   * Check whether the User is member of all the passed groups
   */
  def isMemberOfAll(groups: Seq[String]): Boolean = {
    ???
  }

  /**
   * Create a User.
   */
  def create(user: User): User = {
    ???
    /*
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (
            {login}, {password}
          )
        """).on(
          'login -> user.login,
          'password -> user.password).executeUpdate()

      user

    }
    * 
    */
  }
  /**
   * Create a user
   *
   * @param login the login name
   * @param password the password in clear
   */
  def create(login: String, password: String): User = {
    User(0, login, password)
  }
}

