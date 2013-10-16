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

trait MaintenanceServiceTrait extends NamedEntityTrait {
  val name: String
  val displayName0: Option[String]
  val description: Option[String]
  val odometer: Int
  val monthsPeriod: Int
  val enabled: Boolean

  override def validate {
    super.validate
    validateMinValue("odometer", odometer, 1)
    validateMinValue("monthsPeriod", monthsPeriod, 0)
  }
}

case class MaintenanceService(name: String, displayName0: Option[String], description: Option[String], odometer: Int, monthsPeriod: Int, enabled: Boolean)
  extends NamedEntity[MaintenanceServiceTrait]

case class MaintenanceServicePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], odometer: Int, monthsPeriod: Int, enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends NamedEntityPersisted[MaintenanceService]

object MaintenanceServices
  extends NamedEntityCrudTable[MaintenanceServiceTrait, MaintenanceService, MaintenanceServicePersisted]("MaintenanceServices", "MaintenanceServicesNameUQ") {

  // We map only specific fields
  def odometer = column[Int]("odometer")
  def monthsPeriod = column[Int]("monthsPeriod")
  
  def * = id ~ name ~ displayName.? ~ description.? ~ odometer ~ monthsPeriod ~ enabled ~ creationTime ~ modificationTime ~ version <> (MaintenanceServicePersisted, MaintenanceServicePersisted.unapply _)
  def forInsert = name ~ displayName.? ~ description.? ~ odometer ~ monthsPeriod ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => MaintenanceService(t._1, t._2, t._3, t._4, t._5, t._6) },
    { (o: MaintenanceService) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.name, Some(o.displayName), o.description, o.odometer, o.monthsPeriod, o.enabled, now, now, 0))
      }
    })

  // QUERY - BEGIN
  def qyFindByName(name: String) = (for { m <- MaintenanceServices if m.name like s"%$name%" } yield m)
  def qyFindByName2(name: String) = FinitePagination(10,0).paginate(qyFindByName(name))  // just an example of pagination
  def qyFindByNameAndSort(name: String) = qyFindByName(name).sortBy(_.creationTime).sortBy(_.modificationTime.desc)
  def qyFindByNameAndSort2(name: String) = qyFindByName(name).sortBy(r => (r.creationTime, r.modificationTime.desc))
  def qyFindByNameAndSort3(name: String) = qyFindByName(name).sortBy(r => (r.column[Int]("creationTime").asc, r.column[Int]("creationTime").asc))
  // QUERY - END

  override def update(uobj: models.MaintenanceServicePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id) } 
      	yield d.name ~ d.displayName ~ d.description.? ~ d.enabled ~ d.odometer ~ d.monthsPeriod ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.name, obj.displayName, obj.description, obj.enabled, obj.odometer, obj.monthsPeriod, now, obj.version + 1)) == 1
    }
  }
  override def updateWithVersion(uobj: models.MaintenanceServicePersisted): Boolean = WithValidation(uobj) { obj =>
    db withSession {
      val qy = for { d <- this if (d.id === obj.id && d.version === obj.version) }   
        yield d.name ~ d.displayName ~ d.description.? ~ d.enabled ~ d.odometer ~ d.monthsPeriod ~ d.modificationTime ~ d.version
      val now = new Timestamp(new Date().getTime())
      qy.update((obj.name, obj.displayName, obj.description, obj.enabled, obj.odometer, obj.monthsPeriod, now, obj.version + 1)) == 1    }
  }
  def findByName2(name: String): Seq[models.MaintenanceServicePersisted] = db withSession {
    FinitePagination(10,7).paginate(qyFindByNameAndSort2(name)).list
  }
}


