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

trait DeviceTypeTrait extends NamedEntityTrait

/**
  * Device Type Model
  */
case class DeviceType(name: String, displayName0: Option[String] = None, description: Option[String] = None, enabled: Boolean = true)
  extends DeviceTypeTrait
  with Model[DeviceTypeTrait]

case class DeviceTypePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends DeviceTypeTrait
  with Persistable[DeviceTypeTrait]

/**
  * DeviceTypes table mapper
  */
object DeviceTypes
  extends Table[DeviceTypePersisted]("device_types")
  with Backend
  with NameEntityCrudOperations[DeviceTypeTrait, DeviceType, DeviceTypePersisted] {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("display_name", O.Nullable)
  def description = column[String]("description", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def * = id ~ name ~ displayName.? ~ description.? ~ enabled ~ _ctime ~ _mtime ~ _ver <> (DeviceTypePersisted, DeviceTypePersisted.unapply _)

  /**
    * Exception mapper
    *
    * Maps a native postgres exception to a ValidationException.
    */
  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("device_types_name_key"))
      throw new ValidationException(e, "name", "Already in use")
    else
      throw e;
  }
  def find(enabled: Option[Boolean] = None): Seq[DeviceTypePersisted] = db withSession {
    val qy = enabled match {
      case None     => for { dt <- DeviceTypes } yield dt
      case Some(en) => for { dt <- DeviceTypes if (dt.enabled === en) } yield dt
    }
    qy.list
  }
  def findById(id: Int): Option[DeviceTypePersisted] = db withSession {
    val qy = for { dt <- DeviceTypes if (dt.id === id) } yield dt
    qy.firstOption
  }
  def findByName(name: String): Option[DeviceTypePersisted] = db withSession {
    val qy = for { dt <- DeviceTypes if (dt.name === name) } yield dt
    qy.firstOption
  }

  def insert(uobj: DeviceType): Int = WithValidation(uobj) { obj =>
    db withSession {

      val sql = sql"""
    INSERT INTO #$tableName (
    		name, 
    		display_name,
    		description, 
    		enabled, 
    		_ctime,
    		_mtime,
    		_ver
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
  def update(uobj: DeviceTypePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
    UPDATE  #$tableName
       SET name = ${obj.name},
    	   display_name = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${obj.id}
	 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def updateWithVersion(uobj: DeviceTypePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
    UPDATE  #$tableName
       SET name = ${obj.name},
    	   display_name = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${obj.id}
	 AND _ver = ${obj.version}
	 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"DELETE FROM #$tableName WHERE id = $id"
    executeDelete("Deleting from #$tableName record identified by $id", sql) == 1
  }
}





