package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

/**
 * Represents a user
 */
case class User(login: String, password: String) {
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
 * @author Giovanni Costagliola
 */
object User {
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.login") ~
    get[String]("user.password") map {
      case login~password => User(login, password)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email
      ).as(User.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve a User from login.
   */
  def findByLogin(login: String): Option[User] = login match {
    case "giovanni" => Some(User("giovanni", "password"))
    case _ => None
  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
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
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (
            {login}, {password}
          )
        """
      ).on(
        'login -> user.login,
        'password -> user.password
      ).executeUpdate()
      
      user
      
    }
  }
}

