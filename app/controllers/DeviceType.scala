package controllers

import models.{ DeviceTypeTrait, DeviceType => DeviceTypeModel, DeviceTypePersisted, DeviceTypes }
import models.json.DeviceTypePersistedSerializer

object DeviceType extends NamedEntityController[DeviceTypeTrait, DeviceTypeModel, DeviceTypePersisted] {
  override lazy val ariaController = "devicetypes"
  val pageTitle = "Device Type"
  override lazy val playController = "DeviceType" // match the object name

  val dataAccessObject = DeviceTypes
  implicit val jsonSerializer = DeviceTypePersistedSerializer

  def modelBuilder(name: String, displayName: Option[String], description: Option[String], enabled: Boolean) = {
    DeviceTypeModel(name, displayName, description, enabled)
  }

  def persistedBuilder(id: Int, name: String, displayName: Option[String], description: Option[String], enabled: Boolean, version: Int) = {
    DeviceTypePersisted(id, name, displayName, description, enabled, version = version)
  }
}