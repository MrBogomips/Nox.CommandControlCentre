package models.events.messages

import scala.util.Try
import play.api.libs.json.JsValue
import actors.mqttactor._
import org.eclipse.paho.client.mqttv3.MqttMessage

class EventMessage private[messages] (topic: String, payload: JsValue) extends PublishJson(topic, payload, 0, false) {
}

object EventMessage {
	def parse(topic: String, payload: JsValue): Try[EventMessage] = Try {
	  val messageType = (payload \ "message_type").as[String]
	  val messageSubtype = (payload \ "message_subtype").as[String]
	  
	  // TODO Validate payload
	  
	  new EventMessage(topic, payload)
	}
}