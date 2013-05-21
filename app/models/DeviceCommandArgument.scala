package models

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.eclipse.paho.client.mqttv3._
import java.util.Date

/** Represents n argument passed to the command */
case class DeviceCommandArgument(val name: String, val value: String)
object DeviceCommandArgument {
  implicit val jsonFormat = (
    (__ \ 'name).format[String] and
    (__ \ 'value).format[String])(DeviceCommandArgument.apply, unlift(DeviceCommandArgument.unapply))
}