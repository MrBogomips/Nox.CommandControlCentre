package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ MaintenanceActivity => MaintenanceActivityModel, MaintenanceActivityPersisted, MaintenanceActivities }

import org.joda.time.format.ISODateTimeFormat

import models.json.maintenanceActivityPersistedJsonWriter

object MaintenanceActivity extends Secured {
  def index = WithAuthentication { (user, request) =>
    implicit val req = request
    val activities = MaintenanceActivities.index
    
    if (acceptsJson(request)) {
      Ok(Json.toJson(activities))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.maintenanceactivity.index(activities, user))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) =>
    MaintenanceActivities.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.maintenanceactivity.item(d.id, user))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "idVehicle" -> number,
      "idOperator" -> number,
      "odometer" -> number,
      "note" -> optional(text),
      "activityStart" -> jodaDate("yyyy-MM-dd’T’hh:mm:ss’Z'"),
      "activityEnd" -> jodaDate("yyyy-MM-dd’T’hh:mm:ss’Z'")))
  val updateForm = Form(
    tuple(
      "idVehicle" -> number,
      "idOperator" -> number,
      "odometer" -> number,
      "note" -> optional(text),
      "activityStart" -> jodaDate("yyyy-MM-dd’T’hh:mm:ss’Z'"),
      "activityEnd" -> jodaDate("yyyy-MM-dd’T’hh:mm:ss’Z'"),
      "version" -> number))

  def create = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (idVehicle, idOperator, odometer, note, activityStart, activityEnd) =>
          val obj = MaintenanceActivityModel(idVehicle, idOperator, odometer, note, activityStart, activityEnd)
          val id = MaintenanceActivities.insert(obj)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (idVehicle, idOperator, odometer, note, activityStart, activityEnd, version) =>
          val obj = MaintenanceActivityPersisted(id, idVehicle, idOperator, odometer, note, activityStart, activityEnd, version = version)
          //Simcards.up
          MaintenanceActivities.update(obj) match {
            case true => Ok
            case _    => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    MaintenanceActivities.deleteById(id) match {
      case true => Ok
      case _    => NotFound
    }
  }
}