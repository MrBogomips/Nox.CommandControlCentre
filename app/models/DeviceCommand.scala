package models

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.eclipse.paho.client.mqttv3._
import java.util.Date

/** Represents n argument passed to the command */
case class Argument(val name: String, val value: String)
object Argument {
  implicit val jsonFormat = (
    (__ \ 'name).format[String] and
    (__ \ 'value).format[String])(Argument.apply, unlift(Argument.unapply))
}

/** Represents a command sent to a device */
case class DeviceCommandRequest(val device: String, val tranId: String, val command: String, val arguments: Seq[Argument]) {
  private val conf = play.Configuration.root()

  val creationTime = new java.util.Date
  lazy val creationTimeISO = {
    import java.util.TimeZone
    import java.text.SimpleDateFormat
    val tz = TimeZone.getTimeZone("UTC");
    val df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm'Z'")
    df.setTimeZone(tz)
    df.format(creationTime)
  }

  def sendToDevice(jsReq: JsValue): DeviceCommandResponse = {
    /** Call a MQTT Client to publish the message command */
    println("device [" + device + "] command[" + command + "]");
    try {
      //Setting ClientId to transactionId putted in the message.

      val mqRequestTopic = conf.getString("nox.mqtt.Command.RequestTopic")
      val mqBrokerURI = conf.getString("nox.mqtt.BrokerURI")
      val clientId = MqttClient.generateClientId()

      Logger.debug("Client created..try to connect [" + mqBrokerURI + "] on clientId[" + clientId + "]");

      val mqtt = new MqttClient(mqBrokerURI, clientId.substring(0, 22))

      mqtt.connect();

      val payload = Json.stringify(jsReq);

      val message = new MqttMessage(payload.getBytes());

      mqtt.publish(mqRequestTopic, message)

      println("Command Sended! Disconnetting..");
      mqtt.disconnect()
      println("Disconnetted!!");

      DeviceCommandResponseOK("Command Sended successfully")
    } catch {
      case e: Exception =>
        Logger.error("exception caught: " + e.printStackTrace());
        DeviceCommandResponseERR("MAnnaggia la bubbazza")
    }

  }
}
object DeviceCommandRequest {
  import Argument._

  implicit val jsonFormat = (
    (__ \ 'device).format[String] and
    (__ \ 'tranId).format[String] and
    (__ \ 'command).format[String] and
    (__ \ 'arguments).format[Seq[Argument]])(DeviceCommandRequest.apply, unlift(DeviceCommandRequest.unapply))
}

/** Represents a command response sent to the web client */
class DeviceCommandResponse(val status: String, val description: String)
object DeviceCommandResponse {
  implicit val jsonFormat = (
    (__ \ 'status).format[String] and
    (__ \ 'description).format[String])(DeviceCommandResponse.apply, unlift(DeviceCommandResponse.unapply))

  def apply(status: String, description: String = ""): DeviceCommandResponse = {
    require(status != null, "status cannot be null")
    status match {
      case "OK" => DeviceCommandResponseOK(description)
      case "ERR" => DeviceCommandResponseERR(description)
      case _ => new DeviceCommandResponse(status, description)
    }
  }

  def unapply(o: DeviceCommandResponse): Option[(String, String)] = if (o != null) Some((o.status, o.description)) else None
}

class DeviceCommandResponseOK(description: String) extends DeviceCommandResponse("OK", description)
object DeviceCommandResponseOK {
  def apply(description: String = "") = new DeviceCommandResponseOK(description)
}
class DeviceCommandResponseERR(description: String) extends DeviceCommandResponse("ERR", description)
object DeviceCommandResponseERR {
  def apply(description: String = "") = new DeviceCommandResponseERR(description)
}