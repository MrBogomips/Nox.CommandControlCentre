package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ Vehicle => VehicleModel, VehiclePersisted, Vehicles }

import utils.Converter._

import org.joda.time.format.ISODateTimeFormat

import models.json.vehiclePersistedJsonWriter

object Vehicle extends Secured {
  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    implicit val req = request
    val vehicles = all match {
      case false => Vehicles.find(Some(true))
      case true  => Vehicles.find(None)
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
  val updateForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "display_name" -> optional(text),
      "description" -> optional(text),
      "model" -> nonEmptyText(minLength = 3),
      "license_plate" -> nonEmptyText(minLength = 3),
      "enabled" -> optional(text),
      "version" -> number))

  def create = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, model, licensePlate, enabled) =>
          val v = VehicleModel(name, displayName, description, enabled, model, licensePlate)
          val id = Vehicles.insert(v)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, model, licensePlate, enabled, version) =>
          val vp = VehiclePersisted(id, name, displayName, description, enabled, model, licensePlate, version = version)
          //Simcards.up
          Vehicles.update(vp) match {
            case true => Ok
            case _    => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    Vehicles.deleteById(id) match {
      case true ⇒ Ok
      case _    ⇒ NotFound
    }
  }

}