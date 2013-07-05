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

trait DriverTrait extends Validatable {
  val name: String
  val surname: String
  val enabled: Boolean

  def validate = {
    Nil
  }
}

case class Driver(val name: String, val surname: String, val enabled: Boolean)
  extends DriverTrait
  with ValidationRequired

case class DriverPersisted(val id: Int, val name: String, val surname: String, val enabled: Boolean, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends DriverTrait
  with Persistable

object Drivers
  extends Table[DriverPersisted]("drivers")
  with Backend {

  def * = ???
}
	
	
