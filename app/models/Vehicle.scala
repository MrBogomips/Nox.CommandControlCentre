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

trait VehicleTrait extends NamedEntityTrait {
  val model: String
  val licensePlate: String
  val vehicleTypeId: Option[Int]

  override def validate {
    super.validate
    validateMinLength("model", model, 3)
    validateMinLength("licensePlate", licensePlate, 3)
  }
}

case class Vehicle(name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, model: String, licensePlate: String, vehicleTypeId: Option[Int])
  extends VehicleTrait
  with Model[VehicleTrait] {

  def this(name: String, model: String, licensePlate: String, vehicleTypeId: Option[Int] = None) =
    this(name, Some(name), None, true, model, licensePlate, vehicleTypeId)
}

case class VehiclePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, model: String, licensePlate: String, vehicleTypeId: Option[Int], creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends VehicleTrait
  with Persisted[Vehicle]

object Vehicles
  extends Table[VehiclePersisted]("Vehicles")
  with Backend
  with NameEntityCrudOperations[VehicleTrait, Vehicle, VehiclePersisted] {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("displayName")
  def description = column[String]("description", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def model = column[String]("model")
  def licensePlate = column[String]("licensePlate")
  def vehicleTypeId = column[Int]("vehicleTypeId", O.Nullable)
  def creationTime = column[Timestamp]("creationTime")
  def modificationTime = column[Timestamp]("modificationTime")
  def version = column[Int]("version")

  //def deviceTypeFk = foreignKey("defice_type_fk", deviceTypeId, DeviceTypes)(_.id)
  //def deviceGroupFk = foreignKey("defice_group_fk", deviceGroupId, DeviceGroups)(_.id)

  def * = id ~ name ~ displayName.? ~ description.? ~ enabled ~ model ~ licensePlate ~ vehicleTypeId.? ~ creationTime ~ modificationTime ~ version <> (VehiclePersisted.apply _, VehiclePersisted.unapply _)

  /**
    * Exception mapper
    *
    * Maps a native postgres exception to a ValidationException.
    */
  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("vehicle_name_key"))
      throw new ValidationException(e, "name", "Already in use")
    else
      throw e;
  }

  def find(enabled: Option[Boolean] = None): Seq[VehiclePersisted] = db withSession {
    val qy = enabled match {
      case None     => for { v <- Vehicles } yield v
      case Some(en) => for { v <- Vehicles if (v.enabled === en) } yield v
    }
    qy.list
  }
  def findById(id: Int): Option[VehiclePersisted] = db withSession {
    val qy = for { v <- Vehicles if (v.id === id) } yield v
    qy.firstOption
  }
  def findByName(name: String): Option[VehiclePersisted] = db withSession {
    val qy = for { v <- Vehicles if (v.name === name) } yield v
    qy.firstOption
  }

  def insert(uobj: Vehicle): Int = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sql"""
    INSERT INTO "#$tableName" (
    		name, 
    		"displayName",
    		description,
    		enabled, 
    		model,
    		"licensePlate",
    		"vehicleTypeId",
    		"creationTime",
    		"modificationTime",
    		version
    ) 
    VALUES (
    		${obj.name},
    		${obj.displayName},
    		${obj.description},
    		${obj.enabled},
    		${obj.model},
    		${obj.licensePlate},
    		${obj.vehicleTypeId},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """

      executeSql(BackendOperation.INSERT, s"vehicle $obj", sql.as[Int]) { _.first }
    }
  }
  def update(uobj: models.VehiclePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      Logger.debug(s"description = ${obj.description}")
      val sql = sqlu"""
   UPDATE "#$tableName"
       SET name = ${obj.name},
           "displayName" = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
    	   model = ${obj.model},
    	   "licensePlate" = ${obj.licensePlate}, 
    	   "vehicleTypeId" = ${obj.vehicleTypeId}, 
           "modificationTime" = NOW(),
           version = version + 1 
	 WHERE id = ${obj.id}
	 """
      executeUpdate(s"vehicle $obj", sql) == 1
    }
  }
  def updateWithVersion(uobj: models.VehiclePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
    UPDATE "#$tableName"
       SET name = ${obj.name},
           "displayName" = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
    	   model = ${obj.model},
    	   "licensePlate" = ${obj.licensePlate},
    	   "vehicleTypeId" = ${obj.vehicleTypeId}, 
           "modificationTime" = NOW(),
           version = version + 1 
	 WHERE id = ${obj.id}
	   AND version = ${obj.version}
	 """
      executeUpdate(s"vehicle $obj with version check", sql) == 1
    }
  }
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"""DELETE FROM "#$tableName" WHERE id = $id"""
    executeDelete("Deleting from #$tableName record identified by $id", sql) == 1
  }
}

  
