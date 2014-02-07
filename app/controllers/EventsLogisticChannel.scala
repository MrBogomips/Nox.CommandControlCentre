package controllers

import models.{ MqttLogisticChannelTrait, MqttLogisticChannel => MqttLogisticChannelModel, MqttLogisticChannelPersisted, MqttLogisticChannels }
import models.json.MqttLogisticChannelPersistedSerializer

object EventsLogisticChannel extends NamedEntityController[MqttLogisticChannelTrait, MqttLogisticChannelModel, MqttLogisticChannelPersisted] {
  val pageTitle = "Logistic Channels"
  override lazy val createButton = "channel"

  val dataAccessObject = MqttLogisticChannels
  implicit val jsonSerializer = MqttLogisticChannelPersistedSerializer

  def modelBuilder(name: String, displayName: Option[String], description: Option[String], enabled: Boolean) = {
    MqttLogisticChannelModel(name, displayName, description, enabled)
  }

  def persistedBuilder(id: Int, name: String, displayName: Option[String], description: Option[String], enabled: Boolean, version: Int) = {
    MqttLogisticChannelPersisted(id, name, displayName, description, enabled, version = version)
  }
}