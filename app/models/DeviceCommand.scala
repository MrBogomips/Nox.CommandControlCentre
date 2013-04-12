package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/** Represents the command sent to a device */
case class CommandInvokation(command: String, arguments: Seq[(String)])
  
object CommandInvokation {
   implicit val jsonFormat = (
	  (__ \ "command").format[String] and
	  (__ \ "arguments").format[Seq[(String)]]
	)(CommandInvokation.apply, unlift(CommandInvokation.unapply))
}

case class DeviceCommand(deviceId: String, command: CommandInvokation)
