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

trait VehicleTypeTrait extends NamedEntityTrait

/**
  * Device Type Model
  */
case class VehicleType(name: String, displayName0: Option[String] = None, description: Option[String] = None, enabled: Boolean = true)
  extends VehicleTypeTrait
  with Model[VehicleTypeTrait]

case class VehicleTypePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends VehicleTypeTrait
  with Persisted[VehicleType]

/**
  * VehicleTypes table mapper
  */
object VehicleTypes
  extends Table[VehicleTypePersisted]("VehicleTypes")
  with Backend
  with NameEntityCrudOperations[VehicleTypeTrait, VehicleType, VehicleTypePersisted] {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("displayName", O.Nullable)
  def description = column[String]("description", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def creationTime = column[Timestamp]("creationTime")
  def modificationTime = column[Timestamp]("modificationTime")
  def version = column[Int]("version")

  def * = id ~ name ~ displayName.? ~ description.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (VehicleTypePersisted, VehicleTypePersisted.unapply _)

  /**
    * Exception mapper
    *
    * Maps a native postgres exception to a ValidationException.
    */
  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("VehicleTypes_name_key"))
      throw new ValidationException(e, "name", "Already in use")
    else
      throw e;
  }
  def find(enabled: Option[Boolean] = None): Seq[VehicleTypePersisted] = db withSession {
    val qy = enabled match {
      case None     => for { dt <- VehicleTypes } yield dt
      case Some(en) => for { dt <- VehicleTypes if (dt.enabled === en) } yield dt
    }
    qy.list
  }
  def findById(id: Int): Option[VehicleTypePersisted] = db withSession {
    val qy = for { dt <- VehicleTypes if (dt.id === id) } yield dt
    qy.firstOption
  }
  def findByName(name: String): Option[VehicleTypePersisted] = db withSession {
    val qy = for { dt <- VehicleTypes if (dt.name === name) } yield dt
    qy.firstOption
  }

  def insert(uobj: VehicleType): Int = WithValidation(uobj) { obj =>
    db withSession {

      val sql = sql"""
    INSERT INTO "#$tableName" (
    		name, 
    		"displayName",
    		description, 
    		enabled, 
    		"creationTime",
    		"modificationTime",
    		version
    ) 
    VALUES (
    		${obj.name},
    		${obj.displayName},
    		${obj.description},
    		${obj.enabled},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """
      executeSql(BackendOperation.INSERT, s"$tableName $obj", sql.as[Int]) { _.first }
    }
  }
  def update(uobj: VehicleTypePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
    UPDATE  "#$tableName"
       SET name = ${obj.name},
    	   "displayName" = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
           "modificationTime" = NOW(),
           version = version + 1 
	 WHERE id = ${obj.id}
	 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def updateWithVersion(uobj: VehicleTypePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
    UPDATE  "#$tableName"
       SET name = ${obj.name},
    	   "displayName" = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
           "modificationTime" = NOW(),
           version = version + 1 
	 WHERE id = ${obj.id}
	 AND version = ${obj.version}
	 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"""DELETE FROM "#$tableName" WHERE id = $id"""
    executeDelete("Deleting from #$tableName record identified by $id", sql) == 1
  }
}




