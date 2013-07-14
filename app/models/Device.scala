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

  def validate: Seq[ValidationError] = {
    import collection.mutable.{ HashMap, MultiMap, Set }
    val errorsAccumulator = new HashMap[String, Set[String]] with MultiMap[String, String]
    // Provide all the rule checked
    if (false)
      errorsAccumulator.addBinding("endAssignement", "End assignement must follow the begin")

    errorsAccumulator.toList.map(v => ValidationError(v._1, collection.immutable.Set(v._2.toList: _*)))
  }
}

trait DeviceInfoTrait extends DeviceTrait {
  val deviceTypeDisplayName: String
  val deviceGroupDisplayName: String
  val vehicleDisplayName: Option[String]
  val vehicleLicensePlate: Option[String]
}

case class Device(name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean)
  extends DeviceTrait
  with ValidationRequired

case class DevicePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends DeviceTrait
  with Persistable {
  def this(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean) =
   this(id, name, displayName0, description, deviceTypeId, deviceGroupId, vehicleId, enabled, new Timestamp(0), new Timestamp(0), 0)
  def this(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, version: Int) =
    this(id, name, displayName0, description, deviceTypeId, deviceGroupId, vehicleId, enabled, new Timestamp(0), new Timestamp(0), version)
}

case class DeviceInfoPersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, creationTime: Timestamp, modificationTime: Timestamp, version: Int, deviceTypeDisplayName: String, deviceGroupDisplayName: String, vehicleDisplayName: Option[String], vehicleLicensePlate: Option[String])
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
  def deviceTypeId = column[Int]("device_type_id")
  def deviceGroupId = column[Int]("device_group_id")
  def vehicleId = column[Int]("vehicle_id", O.Nullable)
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def deviceTypeFk = foreignKey("device_type_fk", deviceTypeId, DeviceTypes)(_.id)
  def deviceGroupFk = foreignKey("device_group_fk", deviceGroupId, DeviceGroups)(_.id)
  def vehicleFk = foreignKey("vehicle_fk", vehicleId, Vehicles)(_.id)

  def * = id ~ name ~ displayName.? ~ description.? ~ deviceTypeId ~ deviceGroupId ~ vehicleId.? ~ enabled ~ _ctime ~ _mtime ~ _ver <> (DevicePersisted, DevicePersisted.unapply _)

  implicit private def deviceInfoGetResult = GetResult(r =>
    DeviceInfoPersisted(r.nextInt, r.nextString, r.nextStringOption, r.nextStringOption, r.nextInt, r.nextInt, r.nextIntOption, r.nextBoolean, r.nextTimestamp,
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
    	D.id, D.name, D.display_name, D.description, D.device_type_id, D.device_group_id, D.vehicle_id, D.enabled, D._ctime, D._mtime, D._ver,
    	DT.display_name, DG.display_name, V.display_name, V.license_plate
    FROM #$tableName D
      INNER JOIN device_types DT ON D.device_type_id = DT.id
      INNER JOIN device_groups DG ON D.device_group_id = DG.id
      LEFT JOIN vehicles V ON D.vehicle_id = V.id
    WHERE D.enabled = $en
    """).getOrElse(
      sql"""
    SELECT 
    	D.id, D.name, D.display_name, D.description, D.device_type_id, D.device_group_id, D.vehicle_id, D.enabled, D._ctime, D._mtime, D._ver,
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
    	D.id, D.name, D.display_name, D.description, D.device_type_id, D.device_group_id, D.vehicle_id, D.enabled, D._ctime, D._mtime, D._ver,
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
    	D.id, D.name, D.display_name, D.description, D.device_type_id, D.device_group_id, D.vehicle_id, D.enabled, D._ctime, D._mtime, D._ver,
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
    		${obj.deviceTypeId},
    		${obj.deviceGroupId},
    		${obj.vehicleId},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """

    executeSql(BackendOperation.INSERT, s"device $obj", sql.as[Int]) { _.first }
  }
  def update(obj: DevicePersisted): Boolean = //withPersistableObject(obj, default = false) {
    db withSession {
      val sql = sqlu"""
	   UPDATE devices
	       SET name = ${obj.name},
	           display_name = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	           device_type_id = ${obj.deviceTypeId},
	           device_group_id = ${obj.deviceGroupId},
	           vehicle_id = ${obj.vehicleId},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${obj.id}
		 """
      executeUpdate(s"device $obj", sql) == 1
    }
  //}
  def updateWithVersion(obj: DevicePersisted): Boolean = //withPersistableObject(obj, default = false) {
    db withSession {
      val sql = sqlu"""
	   UPDATE devices
	       SET name = ${obj.name},
	           display_name = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	           device_type_id = ${obj.deviceTypeId},
	           device_group_id = ${obj.deviceGroupId},
	           vehicle_id = ${obj.vehicleId},
	           _mtime = NOW(),
	           _ver = _ver + 1 
		 WHERE id = ${obj.id}
		   AND _ver = ${obj.version}
		 """
      executeUpdate(s"device $obj", sql) == 1
    }
  //}
  def delete(obj: DevicePersisted): Boolean = deleteById(obj.id)
  def deleteById(id: Int): Boolean = db withSession {
    val sql = sqlu"DELETE FROM devices WHERE id = ${id}"
    executeDelete("device $id", sql) == 1
  }
}