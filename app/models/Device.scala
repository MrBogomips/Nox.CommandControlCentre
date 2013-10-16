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
import org.joda.time.DateTime
import java.util.Date
import utils.Converter._

trait DeviceTrait extends NamedEntityTrait {
  val name: String
  val displayName0: Option[String]
  val description: Option[String]
  val deviceTypeId: Int
  val deviceGroupId: Int
  val vehicleId: Option[Int]
  val deviceManagerId: Option[Int]
  val enabled: Boolean
  val simcardId: Option[Int]

  override def validate {
    super.validate
    validateMinValue("deviceTypeId", deviceTypeId, 1)
    validateMinValue("deviceGroupId", deviceGroupId, 1)
    vehicleId.map(v => validateMinValue("vehicleId", v, 1))
    simcardId.map(v => validateMinValue("simcardId", v, 1))
  }
}

trait DeviceInfoTrait extends DeviceTrait {
  val deviceTypeDisplayName: String
  val deviceGroupDisplayName: String
  val vehicleDisplayName: Option[String]
  val vehicleLicensePlate: Option[String]
  val simcardImei: Option[String]
  val simcardDisplayName: Option[String]
}

case class Device(name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, simcardId: Option[Int], deviceManagerId: Option[Int])
  extends NamedEntity[DeviceTrait]

case class DevicePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, simcardId: Option[Int], deviceManagerId: Option[Int], creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends NamedEntityPersisted[Device] {
  def this(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, simcardId: Option[Int], deviceManagerId: Option[Int]) =
    this(id, name, displayName0, description, deviceTypeId, deviceGroupId, vehicleId, enabled, simcardId, deviceManagerId, new Timestamp(0), new Timestamp(0), 0)
  def this(id: Int, name: String, displayName0: Option[String], description: Option[String], deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, simcardId: Option[Int], deviceManagerId: Option[Int], version: Int) =
    this(id, name, displayName0, description, deviceTypeId, deviceGroupId, vehicleId, enabled, simcardId, deviceManagerId, new Timestamp(0), new Timestamp(0), version)
}

case class DeviceInfoPersisted(id: Int, name: String, displayName0: Option[String], description: Option[String],
                               deviceTypeId: Int, deviceGroupId: Int, vehicleId: Option[Int], enabled: Boolean, simcardId: Option[Int],
                               deviceManagerId: Option[Int], creationTime: Timestamp, modificationTime: Timestamp, version: Int, deviceTypeDisplayName: String, deviceGroupDisplayName: String,
                               vehicleDisplayName: Option[String], vehicleLicensePlate: Option[String], simcardImei: Option[String], simcardDisplayName: Option[String],
                               deviceManagerName: Option[String], deviceManagerDisplayName: Option[String])
  extends DeviceInfoTrait
  with Persisted[Device] // TODO

object Devices
  extends NamedEntityCrudTable[DeviceTrait, Device, DevicePersisted]("Devices", "") {

  // Map only significative fields
  def simcardId = column[Int]("simcardId", O.Nullable)
  def deviceManagerId = column[Int]("deviceManagerId", O.Nullable)
  def deviceTypeId = column[Int]("deviceTypeId")
  def deviceGroupId = column[Int]("deviceGroupId")
  def vehicleId = column[Int]("vehicleId", O.Nullable)

  def deviceTypeFk = foreignKey("device_type_fk", deviceTypeId, DeviceTypes)(_.id)
  def deviceGroupFk = foreignKey("device_group_fk", deviceGroupId, DeviceGroups)(_.id)
  def vehicleFk = foreignKey("vehicle_fk", vehicleId, Vehicles)(_.id)

  def * = id ~ name ~ displayName.? ~ description.? ~ deviceTypeId ~ deviceGroupId ~ vehicleId.? ~ enabled ~ simcardId.? ~ deviceManagerId.? ~ creationTime ~ modificationTime ~ version <> (DevicePersisted, DevicePersisted.unapply _)
  def forInsert = name ~ displayName.? ~ description.? ~ deviceTypeId ~ deviceGroupId ~ vehicleId.? ~ enabled ~ simcardId.? ~ deviceManagerId.? ~ creationTime ~ modificationTime ~ version <> (
    { t => Device(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9) },
    { (o: Device) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.name, Some(o.displayName), o.description, o.deviceTypeId, o.deviceGroupId, o.vehicleId, o.enabled, o.simcardId, o.deviceManagerId, now, now, 0))
      }
    })
  /**
    * Exception mapper
    *
    * Maps a native postgres exception to a ValidationException.
    */
  override implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("devices_name_key"))
      throw new ValidationException(e, "name", "Already in use")
    else if (errMessage.contains("devices_Vehicles_fk"))
      throw new ValidationException(e, "vehicleId", "Not found")
    else if (errMessage.contains("device_type_fk"))
      throw new ValidationException(e, "deviceTypeId", "Not found")
    else if (errMessage.contains("device_group_fk"))
      throw new ValidationException(e, "deviceGroupId", "Not found")
    else if (errMessage.contains("devices_simcard_fk"))
      throw new ValidationException(e, "simcardId", "Not found")
    else if (errMessage.contains("mtime_gtecreationTime_chk"))
      throw new ValidationException(e, "creationTime,modificationTime", "Not in the correct sequence")
    else
      throw e;
  }

  implicit private def deviceInfoGetResult = GetResult(r =>
    DeviceInfoPersisted(r.nextInt, r.nextString, r.nextStringOption, r.nextStringOption, r.nextInt, r.nextInt, r.nextIntOption, r.nextBoolean,
      r.nextIntOption, //simcardId 
      r.nextIntOption, //deviceManagerId 
      r.nextTimestamp,
      r.nextTimestamp, r.nextInt, r.nextString, r.nextString, r.nextStringOption,
      r.nextStringOption,
      r.nextStringOption, // simcardImei
      r.nextStringOption, // simcardDisplayName
      r.nextStringOption, // deviceManagerName
      r.nextStringOption // deviceManagerDisplayName
      ))

  def findWithInfo(enabled: Option[Boolean] = None): Seq[DeviceInfoPersisted] = db withSession {
    val sql = enabled.map(en =>
      sql"""
    SELECT 
    	D."id", D."name", D."displayName", D."description", D."deviceTypeId", D."deviceGroupId", D."vehicleId", D."enabled", D."simcardId", D."deviceManagerId", D."creationTime", D."modificationTime", D."version",
    	DT."displayName", DG."displayName", V."displayName", V."licensePlate", SC."imei", SC."displayName", DM."name", DM."displayName"
    FROM "#$tableName" D
      INNER JOIN "DeviceTypes" DT ON D."deviceTypeId" = DT."id"
      INNER JOIN "DeviceGroups" DG ON D."deviceGroupId" = DG."id"
      LEFT JOIN "Vehicles" V ON D."vehicleId" = V."id"
      LEFT JOIN "Simcards" SC ON D."simcardId" = SC."id"
      LEFT JOIN "DeviceManagers" DM ON D."deviceManagerId" = DM.id
    WHERE D."enabled" = $en
    """).getOrElse(
      sql"""
    SELECT 
    	D."id", D."name", D."displayName", D."description", D."deviceTypeId", D."deviceGroupId", D."vehicleId", D."enabled", D."simcardId", D."deviceManagerId", D."creationTime", D."modificationTime", D."version",
    	DT."displayName", DG."displayName", V."displayName", V."licensePlate", SC."imei", SC."displayName", DM."name", DM."displayName"
    FROM "#$tableName" D
      INNER JOIN "DeviceTypes" DT ON D."deviceTypeId" = DT."id"
      INNER JOIN "DeviceGroups" DG ON D."deviceGroupId" = DG."id"
      LEFT JOIN "Vehicles" V ON D."vehicleId" = V."id"
      LEFT JOIN "Simcards" SC ON D."simcardId" = SC."id"
      LEFT JOIN "DeviceManagers" DM ON D."deviceManagerId" = DM.id
    """)

    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[DeviceInfoPersisted]) { _.list }
  }

  def findWithInfoById(id: Int): Option[DeviceInfoPersisted] = db withSession {
    val sql = sql"""
    SELECT 
    	D.id, D.name, D."displayName", D.description, D."deviceTypeId", D."deviceGroupId", D."vehicleId", D.enabled, D."simcardId", D."deviceManagerId", D."creationTime", D."modificationTime", D.version,
    	DT."displayName", DG."displayName", V."displayName", V."licensePlate", SC.imei, SC."displayName", DM."name", DM."displayName"
    FROM "#$tableName" D
      INNER JOIN "DeviceTypes" DT ON D."deviceTypeId" = DT.id
      INNER JOIN "DeviceGroups" DG ON D."deviceGroupId" = DG.id
      LEFT JOIN "Vehicles" V ON D."vehicleId" = V.id
      LEFT JOIN "Simcards" SC ON D."simcardId" = SC.id
      LEFT JOIN "DeviceManagers" DM ON D."deviceManagerId" = DM.id
    WHERE D.id = $id
    """

    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[DeviceInfoPersisted]) { _.firstOption }
  }

  def findWithInfoByName(name: String): Option[DeviceInfoPersisted] = db withSession {
    val sql = sql"""
    SELECT 
    	D.id, D.name, D."displayName", D.description, D."deviceTypeId", D."deviceGroupId", D."vehicleId", D.enabled, D."simcardId", D."deviceManagerId", D."creationTime", D."modificationTime", D.version,
    	DT."displayName", DG."displayName", V."displayName", V."licensePlate", SC.imei, SC."displayName", DM."name", DM."displayName"
    FROM "#$tableName" D
      INNER JOIN "DeviceTypes" DT ON D."deviceTypeId" = DT.id
      INNER JOIN "DeviceGroups" DG ON D."deviceGroupId" = DG.id
      LEFT JOIN "Vehicles" V ON D."vehicleId" = V.id
      LEFT JOIN "Simcards" SC ON D."simcardId" = SC.id
      LEFT JOIN "DeviceManagers" DM ON D."deviceManagerId" = DM.id
    WHERE D.name = $name
    """

    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[DeviceInfoPersisted]) { _.firstOption }
  }
  // TODO: reimplement avoiding explicit SQL
  override def update(uobj: DevicePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET name = ${obj.name},
	           "displayName" = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	    	   "simcardId" = ${obj.simcardId},
	    	   "deviceManagerId" = ${obj.deviceManagerId},
	           "deviceTypeId" = ${obj.deviceTypeId},
	           "deviceGroupId" = ${obj.deviceGroupId},
	           "vehicleId" = ${obj.vehicleId},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${obj.id}
		 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
  // TODO: reimplement avoiding explicit SQL
  override def updateWithVersion(uobj: DevicePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val sql = sqlu"""
	   UPDATE "#$tableName"
	       SET name = ${obj.name},
	           "displayName" = ${obj.displayName},
	    	   description = ${obj.description},
	    	   enabled = ${obj.enabled},
	    	   "simcardId" = ${obj.simcardId},
	    	   "deviceManagerId" = ${obj.deviceManagerId},
	           "deviceTypeId" = ${obj.deviceTypeId},
	           "deviceGroupId" = ${obj.deviceGroupId},
	           "vehicleId" = ${obj.vehicleId},
	           "modificationTime" = NOW(),
	           version = version + 1 
		 WHERE id = ${obj.id}
		   AND version = ${obj.version}
		 """
      executeUpdate(s"$tableName $obj", sql) == 1
    }
  }
}