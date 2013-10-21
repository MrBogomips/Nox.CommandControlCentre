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

trait VehicleTypeTrait extends NamedEntityTrait

/**
  * Device Type Model
  */
case class VehicleType(name: String, displayName0: Option[String] = None, description: Option[String] = None, enabled: Boolean = true)
  extends NamedEntity[VehicleTypeTrait]

case class VehicleTypePersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends NamedEntityPersisted[VehicleType]

/**
  * VehicleTypes table mapper
  */
object VehicleTypes
  extends NamedEntityCrudTable[VehicleTypeTrait, VehicleType, VehicleTypePersisted]("VehicleTypes", "VehicleTypes_name_key") {

  def * = id ~ name ~ displayName.? ~ description.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (VehicleTypePersisted, VehicleTypePersisted.unapply _)
  def forInsert = name ~ displayName.? ~ description.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => VehicleType(t._1, t._2, t._3, t._4) },
    { (o: VehicleType) =>
      {
        import java.util.Date
        val now = new Timestamp(new Date().getTime())
        Some((o.name, Some(o.displayName), o.description, o.enabled, now, now, 0))
      }
    })
}





