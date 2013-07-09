package controllers.biz

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models._
import controllers._

import org.joda.time.format.ISODateTimeFormat

object Device extends ControllerBase {
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
        "channels" -> List("NOXT1","NOXT2"),
        "creation_time" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()))
    }
  }

  def index(all: Boolean = false) = Action { implicit request ⇒
    implicit val req = request
    val devices = all match {
      case false ⇒ Devices.findAllEnabled
      case true ⇒ Devices.findAll
    }
      Ok(Json.toJson(devices))
  }

  def get(id: Int) = Action { implicit request ⇒
    implicit val req = request
    Devices.findById(id).map { d ⇒
        Ok(Json.toJson(d))
    }.getOrElse(NotFound);
  }
  def getByName(name: String) = Action { implicit request ⇒
    implicit val req = request
    Devices.findByName(name).map { d ⇒
        Ok(Json.toJson(d))
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
/*
  def create = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, display_name, description, type_id, group_id, enabled) ⇒
          if (Devices.findByName(name).isDefined) {
            BadRequest("""{"name": ["A device with the same name already exists"]}""").as("application/json")
          } else {
            val dev_group = DeviceGroups.findById(group_id).get
            val dev_type = DeviceTypes.findById(type_id).get
            var d = new Device(name, dev_type, dev_group)
            //if (display_name.isDefined) d = d.copy(displayName = display_name.get)
            display_name.map(desc ⇒ d = d.copy(displayName = desc))
            d = d.copy(
              enabled = enabled match { case Some("on") ⇒ true case _ ⇒ false },
              description = description)
            val id = Devices.insert(d)
            Ok(s"id=id")
          }
      })
  }

  def update(id: Int) = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, display_name, description, type_id, group_id, enabled) ⇒
          Devices.findById(id).map { x ⇒
            var d = x
            val dev_group = DeviceGroups.findById(group_id).get
            val dev_type = DeviceTypes.findById(type_id).get
            display_name.map(desc ⇒ d = d.copy(displayName = desc))
            d = d.copy(
              name = name,
              description = description,
              deviceType = dev_type,
              deviceGroup = dev_group,
              enabled = enabled match { case Some("on") ⇒ true case _ ⇒ false })
            Ok(s"Device $id updated successfully")
            if (Devices.update(d)) {
              Ok(s"Device $id updated successfully")
            } else {
              NotFound("Device $id wasn't updated")
            }
          }.getOrElse(NotFound)
      })
  }

  def delete(id: Int) = WithAuthentication {
    Devices.deleteById(id) match {
      case true ⇒ Ok(s"Device $id deleted successfully")
      case _ ⇒ NotFound
    }
  }
  */
}