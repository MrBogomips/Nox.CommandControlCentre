package mqtt

import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.eclipse.paho.client.mqttv3._
import patterns.disposable._

/**
  * A very simple MQTT client
  */
class SimpleClient(brokerUri: String, clientId: String)
  extends Disposable {

  private val conf = play.Configuration.root()
  
  def this(brokerUri: String) = this(brokerUri, SimpleClient.generateClientId)
  def this() = this(SimpleClient.defaultBrokerUri, SimpleClient.generateClientId)

  require(clientId.length() <= 22, "clientId length must be less or equal to 22 characters")

  private def logMethod[A](method: String, message: String ="")(f: => A) = try {
    Logger.debug(s"mqtt.SimpleClient.$method()[$message]... try")
    val r = f
    Logger.debug(s"mqtt.SimpleClient.$method()... success")
    r
  } catch {
    case e: Throwable =>
      Logger.error(s"mqtt.SimpleClient.$method() $this")
      throw e;
  } finally {
    Logger.debug(s"mqtt.SimpleClient.$method()... exit")
  }
  
  val mqtt = new MqttClient(brokerUri, clientId)

  def isConnected = mqtt.isConnected()

  def connect: Unit = logMethod("connect") {
    mqtt.connect()
  }
  
  def disconnect: Unit = logMethod("disconnect") {
    mqtt.disconnect()
  }

  def dispose = if (isConnected) disconnect

  def publish(topic: String, message: String): Unit = logMethod("publish") {
    if (conf.getBoolean("nox.mqtt.logging.log_message"))
      Logger.debug(s"  >>> topic: $topic, value: $message")
      
    val m = new MqttMessage(message.getBytes());
    mqtt.publish(topic, m)
  }

  def publish(topic: String, json: JsValue): Unit = logMethod("publish", s"topic: $topic, value: {JSON}") {
    publish(topic, Json.stringify(json))
  }

  override def toString = s"SimpleClient($brokerUri, $clientId)"
}

object SimpleClient {
  private val conf = play.Configuration.root()
  def generateClientId: String = { // MqttClient constructors requires a 22 long string
    val t = MqttClient.generateClientId()
    if (t.length() > 22)
      t.substring(0, 22)
    else
      t
  }
  lazy val defaultBrokerUri: String = conf.getString("nox.mqtt.BrokerURI")
}