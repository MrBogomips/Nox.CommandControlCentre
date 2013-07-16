package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ Devices, Device ⇒ DeviceModel, DevicePersisted, DeviceInfoPersisted }
import models.json._
import org.joda.time.format.ISODateTimeFormat
import models.ValidationException

object Device extends Secured {

  import models.DeviceCommandRequest
  import models.DeviceCommandResponse._

  def receiveCommand(deviceId: String) = WithAuthentication(parse.json) { (user, request) ⇒
    request.body.validate[DeviceCommandRequest].map { c ⇒
      Logger.debug("HTTP REQUEST: "+request.body.toString)
      val response = Json.toJson(c.sendToDevice())
      Logger.debug("HTTP RESPONSE: "+response.toString)
      Ok(response)
    }.recoverTotal {
      e ⇒ BadRequest("Invalid Command:"+JsError.toFlatJson(e))
    }
  }

  def configureDevice(deviceId: String) = WithAuthentication { (user, request) =>
    Ok(views.html.aria.device.configure(deviceId));
  }

  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    implicit val req = request
    val devices = all match {
      case false ⇒ Devices.findWithInfo(Some(true))
      case true  ⇒ Devices.findWithInfo(None)
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(devices))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.device.index(devices, user))
    } else {
      BadRequest
    }
  }

  def get(id: Int) = WithAuthentication { (user, request) ⇒
    implicit val req = request
    Devices.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.device.item(d.id, user))
      } else {
        BadRequest
      }
    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "name" -> text, //nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "deviceTypeId" -> number, //number(min = 100),
      "deviceGroupId" -> number,
      "vehicleId" -> optional(number(min = 1)),
      "enabled" -> boolean,
      "imei" -> optional(text)))

  val updateForm = Form(
    tuple(
      "name" -> text,
      "displayName" -> optional(text),
      "description" -> optional(text),
      "deviceTypeId" -> number,
      "deviceGroupId" -> number,
      "vehicleId" -> optional(number),
      "enabled" -> boolean,
      "imei" -> optional(text),
      "version" -> number))

  def create = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, deviceTypeId, deviceGroupId, vehicle_id, enabled, imei) ⇒
          val d = DeviceModel(name, displayName, description, deviceTypeId, deviceGroupId, vehicle_id, enabled, imei)
          val id = Devices.insert(d)
          Ok(s"""{"id"=id}""")
      })
  }

  def update(id: Int) = WithAuthentication { implicit request ⇒
    updateForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, deviceTypeId, deviceGroupId, vehicleId, enabled, imei, version) ⇒
          val dp = new DevicePersisted(id, name, displayName, description, deviceTypeId, deviceGroupId, vehicleId, enabled, imei, version)
          Devices.update(dp) match {
            case true ⇒ Ok(s"Device $id updated successfully")
            case _    ⇒ NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    Devices.deleteById(id) match {
      case true ⇒ Ok(s"Device $id deleted successfully")
      case _    ⇒ NotFound
    }
  }
}