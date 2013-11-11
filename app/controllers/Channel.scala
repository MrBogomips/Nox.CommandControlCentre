package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Channel extends Controller {
  def logisticIndex = WithCors("GET") {
    Action {
      import models.{ MqttLogisticChannels => Channels }
      val channels = Json.obj(
        "channels" -> Channels.find(Some(true)).map(c => c.name))
      Ok(channels)
    }
  }
  def functionalIndex = WithCors("GET") {
    Action {
      import models.{ MqttFunctionalChannels => Channels }
      val channels = Json.obj(
        "channels" -> Channels.find(Some(true)).map(c => c.name))
      Ok(channels)
    }
  }
}