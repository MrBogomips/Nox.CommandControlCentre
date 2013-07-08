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
  implicit val driverJsonWriter = new Writes[VehicleAssignementPersisted] {
    def writes(va: VehicleAssignementPersisted): JsValue = {
      Json.obj(
        "id" -> va.id,
        "vehicleId" -> va.vehicleId,
        "driverId" -> va.driverId,
        "beginAssignement" -> ISODateTimeFormat.dateTime.print(va.beginAssignement.getTime()),
        "endAssignement" -> ISODateTimeFormat.dateTime.print(va.endAssignement.getTime()),
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
      "vehicle_id" -> number(min = 0),
      "driver_id" -> number(min = 0),
      "begin_assignement" -> jodaDate("yyyy-MM-dd"),
      "end_assignemnet" -> jodaDate("yyyy-MM-dd"),
      "enabled" -> optional(text)))

  def create = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (vehicle_id, driver_id, begin_assignement, end_assignement, enabled) ⇒
          val va = models.VehicleAssignement(vehicle_id, driver_id, begin_assignement, end_assignement, enabled)
          VehicleAssignements.insert(va).fold(
              errs => BadRequest(Json.toJson(errs)), 
              id => Ok(s"id=$id")
          )
      })
  }

  def update(id: Int) = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (vehicle_id, driver_id, begin_assignement, end_assignement, enabled) ⇒
          models.VehicleAssignements.findById(id).fold(NotFound("Vehicle assignement $id wasn't found")) { va =>
            val va2 = va.copy(
              vehicleId = vehicle_id,
              driverId = driver_id,
              beginAssignement = begin_assignement,
              endAssignement = end_assignement,
              enabled = enabled)
            VehicleAssignements.update(va2).fold(
                errs => BadRequest(Json.toJson(errs).toString), 
                _ => Ok(s"Vehicle assignement $id updated successfully")
            )
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    if (Drivers.deleteById(id) > 0) 
     Ok(s"Driver $id deleted successfully")
    else 
      NotFound
  }
}