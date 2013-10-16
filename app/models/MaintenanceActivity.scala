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
import org.joda.time.DateTime

import com.github.nscala_time.time.Imports._
import com.github.tototoshi.slick.JodaSupport._

/**
  * Maintenance activity
  */
trait MaintenanceActivityTrait extends Validatable {
  val idVehicle: Int
  val idOperator: Int
  val odometer: Int
  val note: Option[String]
  val activityStart: DateTime
  val activityEnd: DateTime

  def validate {
    validateLesserOrEqualThan("activityStart", activityStart, "activityEnd", activityEnd)
    validateMinValue("odometer", odometer, 0)
  }
}

case class MaintenanceActivity(idVehicle: Int, idOperator: Int, odometer: Int, note: Option[String], activityStart: DateTime, activityEnd: DateTime)
  extends MaintenanceActivityTrait
  with Model[MaintenanceActivityTrait]

case class MaintenanceActivityPersisted(id: Int, idVehicle: Int, idOperator: Int, odometer: Int, note: Option[String], activityStart: DateTime, activityEnd: DateTime, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends MaintenanceActivityTrait
  with Persisted[MaintenanceActivity]

object MaintenanceActivities
  extends CrudTable[MaintenanceActivityTrait, MaintenanceActivity, MaintenanceActivityPersisted]("MaintenanceActivities") {

  def idVehicle = column[Int]("idVehicle")
  def idOperator = column[Int]("idOperator")
  def odometer = column[Int]("odometer")
  def note = column[String]("note")
  def activityStart = column[DateTime]("activityStart")
  def activityEnd = column[DateTime]("activityEnd")

  def * = id ~ idVehicle ~ idOperator ~ odometer ~ note.? ~ activityStart ~ activityEnd ~ creationTime ~ modificationTime ~ version <> (MaintenanceActivityPersisted, MaintenanceActivityPersisted.unapply _)
  def forInsert = idVehicle ~ idOperator ~ odometer ~ note.? ~ activityStart ~ activityEnd ~ creationTime ~ modificationTime ~ version <> (
    { t => MaintenanceActivity(t._1, t._2, t._3, t._4, t._5, t._6) },
    { (o: MaintenanceActivity) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.idVehicle, o.idOperator, o.odometer, o.note, o.activityStart, o.activityEnd, now, now, 0))
      }
    })

  override protected implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("MaintenanceServicesNameUQ"))
      throw new ValidationException(e, "name", "Already in use")
    else
      throw e;
  }

  def update(uobj: models.MaintenanceActivityPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id) }
        yield d.idVehicle ~ d.idOperator ~ d.odometer ~ d.note.? ~ d.activityStart ~ d.activityEnd ~ d.creationTime ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.idVehicle, obj.idOperator, obj.odometer, obj.note, obj.activityStart, obj.activityEnd, now, now, obj.version + 1)) == 1
    }
  }
  def updateWithVersion(uobj: models.MaintenanceActivityPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id && d.version === obj.version) }
        yield d.idVehicle ~ d.idOperator ~ d.odometer ~ d.note.? ~ d.activityStart ~ d.activityEnd ~ d.creationTime ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.idVehicle, obj.idOperator, obj.odometer, obj.note, obj.activityStart, obj.activityEnd, now, now, obj.version + 1)) == 1
    }
  }
}