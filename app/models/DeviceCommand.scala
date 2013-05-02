package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.Logger


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
  val creationTime = new java.util.Date
  lazy val creationTimeISO = {
    import java.util.TimeZone
    import java.text.SimpleDateFormat
    val tz = TimeZone.getTimeZone("UTC");
    val df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm'Z'")
    df.setTimeZone(tz)
    df.format(creationTime)
  }
  def sendToDevice(): DeviceCommandResponse = {	
    DeviceCommandResponseERR() 
  }
  
  def prepareSendingMessage: String = {
    val json = Json.toJson(this)
    json.toString
  }
}
object DeviceCommandRequest {
   import Argument._ 
   
	implicit val jsonRead: Reads[DeviceCommandRequest] = ( 
	  (__ \ 'deviceId).read[String] and
      (__ \ 'command).read[String] and
	  (__ \ 'arguments).read[Seq[Argument]]
	)(DeviceCommandRequest.apply _)
	
	implicit val jsonWrite: Writes[DeviceCommandRequest] = ( 
	  (__ \ 'deviceId).write[String] and
	  (__ \ 'command).write[String] and
      (__ \ 'sent_time).write[String] and
	  (__ \ 'arguments).write[Seq[Argument]]
	)(unlift(DeviceCommandRequest.unapply2 _))
	
   implicit val jsonFormat = Format(jsonRead, jsonWrite) 
   def unapply2(o: DeviceCommandRequest): Option[(String, String, String, Seq[Argument])] = 
     if (o != null) Some((o.deviceId,o.command, o.creationTimeISO, o.arguments)) else None
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