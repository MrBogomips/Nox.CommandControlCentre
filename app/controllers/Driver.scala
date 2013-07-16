package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models._
import utils.Converter._

import org.joda.time.format.ISODateTimeFormat

object Driver extends Secured {
  /**
    * DriverPersisted JSON serializer
    */
  implicit val driverJsonWriter = new Writes[DriverPersisted] {
    def writes(d: DriverPersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "surname" -> d.surname,
        "display_name" -> d.displayName,
        "enabled" -> d.enabled,
        "creation_time" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()))
    }
  }

  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    val drivers = all match {
      case false ⇒ Drivers.findAllEnabled
      case true  ⇒ Drivers.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(drivers))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.driver.index(drivers, user))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) ⇒
    Drivers.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.driver.item(d.id, user))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "surname" -> nonEmptyText(minLength = 3),
      "display_name" -> optional(text),
      "enabled" -> optional(text)))

  def create = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, surname, display_name, enabled) ⇒
          val d: Driver = display_name.fold(new Driver(name, surname, enabled)) {
            new Driver(name, surname, _, enabled)
          }
          val id = Drivers.insert(d)
          Ok(s"id=$id")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, surname, display_name, enabled) ⇒
          Drivers.findById(id).fold(NotFound("Driver $id wasn't found")) { d =>
            val d2 = d.copy(
              name = name,
              surname = surname,
              displayName = display_name.fold(d.displayName) { s => s },
              enabled = enabled)
            if (Drivers.update(d2) > 0)
              Ok(s"Driver $id updated successfully")
            else
              NotFound("")
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    if (Drivers.deleteById(id) > 0)
      Ok(s"Driver $id deleted successfully")
    else
      NotFound
  }
}