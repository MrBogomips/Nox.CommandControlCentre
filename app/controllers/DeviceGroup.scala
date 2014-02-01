package controllers

import models.{ DeviceGroupTrait, DeviceGroup => DeviceGroupModel, DeviceGroupPersisted, DeviceGroups }
import models.json.DeviceGroupPersistedSerializer

object DeviceGroup extends NamedEntityController[DeviceGroupTrait, DeviceGroupModel, DeviceGroupPersisted] {
  override lazy val ariaController = "DeviceGroup"
  val pageTitle = "Device Group"
  override lazy val playController = "DeviceGroup" // match the object name

  val dataAccessObject = DeviceGroups
  implicit val jsonSerializer = DeviceGroupPersistedSerializer

  def modelBuilder(name: String, displayName: Option[String], description: Option[String], enabled: Boolean) = {
    DeviceGroupModel(name, displayName, description, enabled)
  }

  def persistedBuilder(id: Int, name: String, displayName: Option[String], description: Option[String], enabled: Boolean, version: Int) = {
    DeviceGroupPersisted(id, name, displayName, description, enabled, version = version)
  }
}