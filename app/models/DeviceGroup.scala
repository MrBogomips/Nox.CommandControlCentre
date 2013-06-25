package models

import play.api.Logger
import play.api.db._
import play.api.Play.current

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

trait DeviceGroupTrait extends NamedEntityTrait

/**
 * Device Type Model
 */
case class DeviceGroup(private val initName: String, displayName: String, description: Option[String], enabled: Boolean)
  extends DeviceGroupTrait {
  lazy val name = normalizeName(initName)

  def this(name: String, description: Option[String] = None, enabled: Boolean = true) = this(name, name, description, enabled)

  override def toString = s"DeviceGroup($name,$displayName,$description)"

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled) =
    DeviceGroup(name, displayName, description, enabled)
}

/**
 * DeviceTypes table mapper
 */
object DeviceGroups extends SimpleNameEntityTable[DeviceGroupPersisted, DeviceGroup]("device_groups", DeviceGroupPersisted, DeviceGroupPersisted.unapply)

/**
 * Device Type Persisted
 */
case class DeviceGroupPersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends SimpleNameEntityPersisted[DeviceGroupPersisted, DeviceGroup] 
  with DeviceGroupTrait
{
  val entityTableMapper = DeviceGroups
  
  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled) =
    prepareCopy {
      DeviceGroupPersisted(id, name, displayName, description, enabled, creationTime, modificationTime, version)
    }
}

