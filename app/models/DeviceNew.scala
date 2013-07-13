package models._new

import models._

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
  val displayName: String
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
}

case class Device(name: String, displayName: String, description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean)
  extends DeviceTrait
  with ValidationRequired

case class DevicePersisted(id: Int, name: String, displayName: String, description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends DeviceTrait
  with Persistable
  
class DeviceInfoPersisted(id: Int, name: String, displayName: String, description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, creationTime: Timestamp, modificationTime: Timestamp, version: Int, val deviceTypeDisplayName: String, val deviceGroupDisplayName: String, val vehicleDisplayName: Option[String]) 
	extends DevicePersisted(id, name, displayName, description, deviceTypeId, deviceGroupId, vehicleId, enabled, creationTime, modificationTime, version)
	with DeviceInfoTrait

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

  def * = id ~ name ~ displayName ~ description.? ~ deviceTypeId ~ deviceGroupId ~ vehicleId.? ~ enabled ~ _ctime ~ _mtime ~ _ver <> (DevicePersisted, DevicePersisted.unapply _)

  def find(all: Boolean = true): Seq[DevicePersisted] = db withSession (for { d <- Devices } yield d).list
  
  def fakeMethod = {
    val obj = new {
      val name: String = ""
	  val displayName: String = ""
	  val description: Option[String] = None
	  val deviceTypeId: Int = 0
	  val deviceGroupId: Int = 0
	  val vehicleId: Option[Int] = None
	  val enabled: Boolean  = true
	  
      val deviceTypeDisplayName: String = ""
	  val deviceGroupDisplayName: String = ""
	  val vehicleDisplayName: Option[String] = None
	  
	  val id: Int = 0
	  val creationTime: Timestamp = new Timestamp(234)
      val modificationTime: Timestamp = new Timestamp(234)
      val version: Int = 0
      
    } with DeviceInfoTrait with Persistable
    
    val obj2 = new {
      val name: String = ""
	  val displayName: String = ""
	  val description: Option[String] = None
	  val deviceTypeId: Int = 0
	  val deviceGroupId: Int = 0
	  val vehicleId: Option[Int] = None
	  val enabled: Boolean  = true
	  
      val deviceTypeDisplayName: String = ""
	  val deviceGroupDisplayName: String = ""
	  val vehicleDisplayName: Option[String] = None
	  
	  val id: Int = 0
	  val creationTime: Timestamp = new Timestamp(234)
      val modificationTime: Timestamp = new Timestamp(234)
      val version: Int = 0
      
    } with DeviceInfoTrait with Persistable
    
    obj
  }
  
  def findWithInfo(all: Boolean): Seq[DeviceInfoPersisted] = db withSession {
    ???
  }

  def findAllEnabled: Seq[models.DevicePersisted] = db withSession { ??? }

  def findById(id: Int): Option[models.DevicePersisted] = db withSession { ??? }

  def findByName(name: String): Option[models.DevicePersisted] = db withSession { ??? }

  def insert(obj: models.Device): Int = { ??? }
  def update(obj: models.DevicePersisted): Boolean = withPersistableObject(obj, default = false) { ??? }
  def updateWithVersion(obj: models.DevicePersisted): Boolean = withPersistableObject(obj, default = false) { ??? }
}