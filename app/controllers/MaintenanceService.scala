package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ MaintenanceServices, MaintenanceService ⇒ MaintenanceServiceModel, MaintenanceServicePersisted }
import org.joda.time.format.ISODateTimeFormat
import patterns.models.ValidationException

object MaintenanceService extends Secured {

  import models.json.{ maintenanceServicePersistedJsonWriter }

  def index2(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) =>
      implicit val req = request
      val services = all match {
        case false => MaintenanceServices.find(Some(true))
        case true  => MaintenanceServices.find(None)
      }
      if (acceptsJson(request)) {
        Ok(Json.toJson(services))
      } else if (acceptsHtml(request)) {
        ??? // Ok(views.html.aria.device.index(devices, user))
      } else {
        BadRequest
      }
    }
  }

  def index(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) =>
      implicit val req = request

      /*
    MaintenanceServices.db withSession {
      val out = MaintenanceServices.findByName2("ciccio")
      Ok(Json.toJson(out))
    }
    * */

      val services = all match {
        case false => MaintenanceServices.find(Some(true))
        case true  => MaintenanceServices.find(None)
      }
      if (acceptsJson(request)) {
        Ok(Json.toJson(services))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.maintenanceservice.index(services, user))
      } else {
        BadRequest
      }
    }
  }

  def get(id: Int) = WithCors("GET", "PUT", "DELETE") {
    WithAuthentication { (user, request) =>
      implicit val req = request
      MaintenanceServices.findById(id).map { d =>
        if (acceptsJson(request)) {
          Ok(Json.toJson(d))
        } else if (acceptsHtml(request)) {
          ??? // Ok(views.html.aria.device.item(d.id, user))
        } else {
          BadRequest
        }
      }.getOrElse(NotFound);
    }
  }

  val createForm = Form(
    tuple(
      "name" -> text, //nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "odometer" -> number,
      "monthsPeriod" -> number,
      "enabled" -> boolean))

  val updateForm = Form(
    tuple(
      "name" -> text,
      "displayName" -> optional(text),
      "description" -> optional(text),
      "odometer" -> number,
      "monthsPeriod" -> number,
      "enabled" -> boolean,
      "version" -> number))

  def create = WithCors("POST") {
    WithAuthentication { implicit request =>
      createForm.bindFromRequest.fold(
        errors => BadRequest(errors.errorsAsJson).as("application/json"),
        {
          case (name, displayName, description, odometer, monthsPeriod, enabled) =>
            val d = MaintenanceServiceModel(name, displayName, description, odometer, monthsPeriod, enabled)
            val id = MaintenanceServices.insert(d)
            Ok(s"""{"id"=id}""")
        })
    }
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, odometer, monthsPeriod, enabled, version) =>
          val dp = new MaintenanceServicePersisted(id, name, displayName, description, odometer, monthsPeriod, enabled, version = version)
          MaintenanceServices.update(dp) match {
            case true => {
              Ok(s"MaintenanceService $id updated successfully")
            }
            case _ => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    MaintenanceServices.deleteById(id) match {
      case true => {
        Ok(s"MaintenanceService $id deleted successfully")
      }
      case _ => NotFound("")
    }
  }
}