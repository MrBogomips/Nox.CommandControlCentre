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
  extends Table[MaintenanceDutyPersisted]("MaintenanceDuties")
  with Backend
  with CrudOperations[MaintenanceDutyTrait, MaintenanceDuty, MaintenanceDutyPersisted] {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def idVehicle = column[Int]("idVehicle")
  def idService = column[Int]("idService")
  def creationTime = column[Timestamp]("creationTime")
  def modificationTime = column[Timestamp]("modificationTime")
  def version = column[Int]("version")

  def * = id ~ idVehicle ~ idService ~ creationTime ~ modificationTime ~ version <> (MaintenanceDutyPersisted, MaintenanceDutyPersisted.unapply _)
  def forInsert = idVehicle ~ idService ~ creationTime ~ modificationTime ~ version <> (
    { t => MaintenanceDuty(t._1, t._2) },
    { (o: MaintenanceDuty) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.idVehicle, o.idService, now, now, 0))
      }
    })

  private implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("MaintenanceDutiesAK"))
      throw new ValidationException(e, "idVehicle,idService", "Already defined")
    else
      throw e;
  }

  def find(enabled: Option[Boolean] = None): Seq[MaintenanceDutyPersisted] = db withSession {
    val qy = for { d <- MaintenanceDuties } yield d
    qy.list
  }

  def findById(id: Int): Option[MaintenanceDutyPersisted] = db withSession {
    val qy = for { d <- MaintenanceDuties if (d.id === id) } yield d
    qy.firstOption
  }

  implicit private def maintenanceDutyInfoGetResult = GetResult(r =>
    MaintenanceDutyInfoPersisted(
      r.nextInt, // id 
      r.nextInt, // idVehicle,
      r.nextInt, // idService
      r.nextTimestamp, // creationTime
      r.nextTimestamp, // modificationTime
      r.nextInt, // version
      r.nextString, // service's name
      r.nextString, // service's displayName
      r.nextString, // vehicle's name
      r.nextString, // vehicle's displayName
      r.nextString // vehicle's licensePlate
      ))

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

  def deleteById(id: Int): Boolean = db withSession {
    val qy = for { d <- MaintenanceDuties if (d.id === id) } yield d
    qy.delete == 1
  }

  def deleteByVehicleAndService(idVehicle: Int, idService: Int): Boolean = db withSession {
    val qy = for { d <- MaintenanceDuties if (d.idVehicle === idVehicle && d.idService === idService) } yield d
    qy.delete == 1
  }

  def insert(uobj: MaintenanceDuty): Int = WithValidation(uobj) { obj =>
    db withSession {
      MaintenanceDuties.forInsert returning MaintenanceDuties.id insert obj
    }
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