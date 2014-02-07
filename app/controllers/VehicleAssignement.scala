package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ VehicleAssignement => VehicleAssignementModel, VehicleAssignementPersisted, VehicleAssignements }

import org.joda.time.format.ISODateTimeFormat

import models.json.vehicleAssignementPersistedJsonWriter

object VehicleAssignement extends Secured {
  
  lazy val ariaController: String = getThisClassSimpleName
  val pageTitle: String = "Vehicle Assignments"
  val createButton : String = "assignment"
    
  private def getThisClassSimpleName: String = {
    val s = this.getClass.getSimpleName()
    s.substring(0, s.length() - 1)
  }

  def index(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) ⇒
      implicit val req = request
      val vehicleAssignements = all match {
        case false => VehicleAssignements.find(Some(true))
        case true  => VehicleAssignements.find(None)
      }
      if (acceptsJson(request)) {
        Ok(Json.toJson(vehicleAssignements))
      } else if (acceptsHtml(request)) {
//        Ok(views.html.aria.vehicleassignement.index(user))
        Ok(views.html.aria.datatable.index(user,ariaController,pageTitle,createButton))
      } else {
        BadRequest
      }
    }
  }
  
  def index2(all: Boolean = false) = WithCors("GET") {
    WithAuthentication { (user, request) ⇒
      implicit val req = request
      val vehicleAssignements = all match {
        case false => VehicleAssignements.find(Some(true))
        case true  => VehicleAssignements.find(None)
      }
      if (acceptsJson(request)) {
        Ok(Json.toJson(vehicleAssignements))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.vehicleassignement.index2(user))
      } else {
        BadRequest
      }
    }
  }

  def get(id: Int) = WithCors("GET", "PUT", "DELETE") {
    WithAuthentication { (user, request) =>
      VehicleAssignements.findById(id).map { va =>
        if (acceptsJson(request)) {
          Ok(Json.toJson(va))
        } else if (acceptsHtml(request)) {
          Ok(views.html.aria.vehicleassignement.item(va.id, user))
        } else {
          BadRequest
        }
      }.getOrElse(NotFound);
    }
  }

  val createForm = Form(
    tuple(
      "vehicleId" -> number(min = 0),
      "driverId" -> number(min = 0),
      "beginAssignement" -> jodaDate("yyyy-MM-dd"),
      "endAssignement" -> jodaDate("yyyy-MM-dd"),
      "enabled" -> boolean))
  val updateForm = Form(
    tuple(
      "vehicleId" -> number(min = 0),
      "driverId" -> number(min = 0),
      "beginAssignement" -> jodaDate("yyyy-MM-dd"),
      "endAssignement" -> jodaDate("yyyy-MM-dd"),
      "enabled" -> boolean,
      "version" -> number))

  def create = WithCors("POST") {
    WithAuthentication { implicit request =>
      createForm.bindFromRequest.fold(
        errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
        {
          case (vehicleId, driverId, beginAssignement, endAssignement, enabled) =>
            val va = VehicleAssignementModel(vehicleId, driverId, beginAssignement, endAssignement, enabled)
            val id = VehicleAssignements.insert(va)
            Ok(s"""{"id":$id}""").as("application/json")
        })
    }
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (vehicleId, driverId, beginAssignement, endAssignement, enabled, version) =>
          val va = VehicleAssignementPersisted(id, vehicleId, driverId, beginAssignement, endAssignement, enabled, version = version)
          VehicleAssignements.update(va) match {
            case true => Ok
            case _    => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    VehicleAssignements.deleteById(id) match {
      case true => Ok
      case _    => NotFound
    }
  }
}