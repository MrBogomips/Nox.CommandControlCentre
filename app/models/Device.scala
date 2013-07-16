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

import org.joda.time.DateTime

import utils.Converter._

trait DeviceTrait extends Validatable {
  val name: String
  val displayName0: Option[String]
  val displayName: String = displayName0.map(v => v).getOrElse(name)
  val description: Option[String]
  val deviceTypeId: Int
  val deviceGroupId: Int
  val vehicleId: Option[Int]
  val enabled: Boolean
  val imei: Option[String]

  def validate {
    validateMinLength("name", name, 3)
    validateMinLength("displayName", displayName, 3)
    validateMinValue("deviceTypeId", deviceTypeId, 1)
    validateMinValue("deviceGroupId", deviceGroupId, 1)
    vehicleId.map(v => validateMinValue("vehicleId", v, 1))
    imei.map(v => validateLength("imei", v, 15))
    
  }
}

trait DeviceInfoTrait extends DeviceTrait {
  val deviceTypeDisplayName: String
  val deviceGroupDisplayName: String
  val vehicleDisplayName: Option[String]
  val vehicleLicensePlate: Option[String]
}

case class Device(name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, imei: Option[String])
  extends DeviceTrait
  with ValidationRequired

case class DevicePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, imei: Option[String], creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends DeviceTrait
  with Persistable {
  def this(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, imei: Option[String]) =
    this(id, name, displayName0, description, deviceTypeId, deviceGroupId, vehicleId, enabled, imei, new Timestamp(0), new Timestamp(0), 0)
  def this(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, imei: Option[String], version: Int) =
    this(id, name, displayName0, description, deviceTypeId, deviceGroupId, vehicleId, enabled, imei, new Timestamp(0), new Timestamp(0), version)
}

case class DeviceInfoPersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, imei: Option[String], creationTime: Timestamp, modificationTime: Timestamp, version: Int, deviceTypeDisplayName: String, deviceGroupDisplayName: String, vehicleDisplayName: Option[String], vehicleLicensePlate: Option[String])
  extends DeviceInfoTrait
  with Persistable

object Devices
  extends Table[DevicePersisted]("devices")
  with Backend {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("display_name")
  def description = column[String]("description", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def imei = column[String]("imei", O.Nullable)
  def deviceTypeId = column[Int]("device_type_id")
  def deviceGroupId = column[Int]("device_group_id")
  def vehicleId = column[Int]("vehicle_id", O.Nullable)
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def deviceTypeFk = foreignKey("device_type_fk", deviceTypeId, DeviceTypes)(_.id)
  def deviceGroupFk = foreignKey("device_group_fk", deviceGroupId, DeviceGroups)(_.id)
  def vehicleFk = foreignKey("vehicle_fk", vehicleId, Vehicles)(_.id)
  
  def * = id ~ name ~ displayName.? ~ description.? ~ deviceTypeId ~ deviceGroupId ~ vehicleId.? ~ enabled ~ imei.? ~ _ctime ~ _mtime ~ _ver <> (DevicePersisted, DevicePersisted.unapply _)

  /**
   * Exception mapper
   * 
   * Maps a native postgres exception to a ValidationException.
   */
  implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage() 
    if (errMessage.contains("devices_name_key")) 
      throw new ValidationException(e, "name", "Already in use")
    else if (errMessage.contains("devices_vehicles_fk"))
      throw new ValidationException(e, "vehicleId", "Not found")
    else if (errMessage.contains("device_type_fk"))
      throw new ValidationException(e, "deviceTypeId", "Not found")
    else if (errMessage.contains("device_group_fk"))
      throw new ValidationException(e, "deviceGroupId", "Not found")
    else if (errMessage.contains("mtime_gte_ctime_chk"))
      throw new ValidationException(e, "creationTime,modificationTime", "Not in the correct sequence")
    else
      throw e;
  }
  
  implicit private def deviceInfoGetResult = GetResult(r =>
    DeviceInfoPersisted(r.nextInt, r.nextString, r.nextStringOption, r.nextStringOption, r.nextInt, r.nextInt, r.nextIntOption, r.nextBoolean, r.nextStringOption, r.nextTimestamp,
      r.nextTimestamp, r.nextInt, r.nextString, r.nextString, r.nextStringOption, r.nextStringOption))

  def find(enabled: Option[Boolean] = None): Seq[DevicePersisted] = db withSession {
    val qy = enabled match {
      case None     => for { d <- Devices } yield d
      case Some(en) => for { d <- Devices if (d.enabled === en) } yield d
    }

    qy.list
  }

  def findWithInfo(enabled: Option[Boolean] = None): Seq[DeviceInfoPersisted] = db withSession {
    val sql = enabled.map(en =>
      sql"""
    SELECT 
    	D.id, D.name, D.display_name, D.description, D.device_type_id, D.device_group_id, D.vehicle_id, D.enabled, D.imei, D._ctime, D._mtime, D._ver,
    	DT.display_name, DG.display_name, V.display_name, V.license_plate
    FROM #$tableName D
      INNER JOIN device_types DT ON D.device_type_id = DT.id
      INNER JOIN device_groups DG ON D.device_group_id = DG.id
      LEFT JOIN vehicles V ON D.vehicle_id = V.id
    WHERE D.enabled = $en
    """).getOrElse(
      sql"""
    SELECT 
    	D.id, D.name, D.display_name, D.description, D.device_type_id, D.device_group_id, D.vehicle_id, D.enabled, D.imei, D._ctime, D._mtime, D._ver,
    	DT.display_name, DG.display_name, V.display_name, V.license_plate
    FROM #$tableName D
      INNER JOIN device_types DT ON D.device_type_id = DT.id
      INNER JOIN device_groups DG ON D.device_group_id = DG.id
      LEFT JOIN vehicles V ON D.vehicle_id = V.id
    """)

    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[DeviceInfoPersisted]) { _.list }
  }

  def findById(id: Int): Option[DevicePersisted] = db withSession {
    val qy = for { d <- Devices if (d.id === id) } yield d

    qy.firstOption
  }

  def findWithInfoById(id: Int): Option[DeviceInfoPersisted] = db withSession {
    val sql = sql"""
    SELECT 
    	D.id, D.name, D.display_name, D.description, D.device_type_id, D.device_group_id, D.vehicle_id, D.enabled, D.imei, D._ctime, D._mtime, D._ver,
    	DT.display_name, DG.display_name, V.display_name, V.license_plate
    FROM #$tableName D
      INNER JOIN device_types DT ON D.device_type_id = DT.id
      INNER JOIN device_groups DG ON D.device_group_id = DG.id
      LEFT JOIN vehicles V ON D.vehicle_id = V.id
    WHERE D.id = $id
    """

    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[DeviceInfoPersisted]) { _.firstOption }
  }

  def findByName(name: String): Option[DevicePersisted] = db withSession {
    val qy = for { d <- Devices if (d.name === name) } yield d

    qy.firstOption
  }

  def findWithInfoByName(name: String): Option[DeviceInfoPersisted] = db withSession {
    val sql = sql"""
    SELECT 
    	D.id, D.name, D.display_name, D.description, D.device_type_id, D.device_group_id, D.vehicle_id, D.enabled, D.imei, D._ctime, D._mtime, D._ver,
    	DT.display_name, DG.display_name, V.display_name, V.license_plate
    FROM #$tableName D
      INNER JOIN device_types DT ON D.device_type_id = DT.id
      INNER JOIN device_groups DG ON D.device_group_id = DG.id
      LEFT JOIN vehicles V ON D.vehicle_id = V.id
    WHERE D.name = $name
    """

    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[DeviceInfoPersisted]) { _.firstOption }
  }

  def insert(obj: Device): Int = db withSession {
    val sql = sql"""
    INSERT INTO #$tableName  (
    		name, 
    		display_name,
    		description,
    		enabled,
    		imei,
    		device_type_id, 
    		device_group_id,
    		vehicle_id,
    		_ctime,
    		_mtime,
    		_ver
    ) 
    VALUES (
    		${obj.name},
    		${obj.displayName},
    		${obj.description},
    		${obj.enabled},
    		${obj.imei},
    		${obj.deviceTypeId},
    		${obj.deviceGroupId},
    		${obj.vehicleId},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """
    executeSql(BackendOperation.INSERT, s"$tableName $obj", sql.as[Int]) { _.first }
  }
  def update(uobj: DevicePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE #$tableName
	       SET name = ${obj.name},
	           display_name = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	    	   imei = ${obj.imei},
	           device_type_id = ${obj.deviceTypeId},
	           device_group_id = ${obj.deviceGroupId},
	           vehicle_id = ${obj.vehicleId},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${obj.id}
		 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def updateWithVersion(uobj: DevicePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE #$tableName
	       SET name = ${obj.name},
	           display_name = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	    	   imei = ${obj.imei},	    	   
	           device_type_id = ${obj.deviceTypeId},
	           device_group_id = ${obj.deviceGroupId},
	           vehicle_id = ${obj.vehicleId},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${obj.id}
		   AND _ver = ${obj.version}
		 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  def delete(obj: DevicePersisted): Boolean = deleteById(obj.id)
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"DELETE FROM #$tableName WHERE id = ${id}"
    executeDelete("$tableName $id", sql) == 1
  }
}