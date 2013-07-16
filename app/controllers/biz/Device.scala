package controllers.biz

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import controllers._
import models.{Devices, Device ⇒ DeviceModel, DevicePersisted, DeviceInfoPersisted}

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
        "type_id" -> d.deviceTypeId,
        "group_id" -> d.deviceGroupId,
        "channels" -> List("NOXT1","NOXT2"),
        "creation_time" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()))
    }
  }
  def index(all: Boolean = false) = Action { implicit request ⇒
    implicit val req = request
    val devices = all match {
      case false ⇒ Devices.find(Some(true))
      case true ⇒ Devices.find(None)
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
}