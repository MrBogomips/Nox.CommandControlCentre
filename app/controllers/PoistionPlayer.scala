package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.json._
import org.joda.time.DateTime
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

  /**
    * Returns an enumerator over the events of a specific device
    */
  def getDeviceHistoryEnumerator(deviceName: String, startTime: DateTime, skipFirstEventDelay: Boolean, minimumTimeOut: Int = 100): Enumerator[JsValue] = {
    val isoDate = org.joda.time.format.ISODateTimeFormat.date
    val isoTime = org.joda.time.format.ISODateTimeFormat.time

    val date: String = isoDate.print(startTime)
    val time: String = isoTime.print(startTime)
    val collectionName: String = s"${deviceName}_${date.replace("-", "")}"

    Logger.debug(s"Date: $date, Time: $time, Collection: $collectionName")

    // Retrieve a cursor on the filtered collection
    def fetchData = {
      val collection: JSONCollection = mongodb.collection[JSONCollection](collectionName)
      collection.find(Json.obj()).cursor[JsValue]
    }
    // Add delay between two consecutive events
    def delayer: Enumeratee[JsValue, JsValue] = {
      import org.joda.time.{ DateTime, Interval }
      import com.github.nscala_time.time.Imports._

      var lastTime: Option[DateTime] = skipFirstEventDelay match {
        case true  => None
        case false => Some(startTime)
      }
      Enumeratee.map {
        json =>
          val timeStamp: String = (json \ "data" \ "ts").as[String].replace(" ", "T")
          Logger.debug(s"Timestamp: $timeStamp")
          val curTime: DateTime = new DateTime(timeStamp)
          Logger.debug(s"Curtime: $curTime")

          try {
            lastTime match {
              case None => {} // No wait
              case Some(lastTime) => {
                val delay = new Interval(lastTime, curTime).toDurationMillis
                Logger.debug("delay: "+delay)
                val sleep = List(delay, minimumTimeOut).max
                Logger.debug("sleep: "+sleep)
                Thread.sleep(sleep)
              }
            }
          } catch {
            case ex: Throwable => {
              Logger.debug(ex.toString)
              Thread.sleep(minimumTimeOut)
            }
          }

          lastTime = Some(curTime)
          json
      }
    }

    val data: Cursor[JsValue] = fetchData
    data.enumerate().through(delayer)

  }

  def history: WebSocket[JsValue] = WebSocket.using[JsValue] { request =>
    //val deviceData = fetchData("dev_0", "20131011", "12:23:30")

    val in = Iteratee.ignore[JsValue]
    val out = getDeviceHistoryEnumerator("dev_0", new DateTime("2013-10-11T12:23:30"), false)

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
