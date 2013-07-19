package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{DeviceGroup => DeviceGroupModel, DeviceGroupPersisted, DeviceGroups}

import java.sql.Timestamp

import org.joda.time.format.ISODateTimeFormat

import models.json.deviceGroupPersistedJsonWriter

object DeviceGroup extends Secured {
  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    implicit val req = request
    val deviceGroups = all match {
      case false => DeviceGroups.find(Some(true))
      case true => DeviceGroups.find(None)
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(deviceGroups))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.devicegroup.index(deviceGroups, user))
    } else {
      BadRequest
    }
  }
  
  def get(id: Int) = WithAuthentication { (user, request) ⇒
    DeviceGroups.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.devicegroup.item(d.id, user))
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
          val dt = DeviceGroupModel(name, displayName, description, enabled)
          val id = DeviceGroups.insert(dt)
          Ok(s"""{"id"=id}""")
      })
  }
  
  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, enabled, version) =>
          val dtp = DeviceGroupPersisted(id, name, displayName, description, enabled, version = version)
          //Simcards.up
          DeviceGroups.update(dtp) match {
            case true => Ok(s"DeviceGroup $id updated successfully")
            case _ => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    DeviceGroups.deleteById(id) match {
      case true ⇒ Ok(s"Device Group $id deleted successfully")
      case _ ⇒ NotFound
    }
  }
}


