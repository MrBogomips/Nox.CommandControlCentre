package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.json._
import org.joda.time.format.ISODateTimeFormat
import patterns.models.ValidationException
import play.api.libs._
import play.api.libs.iteratee._
import play.api.Play.current
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent._

import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api._

object PositionPlayer extends Secured with MongoController {

  val mongodb = ReactiveMongoPlugin.db

  def mongo: WebSocket[JsValue] = WebSocket.using[JsValue] { request =>
    val json: JsValue = Json.obj("ciccio" -> "buffo")

    val in = Iteratee.ignore[JsValue]
    val out = Enumerator(json)
    (in, out)
  }

  def history: WebSocket[JsValue] = WebSocket.using[JsValue] { request =>
    val collection: JSONCollection = mongodb.collection[JSONCollection]("dev_0_20131011")
    val query = collection.find(Json.obj()).cursor[JsValue]

    val in = Iteratee.ignore[JsValue]
    val out = query.enumerate()

    (in, out)
  }

  def index = Action {
    Async {
      val collection: JSONCollection = mongodb.collection[JSONCollection]("dev_0_20131011")
      val cursor: Cursor[JsObject] = collection.find(Json.obj()).cursor[JsObject]

      val positions = cursor.toList(100).map { p => Json.arr(p) }

      positions.map(l => Ok(l(0)))
    }
  }
}
