package poc.controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.Jsonp
import play.api.libs.json.Json

import java.sql.Timestamp
import org.joda.time.format.ISODateTimeFormat

import patterns.models._
import views._

import org.joda.time.DateTime

// Model trait
trait MyModelTrait extends Validatable {
  val name: String
  val surname0: Option[String]
  val surname: String = surname0.map(v => v).getOrElse(name)
  val description: Option[String]
  val enabled: Boolean
  val age: Int
  val dateOfBirth: org.joda.time.DateTime

  def validate {
    //validateMinLength("name", name, 3)
  }
}

// Model
case class MyModel(name: String, surname0: Option[String], description: Option[String], enabled: Boolean, age: Int, dateOfBirth: DateTime)
  extends MyModelTrait
  with Model[MyModelTrait]

// Persisted Model
case class MyModelPersisted(id: Int, name: String, surname0: Option[String], description: Option[String], enabled: Boolean, age: Int, dateOfBirth: DateTime, creationTime: Timestamp, modificationTime: Timestamp, version: Int)
  extends MyModelTrait
  with Persisted[MyModel]

object Fake {
  val data = collection.Map(
    1 -> MyModelPersisted(1, "Giovanni", Some("Costagliola"), None, true, 36, new DateTime(1976, 11, 2, 0, 0), new Timestamp(0), new Timestamp(0), 0),
    2 -> MyModelPersisted(2, "Michele", Some("Costagliola"), None, true, 35, new DateTime(1977, 10, 14, 0, 0), new Timestamp(0), new Timestamp(0), 0),
    3 -> MyModelPersisted(2, "Nausicaa", Some("Costagliola"), None, true, 11, new DateTime(2001, 9, 19, 0, 0), new Timestamp(0), new Timestamp(0), 0),
    4 -> MyModelPersisted(2, "Beatrice", Some("Costagliola"), None, true, 3, new DateTime(2010, 9, 22, 0, 0), new Timestamp(0), new Timestamp(0), 0))
}

object JsonApi extends Controller {

  import models.json.validationErrorJsonWriter

  // Model Reader
  implicit val modelReader: Reads[MyModel] = new Reads[MyModel] {
    def reads(js: JsValue): JsResult[MyModel] = try {
      JsSuccess(MyModel(
        (js \ "name").as[String],
        (js \ "surname").as[Option[String]],
        (js \ "description").as[Option[String]],
        (js \ "enabled").as[Boolean],
        (js \ "age").as[Int],
        (js \ "dateOfBirth").as[DateTime]))
    } catch {
      //case e: ValidationException => JsError(List(__ \ 'ciccio, List(ValidationError("", Set[String](""))))
      case e: ValidationException => JsError(__ \ 'isDead, e.validationErrors.toString)
    }
  }

  // Model Json Reader
  implicit val modelPersistedReader: Reads[MyModelPersisted] = new Reads[MyModelPersisted] {
    def reads(js: JsValue): JsResult[MyModelPersisted] = try {
      val mp = MyModelPersisted(
        (js \ "id").as[Int],
        (js \ "name").as[String],
        (js \ "surname").as[Option[String]],
        (js \ "description").as[Option[String]],
        (js \ "enabled").as[Boolean],
        (js \ "age").as[Int],
        (js \ "dateOfBirth").as[DateTime],
        new Timestamp(0), //(js \ "creationTime").as[Timestamp],
        new Timestamp(0), //(js \ "modificationTime").as[Timestamp],
        (js \ "version").as[Int])

      mp.requireValidation
      JsSuccess(mp)
    } catch {
      case e: ValidationException => JsError(__ \ 'isDead, e.validationErrors.toString)
    }
  }

  // Persisted Model Json Writer
  implicit val modelPersistedWriter: Writes[MyModelPersisted] = new Writes[MyModelPersisted] {
    def writes(o: MyModelPersisted): JsValue = {
      Json.obj(
        "id" -> o.id,
        "name" -> o.name,
        "surname" -> o.surname,
        "description" -> o.description,
        "enabled" -> o.enabled,
        "age" -> o.age,
        "dateOfBirth" -> ISODateTimeFormat.dateTime.print(o.dateOfBirth),
        "creationTime" -> ISODateTimeFormat.dateTime.print(o.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(o.modificationTime.getTime()),
        "version" -> o.version,
        "validationErrors" -> o.validationErrors)
    }
  }

  def index = Action {
    throw new ValidationException("ciccio", "buffo", "arlecchino")
    //throw new Throwable()
    Ok(Json.toJson(Fake.data.values))
  }

  def head = Action {
    Ok
  }

  def get(id: Int) = Action {
    Ok(Json.toJson(Fake.data(id)))
  }

  def post = Action(parse.json) { request =>
    request.body.validate[MyModelPersisted].map { mp =>
      Ok("")
    }.getOrElse(BadRequest(""))

  }

  def put = Action {
    Ok
  }

  def patch = Action {
    Ok
  }

  def delete(id: Int) = Action {
    Ok
  }
}