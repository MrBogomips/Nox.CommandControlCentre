package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models._
import utils.Converter._

import org.joda.time.format.ISODateTimeFormat

object VehicleAssignement extends Secured {
  /**
    * DriverPersisted JSON serializer
    */
  implicit val vehicleAssignementJsonWriter = new Writes[VehicleAssignementPersisted] {
    def writes(va: VehicleAssignementPersisted): JsValue = {
      Json.obj(
        "id" -> va.id,
        "vehicleId" -> va.vehicleId,
        "driverId" -> va.driverId,
        "beginAssignement" -> ISODateTimeFormat.date.print(va.beginAssignement.getTime()),
        "endAssignement" -> ISODateTimeFormat.date.print(va.endAssignement.getTime()),
        "enabled" -> va.enabled,
        "creationTime" -> ISODateTimeFormat.dateTime.print(va.creationTime.getTime()),
        "modificationTime" -> ISODateTimeFormat.dateTime.print(va.modificationTime.getTime()))
    }
  }

  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    val assigns = all match {
      case false ⇒ VehicleAssignements.findAllEnabled
      case true ⇒ VehicleAssignements.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(assigns))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.vehicleassignement.index(user))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) ⇒
    VehicleAssignements.findById(id).map { va ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(va))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.vehicleassignement.item(va.id, user))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "vehicleId" -> number(min = 0),
      "driverId" -> number(min = 0),
      "beginAssignement" -> jodaDate("yyyy-MM-dd"),
      "endAssignement" -> jodaDate("yyyy-MM-dd"),
      "enabled" -> optional(text)))

  def create = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (vehicleId, driverId, beginAssignement, endAssignement, enabled) ⇒
          val va = models.VehicleAssignement(vehicleId, driverId, beginAssignement, endAssignement, enabled)
          VehicleAssignements.insert(va).fold(
              errs => BadRequest(Json.toJson(errs)), 
              id => Ok(s"""{"id":$id}""").as("application/json")
          )
      })
  }

  def update(id: Int) = WithAuthentication { implicit request ⇒
    Thread.sleep(1000)
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (vehicleId, driverId, beginAssignement, endAssignement, enabled) ⇒
          models.VehicleAssignements.findById(id).fold(NotFound("Vehicle assignement $id wasn't found")) { va =>
            val va2 = va.copy(
              vehicleId = vehicleId,
              driverId = driverId,
              beginAssignement = beginAssignement,
              endAssignement = endAssignement,
              enabled = enabled)
            VehicleAssignements.update(va2).fold(
                errs => BadRequest(Json.toJson(errs).toString), 
                _ => Ok("")
            )
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    if (VehicleAssignements.deleteById(id) > 0) 
     Ok
    else 
      NotFound
  }
}