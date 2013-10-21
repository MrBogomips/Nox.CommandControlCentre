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
    vehicleTypeId.map(v => validateMinValue("vehicleTypeId", v, 1))
  }
}

trait VehicleInfoTrait extends VehicleTrait {
  val vehicleTypeDisplayName: Option[String]
}

case class Vehicle(name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, model: String, licensePlate: String, vehicleTypeId: Option[Int] = None)
  extends NamedEntity[VehicleTrait] {

  def this(name: String, model: String, licensePlate: String, vehicleTypeId: Option[Int] = None) =
    this(name, Some(name), None, true, model, licensePlate, vehicleTypeId)
}

case class VehiclePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, model: String, licensePlate: String, vehicleTypeId: Option[Int], creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends NamedEntityPersisted[Vehicle]

case class VehicleInfoPersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, model: String, 
    licensePlate: String, vehicleTypeId: Option[Int], vehicleTypeDisplayName: Option[String], creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends VehicleInfoTrait
  with Persisted[Vehicle]

object Vehicles
  extends NamedEntityCrudTable[VehicleTrait, Vehicle, VehiclePersisted]("Vehicles", "vehicle_name_key") {
  
  // only significant fields
  def model = column[String]("model")
  def licensePlate = column[String]("licensePlate")
  def vehicleTypeId = column[Int]("vehicleTypeId")

  def * = id ~ name ~ displayName.? ~ description.? ~ enabled ~ model ~ licensePlate ~ vehicleTypeId.? ~ creationTime ~ modificationTime ~ version <> (VehiclePersisted.apply _, VehiclePersisted.unapply _)
  def forInsert = name ~ displayName.? ~ description.? ~ enabled ~ model ~ licensePlate ~ vehicleTypeId.? ~ creationTime ~ modificationTime ~ version <> (
    { t => Vehicle(t._1, t._2, t._3, t._4, t._5, t._6, t._7) },
    { (o: Vehicle) =>
      {
        import java.util.Date
        val now = new Timestamp(new Date().getTime())
        Some((o.name, Some(o.displayName), o.description, o.enabled, o.model, o.licensePlate, o.vehicleTypeId, now, now, 0))
      }
    })
    
  implicit private def vehicleInfoGetResult = GetResult(r =>
    VehicleInfoPersisted(
      r.nextInt, // id 
      r.nextString, // name,
      r.nextStringOption, //displayName0
      r.nextStringOption, // description
      r.nextBoolean, // enabled
      r.nextString, // model
      r.nextString, // licensePlate
      r.nextIntOption, // vehicleTypeId
      r.nextStringOption, // vehicleType's displayName
      r.nextTimestamp, // creationTime
      r.nextTimestamp, // modificationTime
      r.nextInt // version
      ))

  
  def findWithInfo(enabled: Option[Boolean] = None): Seq[VehicleInfoPersisted] = db withSession {
    val sql = enabled.map(en =>
      sql"""
    SELECT 
    	V."id", V."name", V."displayName", V."description", V."enabled", V."model", V."licensePlate", V."vehicleTypeId", 
        VT."displayName",
    	V."creationTime", V."modificationTime", V."version"
    FROM "#$tableName" V
      LEFT JOIN "VehicleTypes" VT ON V."vehicleTypeId" = VT."id"
    WHERE V."enabled" = $en
    """).getOrElse(
      sql"""
    SELECT 
    	V."id", V."name", V."displayName", V."description", V."enabled", V."model", V."licensePlate", V."vehicleTypeId", 
        VT."displayName",
    	V."creationTime", V."modificationTime", V."version"
    FROM "#$tableName" V
      LEFT JOIN "VehicleTypes" VT ON V."vehicleTypeId" = VT."id"
    """)

    executeSql(BackendOperation.SELECT, s"$tableName ${this.toString}", sql.as[VehicleInfoPersisted]) { _.list }
  }
  // TODO: reimplement avoiding explicit SQL
  override def update(uobj: models.VehiclePersisted): Boolean = WithValidation(uobj) { obj =>
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
  // TODO: reimplement avoiding explicit SQL
  override def updateWithVersion(uobj: models.VehiclePersisted): Boolean = WithValidation(uobj) { obj =>
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
}

  
