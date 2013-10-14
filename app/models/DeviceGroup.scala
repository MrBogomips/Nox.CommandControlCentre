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

trait DeviceGroupTrait extends NamedEntityTrait

/**
  * Device Group Model
  */
case class DeviceGroup(name: String, displayName0: Option[String] = None, description: Option[String] = None, enabled: Boolean = true)
  extends NamedEntity[DeviceGroupTrait]

case class DeviceGroupPersisted(id: Int, name: String, displayName0: Option[String], description: Option[String], enabled: Boolean, creationTime: Timestamp = new Timestamp(0), modificationTime: Timestamp = new Timestamp(0), version: Int)
  extends NamedEntityPersisted[DeviceGroup]

object DeviceGroups extends NamedEntities[DeviceGroupTrait, DeviceGroup, DeviceGroupPersisted]("DeviceGroups", "device_groups_name_key") {
  def * = id ~ name ~ displayName.? ~ description.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (DeviceGroupPersisted, DeviceGroupPersisted.unapply _)
  def forInsert = name ~ displayName.? ~ description.? ~ enabled ~ creationTime ~ modificationTime ~ version <> (
    { t => DeviceGroup(t._1, t._2, t._3, t._4) },
    { (o: DeviceGroup) =>
      {
        val now = new Timestamp(new Date().getTime())
        Some((o.name, Some(o.displayName), o.description, o.enabled, now, now, 0))
      }
    })
}