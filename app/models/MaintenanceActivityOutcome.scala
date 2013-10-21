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

trait MaintenanceActivityOutcomeTrait extends NamedEntityTrait

/**
  * Device Group Model
  */
case class MaintenanceActivityOutcome(name: String, displayName0: Option[String] = None, description: Option[String] = None, enabled: Boolean = true)
  extends NamedEntity[MaintenanceActivityOutcomeTrait]

case class MaintenanceActivityOutcomePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends NamedEntityPersisted[MaintenanceActivityOutcome]

object MaintenanceActivityOutcomes extends NamedEntityCrudTable[MaintenanceActivityOutcomeTrait, MaintenanceActivityOutcome, MaintenanceActivityOutcomePersisted]("MaintenanceActivityOutcomes", "MaintenanceActivityOutcomesNameUQ") {
  def * = id ~ name ~ displayName.? ~ description.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (MaintenanceActivityOutcomePersisted, MaintenanceActivityOutcomePersisted.unapply _)
  def forInsert = name ~ displayName.? ~ description.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => MaintenanceActivityOutcome(t._1, t._2, t._3, t._4) },
    { (o: MaintenanceActivityOutcome) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.name, Some(o.displayName), o.description, o.enabled, now, now, 0))
      }
    })
}