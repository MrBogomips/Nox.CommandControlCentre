package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ DeviceType => DeviceTypeModel, DeviceTypePersisted, DeviceTypes }

import java.sql.Timestamp

import org.joda.time.format.ISODateTimeFormat

import models.json.deviceTypePersistedJsonWriter

object DeviceType extends Secured {
  
  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    implicit val req = request
    val deviceTypes = all match {
      case false => DeviceTypes.find(Some(true))
      case true  => DeviceTypes.find(None)
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(deviceTypes))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.devicetype.index(deviceTypes, user))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) ⇒
    DeviceTypes.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.devicetype.item(d.id, user))
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
          val dt = DeviceTypeModel(name, displayName, description, enabled)
          val id = DeviceTypes.insert(dt)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, enabled, version) =>
          val dtp = DeviceTypePersisted(id, name, displayName, description, enabled, version = version)
          //Simcards.up
          DeviceTypes.update(dtp) match {
            case true => Ok
            case _    => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    DeviceTypes.deleteById(id) match {
      case true ⇒ Ok
      case _    ⇒ NotFound
    }
  }
}


