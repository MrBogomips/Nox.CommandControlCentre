package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models._

import org.joda.time.format.ISODateTimeFormat

object Vehicle extends Secured {
  /**
   * DevicePersisted JSON serializer
   */
  implicit val vehicleJsonWriter = new Writes[VehiclePersisted] {
    def writes(r: VehiclePersisted): JsValue = {
      Json.obj(
        "id" -> r.id,
        "name" -> r.name,
        "display_name" -> r.displayName,
        "description" -> r.description,
        "enabled" -> r.enabled,
        "model" -> r.model,
        "license_plate" -> r.licensePlate,
        "creation_time" -> ISODateTimeFormat.dateTime.print(r.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(r.modificationTime.getTime()))
    }
  }

  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    val vehicles = all match {
      case false ⇒ Vehicles.findAllEnabled
      case true ⇒ Vehicles.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(vehicles))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.vehicle.index(vehicles, user))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) ⇒
    Vehicles.findById(id).map { v ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(v))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.vehicle.item(v.id, user))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "display_name" -> optional(text),
      "description" -> optional(text),
      "model" -> nonEmptyText(minLength = 3),
      "license_plate" -> nonEmptyText(minLength = 3),
      "enabled" -> optional(text)))

  def create = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson)as("application/json"),
      {
        case (name, display_name, description, model, license_plate, enabled) ⇒
          if (Vehicles.findByName(name).isDefined) {
            BadRequest("""{"name": ["A vehicle with the same name already exists"]}""").as("application/json")
          } else {
            var v = new Vehicle(name, model, license_plate)
            display_name.map(desc ⇒ v = v.copy(displayName = desc))
            v = v.copy(
              enabled = enabled match { case Some("on") ⇒ true case _ ⇒ false },
              description = description)
            val id = Vehicles.insert(v)
            Ok(s"id=id")
          }
      })
  }

  def update(id: Int) = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson)as("application/json"),
      {
        case (name, display_name, description, model, license_plate, enabled) ⇒
          Vehicles.findById(id).map { x ⇒
            var v = x
            display_name.map(desc ⇒ v = v.copy(displayName = desc))
            v = v.copy(
              name = name,
              description = description,
              model = model,
              licensePlate = license_plate,
              enabled = enabled match { case Some("on") ⇒ true case _ ⇒ false })
            Ok(s"Vehicle $id updated successfully")
            if (Vehicles.update(v)) {
              Ok(s"Vehicle $id updated successfully")
            } else {
              NotFound("Vehicle $id wasn't updated")
            }
          }.getOrElse(NotFound)
      })
  }

  def delete(id: Int) = WithAuthentication {
    Vehicles.deleteById(id) match {
      case true ⇒ Ok(s"Vehicle $id deleted successfully")
      case _ ⇒ NotFound
    }
  }
}