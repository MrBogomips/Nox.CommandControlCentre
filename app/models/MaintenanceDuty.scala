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
import java.util.Date

/**
 * Maintenance duties for vehicles
 */
trait MaintenanceDutyTrait extends Validatable {
  val idVehicle: Int
  val idService: Int

  def validate {

  }
}

case class MaintenanceDuty(idVehicle: Int, idService: Int)
  extends MaintenanceDutyTrait
  with Model[MaintenanceDutyTrait]

case class MaintenanceDutyPersisted(id: Int, idVehicle: Int, idService: Int, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends MaintenanceDutyTrait
  with Persisted[MaintenanceDuty]

case class MaintenanceDutyInfoPersisted(id: Int, idVehicle: Int, idService: Int, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int,
                                        serviceName: String, serviceDisplayName: String, vehicleName: String, vehicleDisplayName: String, vehicleLicensePlate: String)
  extends MaintenanceDutyTrait
  with Persisted[MaintenanceDuty]

object MaintenanceDuties
  extends CrudTable[MaintenanceDutyTrait, MaintenanceDuty, MaintenanceDutyPersisted]("MaintenanceDuties") {

  // map only significant fields
  def idVehicle = column[Int]("idVehicle")
  def idService = column[Int]("idService")

  def * = id ~ idVehicle ~ idService ~ creationTime ~ modificationTime ~ version <> (MaintenanceDutyPersisted, MaintenanceDutyPersisted.unapply _)
  def forInsert = idVehicle ~ idService ~ creationTime ~ modificationTime ~ version <> (
    { t => MaintenanceDuty(t._1, t._2) },
    { (o: MaintenanceDuty) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.idVehicle, o.idService, now, now, 0))
      }
    })

  override implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("MaintenanceDutiesAK"))
      throw new ValidationException(e, "idVehicle,idService", "Already defined")
    else
      throw e;
  }
  /**
    * Returns the duties defined for a specific vehicle
    */
  def findByVehicleId(idVehicle: Int, page: Pagination): Seq[MaintenanceDutyInfoPersisted] =
    db withSession {
      page.paginate {
        {
          for {
            d <- MaintenanceDuties if (d.idVehicle is idVehicle)
            v <- Vehicles if (d.idVehicle is v.id)
            s <- MaintenanceServices if (d.idService is s.id)
          } yield (d.id, d.idVehicle, d.idService, d.creationTime, d.modificationTime, d.version, s.name, s.displayName, v.name, v.displayName, v.licensePlate)
        }.sortBy(_._8) // serviceDisplayName
      }
    }.list.map {
      case (id, idVehicle, idService, creationTime, modificationTime, version, serviceName, serviceDisplayName, vehicleName, vehicleDisplayName, vehicleLicensePlate) =>
        MaintenanceDutyInfoPersisted(id, idVehicle, idService, creationTime, modificationTime, version, serviceName, serviceDisplayName, vehicleName, vehicleDisplayName, vehicleLicensePlate)
    }

  def deleteByVehicleAndService(idVehicle: Int, idService: Int): Boolean = db withSession {
    val qy = for { d <- MaintenanceDuties if (d.idVehicle === idVehicle && d.idService === idService) } yield d
    qy.delete == 1
  }

  def update(uobj: MaintenanceDutyPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- MaintenanceDuties if (d.id === obj.id) }
        yield d.idVehicle ~ d.idService ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.idVehicle, obj.idService, now, obj.version + 1)) == 1
    }
  }
  def updateWithVersion(uobj: models.MaintenanceDutyPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- MaintenanceDuties if (d.id === obj.id && d.version === obj.version) }
        yield d.idVehicle ~ d.idService ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.idVehicle, obj.idService, now, obj.version + 1)) == 1
    }
  }
}