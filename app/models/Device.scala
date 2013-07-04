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

trait DeviceTrait extends NamedEntityTrait {
  val deviceType: DeviceTypeTrait
  val deviceGroup: DeviceGroupTrait
  val vehicle: Option[VehicleTrait]
}

case class Device(private val initName: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait, vehicle: Option[VehicleTrait])
  extends DeviceTrait {
  lazy val name = normalizeName(initName)

  def this(name: String, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait, vehicle: Option[VehicleTrait] = None) =
    this(name, name, None, true, deviceType, deviceGroup, vehicle)

  override def toString = s"Device($name,$displayName,$description,$deviceType,$deviceGroup)"

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceType: DeviceTypeTrait = this.deviceType, deviceGroup: DeviceGroupTrait = this.deviceGroup, vehicle: Option[VehicleTrait] = this.vehicle) =
    Device(name, displayName, description, enabled, deviceType, deviceGroup, vehicle)
}

//case class DevicePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
case class DevicePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypePersisted, deviceGroup: DeviceGroupPersisted, vehicle: Option[VehiclePersisted], creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends DeviceTrait
  with Persisted[DevicePersisted, Device] {

  //def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceType: DeviceTypeTrait = this.deviceType, deviceGroup: DeviceGroupTrait = this.deviceGroup) =
  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceType: DeviceTypePersisted = this.deviceType, deviceGroup: DeviceGroupPersisted = this.deviceGroup, vehicle: Option[VehiclePersisted] = this.vehicle) =
    prepareCopy {
      DevicePersisted(id, name, displayName, description, enabled, deviceType, deviceGroup, vehicle, creationTime, modificationTime, version)
    }

  def delete(): Boolean = ???
  def refetch(): Option[models.DevicePersisted] = ???
  def update(): Boolean = ???
  def updateWithVersion(): Boolean = ???
}

//case class DevicePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
case class DevicePersistedRecord private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], creationTime: Timestamp, modificationTime: Timestamp, version: Int) //extends DeviceTrait
//with Persisted[DevicePersisted, Device] 
{

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceTypeId: Int = this.deviceTypeId, deviceGroupId: Int = this.deviceGroupId, vehicleId: Option[Int] = this.vehicleId) =
    //prepareCopy {
    DevicePersistedRecord(id, name, displayName, description, enabled, deviceTypeId, deviceGroupId, vehicleId, creationTime, modificationTime, version)
  //}

  def delete(): Boolean = ???
  def refetch(): Option[models.DevicePersisted] = ???
  def update(): Boolean = ???
  def updateWithVersion(): Boolean = ???
}

object Devices
  extends Table[DevicePersistedRecord]("devices")
  with Backend {
  //extends PersistedTable[DevicePersistedRecord, Device]("devices") {
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
  
  def * = id ~ name ~ displayName ~ description.? ~ enabled ~ deviceTypeId ~ deviceGroupId ~ vehicleId.? ~ _ctime ~ _mtime ~ _ver <> (DevicePersistedRecord, DevicePersistedRecord.unapply _)

  def delete(obj: models.DevicePersisted): Boolean = deleteById(obj.id)
  def deleteById(id: Int): Boolean = db withSession {
      val sql = sqlu"DELETE FROM devices WHERE id = ${id}"
      executeDelete("device $id", sql) == 1
    }
  
  //private def recordExtractor(r: (DevicePersistedRecord, DeviceTypePersisted, DeviceGroupPersisted, Option[VehiclePersisted])): DevicePersisted =
  //  DevicePersisted(r._1.id, r._1.name, r._1.displayName, r._1.description, r._1.enabled, r._2, r._3, r._4, r._1.creationTime, r._1.modificationTime, r._1.version)
  private def recordExtractor(r: (DevicePersistedRecord, DeviceTypePersisted, DeviceGroupPersisted)): DevicePersisted =
    DevicePersisted(r._1.id, r._1.name, r._1.displayName, r._1.description, r._1.enabled, r._2, r._3, None, r._1.creationTime, r._1.modificationTime, r._1.version)
  
  private lazy val SELECT_STAR = 
    sql"""
    SELECT *
    FROM #$tableName AS D.
    INNER JOIN #${DeviceTypes.tableName} AS DT
    	ON D.device_type_id = DT.id
    INNER JOIN #${DeviceGroups.tableName} AS DG
    	ON D.device_group_id = DG.id
    LEFT JOIN #${Vehicles.tableName} AS V
    	ON ON D.vehicle_id = V.id
    """
    
  def findAll: Seq[models.DevicePersisted] = db withSession {
    val qy = for {
      dr <- Devices
      dt <- DeviceTypes if (dr.deviceTypeId === dt.id)
      dg <- DeviceGroups if (dr.deviceGroupId === dg.id)
      //v <- dr leftJoin Vehicles on (_.vehicleId === _.id)  
    } //yield (dr, dt, dg, v._2)
    yield (dr, dt, dg)
    

    
    qy.list.map(recordExtractor)
  }
  def findAllEnabled: Seq[models.DevicePersisted] = db withSession {
    val qy = for {
      dr <- Devices if (dr.enabled === true)
      dt <- DeviceTypes if (dr.deviceTypeId === dt.id)
      dg <- DeviceGroups if (dr.deviceGroupId === dg.id)
    } yield (dr, dt, dg)

    qy.list.map(recordExtractor)
  }
  def findById(id: Int): Option[models.DevicePersisted] = db withSession {
    val qy = for {
      dr <- Devices if (dr.id === id)
      dt <- DeviceTypes if (dr.deviceTypeId === dt.id)
      dg <- DeviceGroups if (dr.deviceGroupId === dg.id)
    } yield (dr, dt, dg)

    qy.firstOption.map(recordExtractor)
  }
  
  def findByName(name: String): Option[models.DevicePersisted] = db withSession {
    val qy = for {
      dr <- Devices if (dr.name === name)
      dt <- DeviceTypes if (dr.deviceTypeId === dt.id)
      dg <- DeviceGroups if (dr.deviceGroupId === dg.id)
    } yield (dr, dt, dg)

    qy.firstOption.map(recordExtractor)
  }

  def insert(obj: models.Device): Int = {
    def deviceTypeId = obj.deviceType match {
      case o: DeviceTypePersisted => o.id
      case o => DeviceTypes.findByName(o.name).get.id
    }
    def deviceGroupId = obj.deviceGroup match {
      case o: DeviceGroupPersisted => o.id
      case o => DeviceGroups.findByName(o.name).get.id
    }
    db withSession {
      val sql = sql"""
    INSERT INTO devices (
    		name, 
    		display_name,
    		description,
    		enabled, 
    		device_type_id, 
    		device_group_id,
    		_ctime,
    		_mtime,
    		_ver
    ) 
    VALUES (
    		${obj.name},
    		${obj.displayName},
    		${obj.description},
    		${obj.enabled},
    		${deviceTypeId},
    		${deviceGroupId},
    		NOW(),
    		NOW(),
    		0
    )
    RETURNING id
    """

      executeSql(BackendOperation.INSERT, s"device $obj", sql.as[Int]) { _.first }
    }
  }
  def update(obj: models.DevicePersisted): Boolean = withPersistableObject(obj, default = false) {
    db withSession {		
      Logger.debug(s"description = ${obj.description}")
      val sql = sqlu"""
   UPDATE devices
       SET name = ${obj.name},
           display_name = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
           device_type_id = ${obj.deviceType.id},
           device_group_id = ${obj.deviceGroup.id},
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${obj.id}
	 """
      executeUpdate(s"device $obj", sql) == 1
    }
  }
  def updateWithVersion(obj: models.DevicePersisted): Boolean = withPersistableObject(obj, default = false) {
    db withSession {
      val sql = sqlu"""
    UPDATE devices
       SET name = ${obj.name},
           display_name = ${obj.displayName},
    	   description = ${obj.description},
    	   enabled = ${obj.enabled},
           device_type_id = ${obj.deviceType.id},
           device_group_id = ${obj.deviceGroup.id},
           _mtime = NOW(),
           _ver = _ver + 1 
	 WHERE id = ${obj.id}
	   AND _ver = ${obj.version}
	 """
      executeUpdate(s"device $obj with version check", sql) == 1
    }
  }
}

  
