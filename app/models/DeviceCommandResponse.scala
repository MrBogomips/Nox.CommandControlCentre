package models

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.eclipse.paho.client.mqttv3._
import java.util.Date


/** Represents a command response sent to the web client */
class DeviceCommandResponse(val status: String, val tranId: String, val description: String)
object DeviceCommandResponse {
  implicit val jsonFormat = (
    (__ \ 'status).format[String] and
    (__ \ 'tranId).format[String] and
    (__ \ 'description).format[String])(DeviceCommandResponse.apply, unlift(DeviceCommandResponse.unapply))

  def apply(status: String, tranId: String, description: String = ""): DeviceCommandResponse = {
    require(status != null, "status cannot be null")
    status match {
      case "OK" => DeviceCommandResponseOK(tranId, description)
      case "ERR" => DeviceCommandResponseERR(tranId, description)
      case _ => new DeviceCommandResponse(status, tranId, description)
    }
  }

  def unapply(o: DeviceCommandResponse): Option[(String, String, String)] = if (o != null) Some((o.status, o.tranId, o.description)) else None
}

class DeviceCommandResponseOK(tranId: String, description: String) extends DeviceCommandResponse("OK", tranId, description)
object DeviceCommandResponseOK {
  def apply(tranId: String, description: String = "") = new DeviceCommandResponseOK(tranId, description)
}
class DeviceCommandResponseERR(tranId: String, description: String) extends DeviceCommandResponse("ERR", tranId, description)
object DeviceCommandResponseERR {
  def apply(tranId: String, description: String = "") = new DeviceCommandResponseERR(tranId, description)
}