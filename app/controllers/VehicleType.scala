package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ VehicleType => VehicleTypeModel, VehicleTypePersisted, VehicleTypes }

import java.sql.Timestamp

import org.joda.time.format.ISODateTimeFormat

import models.json.vehicleTypePersistedJsonWriter

object VehicleType extends Secured {
  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    implicit val req = request
    val objs = all match {
      case false => VehicleTypes.find(Some(true))
      case true  => VehicleTypes.find(None)
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(objs))
    } else if (acceptsHtml(request)) {
      //Ok(views.html.aria.devicetype.index(objs, user))
      ???
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) ⇒
    VehicleTypes.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        //Ok(views.html.aria.devicetype.item(d.id, user))
        ???
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "enabled" -> boolean))
  val updateForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "enabled" -> boolean,
      "version" -> number))

  def create = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, enabled) =>
          val dt = VehicleTypeModel(name, displayName, description, enabled)
          val id = VehicleTypes.insert(dt)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, enabled, version) =>
          val dtp = VehicleTypePersisted(id, name, displayName, description, enabled, version = version)
          //Simcards.up
          VehicleTypes.update(dtp) match {
            case true => Ok
            case _    => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    VehicleTypes.deleteById(id) match {
      case true ⇒ Ok
      case _    ⇒ NotFound
    }
  }
}


