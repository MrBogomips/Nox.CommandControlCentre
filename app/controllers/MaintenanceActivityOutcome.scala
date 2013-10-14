package controllers

import models.{ MaintenanceActivityOutcomeTrait, MaintenanceActivityOutcome => MaintenanceActivityOutcomeModel, MaintenanceActivityOutcomePersisted, MaintenanceActivityOutcomes }
import models.json.MaintenanceActivityOutcomePersistedSerializer

object MaintenanceActivityOutcome extends NamedEntityController[MaintenanceActivityOutcomeTrait, MaintenanceActivityOutcomeModel, MaintenanceActivityOutcomePersisted] {
  val pageTitle = "Maintenance Activity Outcome"

  val dataAccessObject = MaintenanceActivityOutcomes
  implicit val jsonSerializer = MaintenanceActivityOutcomePersistedSerializer

  def modelBuilder(name: String, displayName: Option[String], description: Option[String], enabled: Boolean) = {
    MaintenanceActivityOutcomeModel(name, displayName, description, enabled)
  }

  def persistedBuilder(id: Int, name: String, displayName: Option[String], description: Option[String], enabled: Boolean, version: Int) = {
    MaintenanceActivityOutcomePersisted(id, name, displayName, description, enabled, version = version)
  }
}