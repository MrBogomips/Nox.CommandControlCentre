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

trait DriverTrait extends PersonTrait

case class Driver(name: String, surname: String, displayName0: Option[String], enabled: Boolean)
  extends DriverTrait
  with Model[DriverTrait] {
  def this(name: String, surname: String, enabled: Boolean = true) = 
    this(name, surname, None, enabled)
}

case class DriverPersisted(id: Int, name: String, surname: String, displayName0: Option[String], enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends DriverTrait
  with Persisted[Driver]

object Drivers
  extends EnabledEntityCrudTable[DriverTrait, Driver, DriverPersisted]("Drivers") {

  def name = column[String]("name")
  def surname = column[String]("surname")
  def displayName = column[String]("displayName")

  def * = id ~ name ~ surname ~ displayName.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (DriverPersisted, DriverPersisted.unapply _)
  def forInsert = name ~ surname ~ displayName.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => Driver(t._1, t._2, t._3, t._4) },
    { (o: Driver) =>
      {
        import java.util.Date
        val now = new Timestamp(new Date().getTime())
        Some((o.name, o.surname, Some(o.displayName), o.enabled, now, now, 0))
      }
    })
  
  override implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    //if (errMessage.contains("vehicle_name_key"))
    //  throw new ValidationException(e, "name", "Already in use")
    //else
      throw e;
  }
  
  def qyFindById(id: Int) = (for { d <- Drivers if (d.id === id)} yield d)
  
    // TODO reimplement avoiding explicit SQL
  def update(obj: DriverPersisted): Boolean =  WithValidation(obj) { vobj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET name = ${vobj.name},
      		   surname = ${vobj.surname},
	           "displayName" = ${vobj.displayName},
	    	   enabled = ${vobj.enabled},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${vobj.id}
		 """
      executeUpdate(s"driver $vobj", sql) == 1
    }
  }
    // TODO reimplement avoiding explicit SQL  
  def updateWithVersion(obj: DriverPersisted): Boolean =  WithValidation(obj) { vobj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET name = ${vobj.name},
      		   surname = ${vobj.surname},
	           "displayName" = ${vobj.displayName},
	    	   enabled = ${vobj.enabled},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${vobj.id}
		   AND version = ${vobj.version}
		 """
      executeUpdate(s"driver $vobj", sql) == 1
    }
  }
  
}
	
	
