package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import org.joda.time.format.ISODateTimeFormat
import patterns.models._
import models.{ MaintenanceDuties, MaintenanceDuty => MaintenanceDutyModel, MaintenanceDutyPersisted }

object MaintenanceDuty extends Secured {

  import models.json.{ maintenanceDutyPersistedJsonWriter, maintenanceDutyInfoPersistedJsonWriter }

  def index(all: Boolean = false) = WithAuthentication { (user, request) =>
    implicit val req = request

    val duties = all match {
      case false => MaintenanceDuties.find(Some(true))
      case true  => MaintenanceDuties.find(None)
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(duties))
    } else if (acceptsHtml(request)) {
      ???
    } else {
      BadRequest
    }
  }

  def findByVehicleId(idVehicle: Int) = WithAuthentication { (user, request) =>
    implicit val req = request 
    //implicit val pagination = NoPagination
    val duties = MaintenanceDuties.findByVehicleId(idVehicle, Paginator.fromRequest)
    if (acceptsJson(request)) {
      Ok(Json.toJson(duties))
    } else if (acceptsHtml(request)) {
      ??? 
    } else {
      BadRequest
    }
  }
  
  def get(id: Int) = WithAuthentication { (user, request) =>
    implicit val req = request
    MaintenanceDuties.findById(id).map { d =>
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        ??? // Ok(views.html.aria.device.item(d.id, user))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "idVehicle" -> number,
      "idService" -> number))
  val updateForm = Form(
    tuple(
      "idVehicle" -> number,
      "idService" -> number,
      "version" -> number))

  def create = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (idVehicle, idService) =>
          val d = MaintenanceDutyModel(idVehicle, idService)
          val id = MaintenanceDuties.insert(d)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (idVehicle, idService, version) =>
          val dp = new MaintenanceDutyPersisted(id, idVehicle, idService, version = version)
          MaintenanceDuties.update(dp) match {
            case true => {
              Ok(s"MaintenanceDuty $id updated successfully")
            }
            case _ => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    MaintenanceDuties.deleteById(id) match {
      case true => {
        Ok(s"MaintenanceDuty $id deleted successfully")
      }
      case _ => NotFound("")
    }
  }
}