package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/** Represents n argument passed to the command */
case class Argument(val name:String, val value:String)
object Argument {
  implicit val jsonFormat = (
	(__ \ 'name).format[String] and
    (__ \ 'value).format[String]  
  )(Argument.apply, unlift(Argument.unapply))
}

/** Represents a command sent to a device */
case class DeviceCommandRequest(val deviceId: String, val command: String, val arguments: Seq[Argument]) {
  def sendToDevice(): DeviceCommandResponse = {
    	DeviceCommandResponseERR() 
  }
}
object DeviceCommandRequest {
   import Argument._ 
   
   implicit val jsonFormat = ( 
	  (__ \ 'deviceId).format[String] and
      (__ \ 'command).format[String] and
	  (__ \ 'arguments).format[Seq[Argument]]
	)(DeviceCommandRequest.apply, unlift(DeviceCommandRequest.unapply))
}

/** Represents a command response sent to the web client */
class DeviceCommandResponse(val status: String, val description: String)
object DeviceCommandResponse {
  implicit val jsonFormat = (
    (__ \ 'status).format[String] and
    (__ \ 'description).format[String]
  )(DeviceCommandResponse.apply, unlift(DeviceCommandResponse.unapply))
  
  def apply(status: String, description: String = ""): DeviceCommandResponse = {
	require(status != null, "status cannot be null")
    status match {
     case "OK"  => DeviceCommandResponseOK(description)
     case "ERR" => DeviceCommandResponseERR(description)
     case _ 	=> new DeviceCommandResponse(status, description)
   }
  }
  
  def unapply(o: DeviceCommandResponse): Option[(String, String)] = if (o != null) Some((o.status,o.description)) else None
}

class DeviceCommandResponseOK(description: String) extends DeviceCommandResponse("OK", description)
object DeviceCommandResponseOK {
  def apply(description: String = "") = new DeviceCommandResponseOK(description)
}
class DeviceCommandResponseERR(description: String) extends DeviceCommandResponse("ERR", description)
object DeviceCommandResponseERR {
  def apply(description: String = "") = new DeviceCommandResponseERR(description)
}