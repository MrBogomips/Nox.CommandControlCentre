import play.api.libs.json._
import play.api.libs.functional.syntax._

import mqtt.SimpleClient
import patterns.disposable._

package object notifications {
  private val conf = play.Configuration.root()
  def notifyDeviceChangeConfiguration(deviceName: String) = WithDisposition(new SimpleClient) { mqtt =>
    mqtt.connect
    val json = Json.obj(
      "device" -> "server",
      "command" -> "reset-device",
      "parameters" -> List(deviceName))
    val mqRequestTopic = conf.getString("nox.mqtt.Command.RequestTopic")
    mqtt.publish(mqRequestTopic, json)
  }
}