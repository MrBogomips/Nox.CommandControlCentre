package models

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.eclipse.paho.client.mqttv3._
import java.util.Date
import java.util.UUID

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
	      val clientId = MqttClient.generateClientId()
	
	      Logger.debug(s"MQTT: Client created... try to connect [$mqBrokerURI] on clientId[$clientId]");
	
	      val mqtt = new MqttClient(mqBrokerURI, clientId.substring(0, 22))
	
	      mqtt.connect();
	
	      val payload = Json.stringify(Json.toJson(cmdReqEnh));
	      
	      Logger.debug("MQTT: Payload=" + payload)
	
	      val message = new MqttMessage(payload.getBytes());
	
	      mqtt.publish(mqRequestTopic, message)
	
	      println("Command Sended! Disconnetting..");
	      mqtt.disconnect()
	      println("Disconnetted!!");
	
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

