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

import models.json.{ vehiclePersistedJsonWriter, vehicleInfoPersistedJsonWriter }

object Vehicle extends Secured {
  
  lazy val ariaController: String = getThisClassSimpleName
  val pageTitle: String = "Vehicles"
    
  private def getThisClassSimpleName: String = {
    val s = this.getClass.getSimpleName()
    s.substring(0, s.length() - 1)
  }
  
  def index(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) =>
      implicit val req = request
      if (acceptsJson(request)) {
        val vehicles = all match {
	      case false => Vehicles.findWithInfo(Some(true))
	      case true  => Vehicles.findWithInfo(None)
	    }
        Ok(Json.toJson(vehicles))
      } else if (acceptsHtml(request)) {
//        Ok(views.html.aria.vehicle.index(user))
        Ok(views.html.aria.datatable.index(user,ariaController,pageTitle))
      } else {
        BadRequest
      }
    }
  }

  def get(id: Int) = WithCors("GET", "PUT", "DELETE") {
    WithAuthentication { (user, request) =>
      Vehicles.findById(id).map { v =>
        if (acceptsJson(request)) {
          Ok(Json.toJson(v))
        } else if (acceptsHtml(request)) {
          Ok(views.html.aria.vehicle.item(v.id, user))
        } else {
          BadRequest
        }
      }.getOrElse(NotFound);
    }
  }

  val createForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "model" -> nonEmptyText(minLength = 3),
      "licensePlate" -> nonEmptyText(minLength = 3),
      "vehicleId" -> optional(number),
      "enabled" -> optional(text)))
  val updateForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "model" -> nonEmptyText(minLength = 3),
      "licensePlate" -> nonEmptyText(minLength = 3),
      "vehicleId" -> optional(number),
      "enabled" -> optional(text),
      "version" -> number))

  def create = WithCors("POST") {
    WithAuthentication { implicit request =>
      createForm.bindFromRequest.fold(
        errors => BadRequest(errors.errorsAsJson).as("application/json"),
        {
          case (name, displayName, description, model, licensePlate, vehicleTypeId, enabled) =>
            val v = VehicleModel(name, displayName, description, enabled, model, licensePlate, vehicleTypeId)
            val id = Vehicles.insert(v)
            Ok(s"""{"id":$id}""").as("application/json")
        })
    }
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, model, licensePlate, vehicleTypeId, enabled, version) =>
          val vp = VehiclePersisted(id, name, displayName, description, enabled, model, licensePlate, vehicleTypeId, version = version)
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