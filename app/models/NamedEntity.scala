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

trait NamedEntityTrait extends Validatable {
  val name: String
  val displayName0: Option[String]
  val displayName: String = displayName0.map(v => v).getOrElse(name)
  val description: Option[String]
  val enabled: Boolean

  def validate {
    validateMinLength("name", name, 3)
    validateMinLength("displayName", displayName, 3)
  }

}

