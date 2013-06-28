package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models._

import org.joda.time.format.ISODateTimeFormat

object Device extends Secured {
  /**
   * DevicePersisted JSON serializer
   */
  implicit val deviceJsonWriter = new Writes[DevicePersisted] {
    def writes(d: DevicePersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "display_name" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled,
        "type_id" -> d.deviceType.id,
        "group_id" -> d.deviceGroup.id,
        "creation_time" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()))
    }
  }

  import models.DeviceCommandRequest
  import models.DeviceCommandResponse._

  def receiveCommand(deviceId: String) = WithAuthentication(parse.json) { (user, request) =>
    request.body.validate[DeviceCommandRequest].map { c =>
      Logger.debug("HTTP REQUEST: " + request.body.toString)
      val response = Json.toJson(c.sendToDevice())
      Logger.debug("HTTP RESPONSE: " + response.toString)
      Ok(response)
    }.recoverTotal {
      e => BadRequest("Invalid Command:" + JsError.toFlatJson(e))
    }
  }

  def configureDevice(deviceId: String) = WithAuthentication {
    Ok(views.html.aria.device.configure(deviceId));
  }

  def index(all: Boolean = false) = WithAuthentication { implicit request =>
    val devices = all match {
      case false => Devices.findAllEnabled
      case true => Devices.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(devices))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.device.index(devices))
    } else {
      BadRequest
    }
  }

  def myDevice(all: Boolean = false) = WithAuthentication { implicit request =>
    val devices = all match {
      case false => Devices.findAllEnabled
      case true => Devices.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(devices))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.device.index(devices))
    } else {
      BadRequest
    }
  }
  
  def get(id: Int) = WithAuthentication { implicit request =>
    Devices.findById(id).map { d =>
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.device.item(d.id))
      } else {
        BadRequest
      }

    }.getOrElse(NotFound);
  }

  val createForm = Form(
    tuple(
      "name" -> nonEmptyText(minLength = 3),
      "display_name" -> optional(text),
      "description" -> optional(text),
      "type_id" -> number(min = 0),
      "group_id" -> number(min = 0),
      "enabled" -> optional(text)))

  def create = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson),
      {
        case (name, display_name, description, type_id, group_id, enabled) =>
          if (Devices.findByName(name).isDefined) {
            BadRequest("""{"name": "A device with the same name already exists"}""")
          } else {
            val dev_group = DeviceGroups.findById(group_id).get
            val dev_type = DeviceTypes.findById(type_id).get
            var d = new Device(name, dev_type, dev_group)
            //if (display_name.isDefined) d = d.copy(displayName = display_name.get)
            display_name.map(desc => d = d.copy(displayName = desc))
            d = d.copy(
              enabled = enabled match { case Some("on") => true case _ => false },
              description = description)
            val deviceId = Devices.insert(d)
            Ok(s"id=$deviceId")
          }
      })
  }

  def update(id: Int) = WithAuthentication { implicit request =>
    createForm.bindFromRequest.fold(
      errors => BadRequest(errors.errorsAsJson),
      {
        case (name, display_name, description, type_id, group_id, enabled) =>
          Devices.findById(id).map { x =>
            var d = x
            val dev_group = DeviceGroups.findById(group_id).get
            val dev_type = DeviceTypes.findById(type_id).get
            display_name.map(desc => d = d.copy(displayName = desc))
            d = d.copy(
              name = name,
              description = description,
              deviceType = dev_type,
              deviceGroup = dev_group,
              enabled = enabled match { case Some("on") => true case _ => false })
            val deviceId = Devices.update(d)
            Ok(s"Device $id updated successfully")
          }.getOrElse(NotFound)
      })
  }

  def delete(id: Int) = WithAuthentication {
    Devices.deleteById(id) match {
      case true => Ok(s"Device $id deleted successfully")
      case _ => NotFound
    }
  }
}