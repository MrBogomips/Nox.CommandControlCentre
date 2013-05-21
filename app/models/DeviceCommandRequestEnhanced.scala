package models

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.eclipse.paho.client.mqttv3._
import java.util.Date


/** Represents a command sent to the backend enhanced with extra info */
case class DeviceCommandRequestEnhanced(val device: String, val tranId: String, val appId: String, val userId: String, val sessionId: String, val replyOnTopics: Seq[String], val command: String, val arguments: Seq[DeviceCommandArgument]) {
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
  
  def this (commandReq: DeviceCommandRequest, appId: String, userId: String, sessionId: String, replyOnTopics: Seq[String]) =
    this(commandReq.device, commandReq.tranId, appId, userId, sessionId, replyOnTopics, commandReq.command, commandReq.arguments)
}
object DeviceCommandRequestEnhanced {
  import DeviceCommandArgument._

  implicit val jsonFormat = (
    (__ \ 'device).format[String] and
    (__ \ 'tranId).format[String] and
    (__ \ 'appId).format[String] and
    (__ \ 'userId).format[String] and
    (__ \ 'sessionId).format[String] and
    (__ \ 'replyOnTopics).format[Seq[String]] and
    (__ \ 'command).format[String] and
    (__ \ 'arguments).format[Seq[DeviceCommandArgument]])(DeviceCommandRequestEnhanced.apply, unlift(DeviceCommandRequestEnhanced.unapply))
}


