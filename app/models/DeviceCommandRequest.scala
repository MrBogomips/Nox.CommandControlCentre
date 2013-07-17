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
    
    def publishOnMqttBus: DeviceCommandResponse = {
	    /** Call a MQTT Client to publish the message command */
	    try {
	
	      val mqRequestTopic = conf.getString("nox.mqtt.Command.RequestTopic")
	      val mqBrokerURI = conf.getString("nox.mqtt.BrokerURI")
	      
	      for(mqtt <- managed(new SimpleClient(mqBrokerURI))) {
	        mqtt.connect
	        mqtt.publish(mqRequestTopic, Json.toJson(cmdReqEnh))
	      }
	      
	      DeviceCommandResponseOK(cmdReqEnh.tranId, "Command sent successfully")
	    } catch {
	      case e: Exception =>
	        Logger.error("exception caught: " + e.printStackTrace())
	        DeviceCommandResponseERR(cmdReqEnh.tranId, e.getMessage())
	    }
	  }
    
    publishOnMqttBus
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

