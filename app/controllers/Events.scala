package controllers

import scala.util.{ Try, Success, Failure }

import play.api._
import play.Logger
import play.api.libs.concurrent._
import play.api.libs.iteratee._
import play.api.libs.json._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import akka.actor._
import akka.pattern.AskTimeoutException

import actors.mqttactor._
import models.events.messages._

object Events extends Controller with Secured {

  lazy val akkaSys = ActorSystem("MqttProxySystem")

  class MqttListner(outChannel: play.api.libs.iteratee.Concurrent.Channel[JsValue]) extends Actor {
    private val conf = play.Configuration.root()
    val log = Logger.of(s"noxccc.MqttListner.$self")

    lazy val serverUri: String = conf.getString("nox.mqtt.BrokerURI")
    val clientId: String = ""
    var mqttActor: ActorRef = ActorRef.noSender

    override def preStart = {
      //(serverUri: String, clientId: String, persistence: MqttClientPersistence = null)
      mqttActor = context.actorOf(Props(classOf[MqttActor], serverUri, clientId, null))
      log.debug("Before connecting…")
      mqttActor ! Connect()
      log.debug("After connecting…")
    }

    def receive = {
      case json: JsValue => (json \ "command").as[String] match {
        case "subscribe" =>
          val topics = (json \ "topics").as[Seq[String]].map(ChannelFilter(_))
          log.debug(s"subscribe to topics $topics")
          mqttActor ! Subscribe(topics)
        case "unsubscribe" =>
          val topics = (json \ "topics").as[Seq[String]]
          log.debug(s"subscribe to topic $topics")
          mqttActor ! Unsubscribe(topics)
        case "push-message" =>
          val topic = (json \ "topic").as[String]
          log.debug(s"subscribe to topic $topic")
          val payload = (json \ "payload").as[JsValue]
          EventMessage.parse(topic, payload) match {
            case Success(message: EventMessage) => mqttActor ! message
            case Failure(error) => outChannel push Json.obj() // TODO
          }
        case "ping" => // just for diagnostic
          outChannel push Json.parse("""{"pong": true}""")
      }
      case MessageArrived(topic, message) =>
        outChannel push Json.parse(message.getPayload())
    }
  }

  def channel =
    WebSocket.using[JsValue] { implicit request =>

      user(request) match {
        // Found a user
        case Some(u) =>
          import play.api.libs.concurrent.Execution.Implicits._
          val log = Logger.of(s"noxccc.MqttListner.WebSocket")
          val (outEnumerator, outChannel) = Concurrent.broadcast[JsValue]
          val listnerRef = akkaSys.actorOf(Props(classOf[MqttListner], outChannel))
          val inIteratee = Iteratee.foreach[JsValue] { json =>
            listnerRef ! json
          }.map { _ =>
            import akka.pattern.gracefulStop
            import scala.concurrent.duration._
            import scala.concurrent.Await
            import scala.language.postfixOps

            try {
              val stopped = gracefulStop(listnerRef, 5 seconds)
              Await.result(stopped, 6 seconds)
            } catch {
              case _: AskTimeoutException => log.debug("Timeout passed while waiting for actor closing")
            }
          }
          (inIteratee, outEnumerator)

        // Not found a user
        case None =>
          val inIteratee = Iteratee.ignore[JsValue]
          val outEnumerator = Enumerator.eof[JsValue]
          (inIteratee, outEnumerator)
      }

    }

}