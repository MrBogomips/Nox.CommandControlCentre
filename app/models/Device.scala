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
}

case class Device(private val initName: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait)
  extends DeviceTrait {
  lazy val name = normalizeName(initName)

  def this(name: String, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait) =
    this(name, name, None, true, deviceType, deviceGroup)

  override def toString = s"Device($name,$displayName,$description,$deviceType,$deviceGroup)"

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceType: DeviceTypeTrait = this.deviceType, deviceGroup: DeviceGroupTrait = this.deviceGroup) =
    Device(name, displayName, description, enabled, deviceType, deviceGroup)
}

//case class DevicePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
case class DevicePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypePersisted, deviceGroup: DeviceGroupPersisted, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends DeviceTrait
  with Persisted[DevicePersisted, Device] {

  //def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceType: DeviceTypeTrait = this.deviceType, deviceGroup: DeviceGroupTrait = this.deviceGroup) =
  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceType: DeviceTypePersisted = this.deviceType, deviceGroup: DeviceGroupPersisted = this.deviceGroup) =
    prepareCopy {
      DevicePersisted(id, name, displayName, description, enabled, deviceType, deviceGroup, creationTime, modificationTime, version)
    }

  def delete(): Boolean = ???
  def refetch(): Option[models.DevicePersisted] = ???
  def update(): Boolean = ???
  def updateWithVersion(): Boolean = ???
}

//case class DevicePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
case class DevicePersistedRecord private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceTypeId: Int, deviceGroupId: Int, creationTime: Timestamp, modificationTime: Timestamp, version: Int) //extends DeviceTrait
//with Persisted[DevicePersisted, Device] 
{

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceTypeId: Int = this.deviceTypeId, deviceGroupId: Int = this.deviceGroupId) =
    //prepareCopy {
    DevicePersistedRecord(id, name, displayName, description, enabled, deviceTypeId, deviceGroupId, creationTime, modificationTime, version)
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
  def description = column[String]("display_name", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def deviceTypeId = column[Int]("device_type_id")
  def deviceGroupId = column[Int]("device_group_id")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")

  def deviceTypeFk = foreignKey("defice_type_fk", deviceTypeId, DeviceTypes)(_.id)
  def deviceGroupFk = foreignKey("defice_group_fk", deviceGroupId, DeviceGroups)(_.id)
  /*
 * 	id					INT NOT NULL PRIMARY KEY DEFAULT(nextval('devices_id_seq')),
	name				text NOT NULL UNIQUE,
	display_name		text NULL,
	description			text NULL,
	enabled				BOOLEAN NOT NULL,
	device_type_id		INT NOT NULL,
	device_group_id		INT NOT NULL,
	_ctime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_mtime				TIMESTAMP NOT NULL DEFAULT(NOW()),
	_ver				INT NOT NULL DEFAULT(0),
 */
  def * = id ~ name ~ displayName ~ description.? ~ enabled ~ deviceTypeId ~ deviceGroupId ~ _ctime ~ _mtime ~ _ver <> (DevicePersistedRecord, DevicePersistedRecord.unapply _)

  def delete(obj: models.DevicePersisted): Boolean = deleteById(obj.id)
  def deleteById(id: Int): Boolean = db withSession {
      val sql = sqlu"DELETE FROM devices WHERE id = ${id}"
      executeDelete("device $id", sql) == 1
    }
  def findAll: Seq[models.DevicePersisted] = db withSession {
    val qy = for {
      dr <- Devices
      dt <- DeviceTypes if (dr.deviceTypeId === dt.id)
      dg <- DeviceGroups if (dr.deviceGroupId === dg.id)
    } yield (dr, dt, dg)

    qy.list.map(r => DevicePersisted(r._1.id, r._1.name, r._1.displayName, r._1.description, r._1.enabled, r._2, r._3, r._1.creationTime, r._1.modificationTime, r._1.version))
  }
  def findById(id: Int): Option[models.DevicePersisted] = db withSession {
    val qy = for {
      dr <- Devices if (dr.id === id)
      dt <- DeviceTypes if (dr.deviceTypeId === dt.id)
      dg <- DeviceGroups if (dr.deviceGroupId === dg.id)
    } yield (dr, dt, dg)

    qy.firstOption.map(r => DevicePersisted(r._1.id, r._1.name, r._1.displayName, r._1.description, r._1.enabled, r._2, r._3, r._1.creationTime, r._1.modificationTime, r._1.version))
  }
  
  def findByName(name: String): Option[models.DevicePersisted] = db withSession {
    val qy = for {
      dr <- Devices if (dr.name === name)
      dt <- DeviceTypes if (dr.deviceTypeId === dt.id)
      dg <- DeviceGroups if (dr.deviceGroupId === dg.id)
    } yield (dr, dt, dg)

    qy.firstOption.map(r => DevicePersisted(r._1.id, r._1.name, r._1.displayName, r._1.description, r._1.enabled, r._2, r._3, r._1.creationTime, r._1.modificationTime, r._1.version))
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

      executeSql(BackendOperation.INSERT, "device $device", sql.as[Int]) { _.first }
    }
  }
  def update(obj: models.DevicePersisted): Boolean = withPersistableObject(obj, default = false) {
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
	 """
      executeUpdate("device $obj", sql) == 1
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
      executeUpdate("device $obj with version check", sql) == 1
    }
  }
}

  
