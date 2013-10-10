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

trait MaintenanceServiceTrait extends Validatable {
  val name: String
  val displayName0: Option[String]
  val displayName: String = displayName0.map(v => v).getOrElse(name)
  val description: Option[String]
  val odometer: Int
  val monthsPeriod: Int
  val enabled: Boolean

  def validate {
    validateMinLength("name", name, 3)
    validateMinLength("displayName", displayName, 3)
    validateMinValue("odometer", odometer, 1)
    validateMinValue("monthsPeriod", monthsPeriod, 0)
  }
}

case class MaintenanceService(name: String, displayName0: Option[String], description: Option[String], odometer: Int, monthsPeriod: Int, enabled: Boolean)
  extends MaintenanceServiceTrait
  with Model[MaintenanceServiceTrait]

case class MaintenanceServicePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], odometer: Int, monthsPeriod: Int, enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends MaintenanceServiceTrait
  with Persisted[MaintenanceService]

object MaintenanceServices
  extends Table[MaintenanceServicePersisted]("MaintenanceServices")
  with Backend
  with NameEntityCrudOperations[MaintenanceServiceTrait, MaintenanceService, MaintenanceServicePersisted] {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def displayName = column[String]("displayName")
  def description = column[String]("description")
  def odometer = column[Int]("odometer")
  def monthsPeriod = column[Int]("monthsPeriod")
  def enabled = column[Boolean]("enabled")
  def creationTime = column[Timestamp]("creationTime")
  def modificationTime = column[Timestamp]("modificationTime")
  def version = column[Int]("version")

  def * = id ~ name ~ displayName.? ~ description.? ~ odometer ~ monthsPeriod ~ enabled ~ creationTime ~ modificationTime ~ version <> (MaintenanceServicePersisted, MaintenanceServicePersisted.unapply _)
  def forInsert = name ~ displayName.? ~ description.? ~ odometer ~ monthsPeriod ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => MaintenanceService(t._1, t._2, t._3, t._4, t._5, t._6) },
    { (o: MaintenanceService) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.name, Some(o.displayName), o.description, o.odometer, o.monthsPeriod, o.enabled, now, now, 0))
      }
    })

  private implicit val exceptionToValidationErrorMapper: (PSQLException => Nothing) = { e =>
    val errMessage = e.getMessage()
    if (errMessage.contains("MaintenanceServicesNameUQ"))
      throw new ValidationException(e, "name", "Already in use")
    else
      throw e;
  }

  // QUERY - BEGIN
  def qyFindByName(name: String) = (for { m <- MaintenanceServices if m.name like s"%$name%" } yield m)
  def qyFindByName2(name: String) = FinitePagination(10,0).paginate(qyFindByName(name))  // just an example of pagination
  def qyFindByNameAndSort(name: String) = qyFindByName(name).sortBy(_.creationTime).sortBy(_.modificationTime.desc)
  def qyFindByNameAndSort2(name: String) = qyFindByName(name).sortBy(r => (r.creationTime, r.modificationTime.desc))
  def qyFindByNameAndSort3(name: String) = qyFindByName(name).sortBy(r => (r.column[Int]("creationTime").asc, r.column[Int]("creationTime").asc))
  // QUERY - END

  // Members declared in patterns.models.CrudOperations
  def find(enabled: Option[Boolean]): Seq[models.MaintenanceServicePersisted] = db withSession {
    val qy = enabled match {
      case None     => for { d <- MaintenanceServices } yield d
      case Some(en) => for { d <- MaintenanceServices if (d.enabled === en) } yield d
    }
    qy.sortBy(r => r.displayName.asc).list
  }
  // Members declared in patterns.models.NameEntityCrudOperations
  def findByName(name: String): Option[models.MaintenanceServicePersisted] = db withSession {
    val qy = for { d <- MaintenanceServices if (d.name === name) } yield d
    qy.firstOption
  }

  def findEx: Seq[models.MaintenanceServicePersisted] = db withSession {
    val qy = {
      for { m <- MaintenanceServices } yield m
    }.sortBy(r => (r.displayName.asc, r.enabled.desc))
    qy.list
  }
  def findById(id: Int): Option[models.MaintenanceServicePersisted] = db withSession {
    val qy = for { d <- MaintenanceServices if (d.id === id) } yield d
    qy.firstOption
  }
  def deleteById(id: Int): Boolean = db withSession {
    val qy = for { d <- MaintenanceServices if (d.id === id) } yield d
    qy.delete == 1
  }
  def insert(uobj: models.MaintenanceService): Int = WithValidation(uobj) { obj =>
    db withSession {
      MaintenanceServices.forInsert returning MaintenanceServices.id insert obj
    }
  }

  def update(uobj: models.MaintenanceServicePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      /* really nice but will reset the creation time
      val qy = for { d <- MaintenanceServices if (d.id === obj.id) } yield d
      qy.update(prepareForUpdate(obj)) == 1
      */
      val qy = for { d <- MaintenanceServices if (d.id === obj.id) } 
      	yield d.name ~ d.displayName ~ d.description.? ~ d.enabled ~ d.odometer ~ d.monthsPeriod ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.name, obj.displayName, obj.description, obj.enabled, obj.odometer, obj.monthsPeriod, now, obj.version + 1)) == 1
    }
  }
  def updateWithVersion(uobj: models.MaintenanceServicePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- MaintenanceServices if (d.id === obj.id && d.version === obj.version) }   
        yield d.name ~ d.displayName ~ d.description.? ~ d.enabled ~ d.odometer ~ d.monthsPeriod ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.name, obj.displayName, obj.description, obj.enabled, obj.odometer, obj.monthsPeriod, now, obj.version + 1)) == 1    }
  }
  def findByName2(name: String): Seq[models.MaintenanceServicePersisted] = db withSession {
    FinitePagination(10,7).paginate(qyFindByNameAndSort2(name)).list
  }
}


