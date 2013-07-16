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

trait VehicleTrait extends NamedEntityTrait {
  val model: String
  val licensePlate: String
}

case class Vehicle(private val initName: String, displayName: String, description: Option[String], enabled: Boolean, model: String, licensePlate: String)
  extends VehicleTrait {
  lazy val name = normalizeName(initName)

  def this(name: String, model: String, licensePlate: String) =
    this(name, name, None, true, model, licensePlate)

  override def toString = s"Vehicle($name,$displayName,$enabled,$model)"

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, model: String = this.model, licensePlate: String = this.licensePlate) =
    Vehicle(name, displayName, description, enabled, model, licensePlate)
}

case class VehiclePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, model: String, licensePlate: String, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends VehicleTrait
  with Persisted[VehiclePersisted, Vehicle] {

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, model: String = this.model, licensePlate: String = this.licensePlate) =
      VehiclePersisted(id, name, displayName, description, enabled, model, licensePlate, creationTime, modificationTime, version)

  def delete(): Boolean = ???
  def refetch(): Option[models.VehiclePersisted] = ???
  def update(): Boolean = ???
  def updateWithVersion(): Boolean = ???
}

case class VehiclePersistedRecord private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, model: String, licensePlate: String, creationTime: Timestamp, modificationTime: Timestamp, version: Int) //extends DeviceTrait
{

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, model: String = this.model, licensePlate: String = this.licensePlate) =
    VehiclePersistedRecord(id, name, displayName, description, enabled, model, licensePlate, creationTime, modificationTime, version)

  def delete(): Boolean = ???
  def refetch(): Option[models.DevicePersisted] = ???
  def update(): Boolean = ???
  def updateWithVersion(): Boolean = ???
}

object Vehicles
  extends Table[VehiclePersistedRecord]("vehicles")
  with Backend {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("display_name")
  def description = column[String]("description", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def model = column[String]("model")
  def licensePlate = column[String]("license_plate")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  //def deviceTypeFk = foreignKey("defice_type_fk", deviceTypeId, DeviceTypes)(_.id)
  //def deviceGroupFk = foreignKey("defice_group_fk", deviceGroupId, DeviceGroups)(_.id)

  def * = id ~ name ~ displayName ~ description.? ~ enabled ~ model ~ licensePlate ~ _ctime ~ _mtime ~ _ver <> (VehiclePersistedRecord.apply _, VehiclePersistedRecord.unapply _)

  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = {e => ???}
  
  def delete(obj: models.DevicePersisted): Boolean = deleteById(obj.id)
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"DELETE FROM vehicles WHERE id = ${id}"
    executeDelete("vehicle $id", sql) == 1
  }

  private def recordExtractor(r: VehiclePersistedRecord): VehiclePersisted =
    VehiclePersisted(r.id, r.name, r.displayName, r.description, r.enabled, r.model, r.licensePlate, r.creationTime, r.modificationTime, r.version)

  def findAll: Seq[models.VehiclePersisted] = db withSession {
    val qy = for {
      v <- Vehicles
    } yield v

    qy.list.map(recordExtractor)
  }
  def findAllEnabled: Seq[models.VehiclePersisted] = db withSession {
    val qy = for {
      v <- Vehicles if (v.enabled === true)
    } yield v

    qy.list.map(recordExtractor)
  }
  def findById(id: Int): Option[models.VehiclePersisted] = db withSession {
    val qy = for {
      v <- Vehicles if (v.id === id)
    } yield v

    qy.firstOption.map(recordExtractor)
  }

  def findByName(name: String): Option[models.VehiclePersisted] = db withSession {
    val qy = for {
      v <- Vehicles if (v.name === name)
    } yield v

    qy.firstOption.map(recordExtractor)
  }

  def insert(obj: models.Vehicle): Int = {
    db withSession {
      val sql = sql"""
    INSERT INTO vehicles (
    		name, 
    		display_name,
    		description,
    		enabled, 
    		model,
    		license_plate,
    		_ctime,
    		_mtime,
    		_ver
    ) 
    VALUES (
    		${obj.name},
    		${obj.displayName},
    		${obj.description},
    		${obj.enabled},
    		${obj.model},
    		${obj.licensePlate},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """

      executeSql(BackendOperation.INSERT, s"vehicle $obj", sql.as[Int]) { _.first }
    }
  }
  def update(obj: models.VehiclePersisted): Boolean = db withSession {
      Logger.debug(s"description = ${obj.description}")
      val sql = sqlu"""
   UPDATE vehicles
       SET name = ${obj.name},
           display_name = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
    	   model = ${obj.model},
    	   license_plate = ${obj.licensePlate}, 
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${obj.id}
	 """
      executeUpdate(s"vehicle $obj", sql) == 1
    }
  def updateWithVersion(obj: models.VehiclePersisted): Boolean = db withSession {
      val sql = sqlu"""
    UPDATE vehicles
       SET name = ${obj.name},
           display_name = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
    	   model = ${obj.model},
    	   license_plate = ${obj.licensePlate},
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${obj.id}
	   AND _ver = ${obj.version}
	 """
      executeUpdate(s"vehicle $obj with version check", sql) == 1
    }
}

  
