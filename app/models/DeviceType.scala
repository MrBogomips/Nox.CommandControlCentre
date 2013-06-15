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

trait DeviceTypeTrait extends NamedEntityTrait

/**
 * Device Type Model
 */
case class DeviceType(private val initName: String, displayName: String, description: Option[String], enabled: Boolean)
  extends DeviceTypeTrait {
  lazy val name = normalizeName(initName)

  def this(name: String, description: Option[String] = None, enabled: Boolean = true) = this(name, name, description, enabled)

  override def toString = s"DeviceType($name,$displayName,$description)"

  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled) =
    DeviceType(name, displayName, description, enabled)
}

/**
 * DeviceTypes table mapper
 */
object DeviceTypes extends SimpleNameEntityTable[DeviceTypePersisted, DeviceType]("device_types", DeviceTypePersisted, DeviceTypePersisted.unapply)

/**
 * Device Type Persisted
 */
case class DeviceTypePersisted private[models] (id: Int, name: String, displayName: String, description: Option[String], enabled: Boolean, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends SimpleNameEntityPersisted[DeviceTypePersisted, DeviceType] {

  val entityTableMapper = DeviceTypes
  
  def copy(name: String = this.name, displayName: String = this.displayName, description: Option[String] = this.description, enabled: Boolean = this.enabled) =
    prepareCopy {
      DeviceTypePersisted(id, name, displayName, description, enabled, creationTime, modificationTime, version)
    }
}




