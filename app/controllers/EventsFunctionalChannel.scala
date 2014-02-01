package controllers

import models.{ MqttFunctionalChannelTrait, MqttFunctionalChannel => MqttFunctionalChannelModel, MqttFunctionalChannelPersisted, MqttFunctionalChannels }
import models.json.MqttFunctionalChannelPersistedSerializer

object EventsFunctionalChannel extends NamedEntityController[MqttFunctionalChannelTrait, MqttFunctionalChannelModel, MqttFunctionalChannelPersisted] {
  val pageTitle = "Functional Channels"
  override lazy val createButton = "channel"

  val dataAccessObject = MqttFunctionalChannels
  implicit val jsonSerializer = MqttFunctionalChannelPersistedSerializer

  def modelBuilder(name: String, displayName: Option[String], description: Option[String], enabled: Boolean) = {
    MqttFunctionalChannelModel(name, displayName, description, enabled)
  }

  def persistedBuilder(id: Int, name: String, displayName: Option[String], description: Option[String], enabled: Boolean, version: Int) = {
    MqttFunctionalChannelPersisted(id, name, displayName, description, enabled, version = version)
  }
}