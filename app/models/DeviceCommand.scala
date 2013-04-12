package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/** Represents n argument passed to the command */
case class Argument(name:String, value:String)
object Argument {
  implicit val jsonFormat = (
	(__ \ "name").format[String] and
    (__ \ "value").format[String]  
  )(Argument.apply, unlift(Argument.unapply))
}

/** Represents a command sent to a device */
case class DeviceCommand(deviceId: String, command: String, arguments: Seq[Argument])
object DeviceCommand {
   import Argument._   
   
   implicit val jsonFormat = ( 
	  (__ \ "deviceId").format[String] and
      (__ \ "command").format[String] and
	  (__ \ "arguments").format[Seq[Argument]]
	)(DeviceCommand.apply, unlift(DeviceCommand.unapply))
}