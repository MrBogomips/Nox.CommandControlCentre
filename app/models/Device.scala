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

case class DevicePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, deviceType: DeviceTypeTrait, deviceGroup: DeviceGroupTrait, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends DeviceTrait
  with Persisted[DevicePersisted, Device] {

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled, deviceType: DeviceTypeTrait = this.deviceType, deviceGroup: DeviceGroupTrait = this.deviceGroup) =
    prepareCopy {
      DevicePersisted(id, name, displayName, description, enabled, deviceType, deviceGroup, creationTime, modificationTime, version)
    }

  def delete(): Boolean = ???
  def refetch(): Option[models.DevicePersisted] = ???
  def update(): Boolean = ???
  def updateWithVersion(): Boolean = ???
}

object Devices extends PersistedTable[DevicePersisted, Device]("devices") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("display_name", O.Nullable)
  def description = column[String]("display_name", O.Nullable)
  def enabled = column[Boolean]("enabled")
  def deviceTypeId = column[Int]("device_type_id")
  def deviceGroupId = column[Int]("device_group_id")
  def _ctime = column[Timestamp]("_ctime")
  def _mtime = column[Timestamp]("_mtime")
  def _ver = column[Int]("_ver")
  
  def deviceTypeFk = foreignKey("defice_type_fk", deviceTypeId, DeviceTypes)(_.id)
  def deviceGroupFk = foreignKey("defice_group_fk", deviceGroupId, DeviceGroups)(_.id)

  def * = ???

  def delete(obj: models.DevicePersisted): Boolean = ???
  def deleteById(id: Int): Boolean = ???
  def findAll: Seq[models.DevicePersisted] = ???
  def findById(id: Int): Option[models.DevicePersisted] = ???
  def insert(obj: models.Device): Int = ???
  def update(obj: models.DevicePersisted): Boolean = ???
  def updateWithVersion(obj: models.DevicePersisted): Boolean = ???
}

  
