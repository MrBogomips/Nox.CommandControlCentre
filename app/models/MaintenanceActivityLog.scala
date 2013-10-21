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

import com.github.nscala_time.time.Imports._
import com.github.tototoshi.slick.JodaSupport._
/**
  * Maintenance activity
  */
trait MaintenanceActivityLogTrait extends Validatable {
  val idActivity: Int
  val idService: Int
  val idOutcome: Int
  val note: Option[String]

  def validate {
    
  }
}

case class MaintenanceActivityLog(idActivity: Int, idService: Int, idOutcome: Int, note: Option[String])
  extends MaintenanceActivityLogTrait
  with Model[MaintenanceActivityLogTrait]

case class MaintenanceActivityLogPersisted(id: Int, idActivity: Int, idService: Int, idOutcome: Int, note: Option[String], creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends MaintenanceActivityLogTrait
  with Persisted[MaintenanceActivityLog]


object MaintenanceActivityLogs
  extends CrudTable[MaintenanceActivityLogTrait, MaintenanceActivityLog, MaintenanceActivityLogPersisted]("MaintenanceActivityLog") {

  def idActivity = column[Int]("idActivity")
  def idService = column[Int]("idService")
  def idOutcome = column[Int]("idOutcome")
  def note = column[String]("note")
  
  def * = id ~ idActivity ~ idService ~ idOutcome ~ note.? ~ creationTime ~ modificationTime ~ version <> (MaintenanceActivityLogPersisted, MaintenanceActivityLogPersisted.unapply _)
  def forInsert = idActivity ~ idService ~ idOutcome ~ note.? ~ creationTime ~ modificationTime ~ version <> (
    { t => MaintenanceActivityLog(t._1, t._2, t._3, t._4) },
    { (o: MaintenanceActivityLog) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.idActivity, o.idService, o.idOutcome, o.note, now, now, 0))
      }
    })

  override protected implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("MaintenanceActivityLogsAK"))
      throw new ValidationException(e, "idActivity,idService", "Already associated")
    else
      throw e;
  }
  def update(uobj: models.MaintenanceActivityLogPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id) }
        yield d.idActivity ~ d.idService ~ d.idOutcome ~ d.note.? ~ d.creationTime ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.idActivity, obj.idService, obj.idOutcome, obj.note, now, now, obj.version + 1)) == 1
    }
  }
  def updateWithVersion(uobj: models.MaintenanceActivityLogPersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id && d.version === obj.version) }
        yield d.idActivity ~ d.idService ~ d.idOutcome ~ d.note.? ~ d.creationTime ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.idActivity, obj.idService, obj.idOutcome, obj.note, now, now, obj.version + 1)) == 1
    }
  }
}
