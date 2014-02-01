package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.{ Devices, Device => DeviceModel, DevicePersisted, DeviceInfoPersisted }
import org.joda.time.format.ISODateTimeFormat
import patterns.models.ValidationException

object Device extends Secured {

  import models.DeviceCommandRequest
  import models.DeviceCommandResponse._
  import models.json.{ devicePersistedJsonWriter, deviceInfoPersistedJsonWriter }

  def receiveCommand(deviceId: String) = WithCors("POST") {
    WithAuthentication(parse.json) { (user, request) =>
      request.body.validate[DeviceCommandRequest].map { c ⇒
        Logger.debug("HTTP REQUEST: "+request.body.toString)
        val response = Json.toJson(c.sendToDevice())
        Logger.debug("HTTP RESPONSE: "+response.toString)
        Ok(response)
      }.recoverTotal {
        e => BadRequest("Invalid Command:"+JsError.toFlatJson(e))
      }
    }
  }

  def configureDevice(deviceId: String) = WithAuthentication { (user, request) =>
    Ok(views.html.aria.device.configure(deviceId));
  }

  import models.UserPersisted

  lazy val ariaController: String = getThisClassSimpleName
  val pageTitle: String = "Devices"
  val createButton : String = "device"
    
  private def getThisClassSimpleName: String = {
    val s = this.getClass.getSimpleName()
    s.substring(0, s.length() - 1)
  }
  
  def index(all: Boolean = false) = WithCors("GET", "OPTIONS") {
    WithAuthentication { (user: UserPersisted, request: Request[AnyContent]) =>
      implicit val req = request
      if (acceptsJson(request)) {
        val devices = all match {
	      case false => Devices.findWithInfo(Some(true))
	      case true  => Devices.findWithInfo(None)
	    }
        Ok(Json.toJson(devices))
      } else if (acceptsHtml(request)) {
//        Ok(views.html.aria.device.index(user))
        Ok(views.html.aria.datatable.index(user,ariaController,pageTitle,createButton))
      } else {
        BadRequest
      }
    }
  }
  
  def get(id: Int) = WithCors("GET", "POST", "PUT", "DELETE") {
    WithAuthentication { (user, request) =>
      implicit val req = request
      Devices.findById(id).map { d =>
        if (acceptsJson(request)) {
          Ok(Json.toJson(d))
        } else if (acceptsHtml(request)) {
          Ok(views.html.aria.device.item(d.id, user))
        } else {
          BadRequest
        }
      }.getOrElse(NotFound);
    }
  }

  def getByName(name: String) = WithCors("GET") {
    controllers.biz.Device.getByName(name)
  }

  val createForm = Form(
    tuple(
      "name" -> text, //nonEmptyText(minLength = 3),
      "displayName" -> optional(text),
      "description" -> optional(text),
      "deviceTypeId" -> number, //number(min = 100),
      "deviceGroupId" -> number,
      "vehicleId" -> optional(number),
      "enabled" -> boolean,
      "simcardId" -> optional(number),
      "deviceManagerId" -> optional(number)))

  val updateForm = Form(
    tuple(
      "name" -> text,
      "displayName" -> optional(text),
      "description" -> optional(text),
      "deviceTypeId" -> number,
      "deviceGroupId" -> number,
      "vehicleId" -> optional(number),
      "enabled" -> boolean,
      "simcardId" -> optional(number),
      "deviceManagerId" -> optional(number),
      "version" -> number))

  def create = WithCors("POST") {
    WithAuthentication { implicit request =>
      createForm.bindFromRequest.fold(
        errors => BadRequest(errors.errorsAsJson).as("application/json"),
        {
          case (name, displayName, description, deviceTypeId, deviceGroupId, vehicle_id, enabled, simcardId, deviceManagerId) =>
            val d = DeviceModel(name, displayName, description, deviceTypeId, deviceGroupId, vehicle_id, enabled, simcardId, deviceManagerId)
            val id = Devices.insert(d)
            Ok(s"""{"id":$id}""").as("application/json")
        })
    }
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    updateForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, displayName, description, deviceTypeId, deviceGroupId, vehicleId, enabled, simcardId, deviceManagerId, version) ⇒
          val dp = new DevicePersisted(id, name, displayName, description, deviceTypeId, deviceGroupId, vehicleId, enabled, simcardId, deviceManagerId, version)
          Devices.update(dp) match {
            case true ⇒ {
              notifications.notifyDeviceChangeConfiguration(dp.name)
              Ok(s"Device $id updated successfully")
            }
            case _ => NotFound
          }
      })
  }

  def delete(id: Int) = WithAuthentication {
    Devices.findById(id).fold(NotFound("")) { dp =>
      notifications.notifyDeviceChangeConfiguration(dp.name)
      Devices.deleteById(id) match {
        case true => {
          Ok(s"Device $id deleted successfully")
        }
        case _ => NotFound("")
      }
    }
  }
}