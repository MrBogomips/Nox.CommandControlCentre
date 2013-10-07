package models

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date
import java.util.UUID
import resource._

import mqtt.SimpleClient

/** Represents a command sent to the webapp */
case class DeviceCommandRequest(val device: String, /*val tranId: String, */ val command: String, val arguments: Seq[DeviceCommandArgument]) {
  private val conf = play.Configuration.root()

  /*
  val creationTime = new java.util.Date
  lazy val creationTimeISO = 
    import java.util.TimeZone
    import java.text.SimpleDateFormat
    val tz = TimeZone.getTimeZone("UTC");
    val df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm'Z'")
    df.setTimeZone(tz)
    df.format(creationTime)
  } 
  */

  val tranId = UUID.randomUUID().toString()

  def sendToDevice(): DeviceCommandResponse = {
    import globals._
    val cmdReqEnh = new DeviceCommandRequestEnhanced(this, Application.applicationId, Demo.userId, Demo.sessionId, List(replyTopic));
    implicit val fmt = DeviceCommandRequestEnhanced.jsonFormat

    def publishOnMqttBus(d: DeviceInfoPersisted): DeviceCommandResponse = {
      /** Call a MQTT Client to publish the message command */
      try {

        // 2013-10-08: introduced the concept of «device manager». Topics are forged
        // to provide routing information
        // Topic should be: <DEVICE_MANAGER_NAME>/<COMMAND_TOPIC>/<DEVICE_NAME> if a Device Manager is configured
        // or <DEVICE_NAME>/<COMMAND_TOPIC>/<DEVICE_NAME> in all other cases

        val mqRequestTopic = conf.getString("nox.mqtt.Command.RequestTopic")
        val mqBrokerURI = conf.getString("nox.mqtt.BrokerURI")
        val commandTopic = d.deviceManagerName.map { dmn =>
          	s"$dmn/$mqRequestTopic/${this.device}"
          }.getOrElse{
            s"${this.device}/$mqRequestTopic/${this.device}"
          }

        Logger.debug(s"""Topic used is $commandTopic""")
          
        for (mqtt <- managed(new SimpleClient(mqBrokerURI))) {
          mqtt.connect
          mqtt.publish(commandTopic, Json.toJson(cmdReqEnh))
        }

        DeviceCommandResponseOK(cmdReqEnh.tranId, "Command sent successfully")
      } catch {
        case e: Exception =>
          Logger.error("exception caught: "+e.printStackTrace())
          DeviceCommandResponseERR(cmdReqEnh.tranId, e.getMessage())
      }
    }

    // Todo: fetch the device manager
    Devices.findWithInfoByName(this.device).map { d =>
      publishOnMqttBus(d)
    }.getOrElse {
      val msg = s"""device identified by "${this.device}" not found."""
      Logger.error(msg)
      DeviceCommandResponseERR(cmdReqEnh.tranId, msg)
    }
  }

  private def replyTopic = globals.Demo.mqttSessionTopic
}
object DeviceCommandRequest {
  import DeviceCommandArgument._

  implicit val jsonFormat = (
    (__ \ 'device).format[String] and
    //(__ \ 'tranId).format[String] and
    (__ \ 'command).format[String] and
    (__ \ 'arguments).format[Seq[DeviceCommandArgument]])(DeviceCommandRequest.apply, unlift(DeviceCommandRequest.unapply))
}

