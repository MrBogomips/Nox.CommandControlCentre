package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models._

import org.joda.time.format.ISODateTimeFormat

object DeviceGroup extends Secured {
  /**
   * DeviceGroupPersisted JSON serializer
   */
  implicit val groupJsonWriter = new Writes[DeviceGroupPersisted] {
    def writes(d: DeviceGroupPersisted): JsValue = {
      Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "display_name" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled,
        "creation_time" -> ISODateTimeFormat.dateTime.print(d.creationTime.getTime()),
        "modification_time" -> ISODateTimeFormat.dateTime.print(d.modificationTime.getTime()))
    }
  }
  
  
  def index(all: Boolean = false) = WithAuthentication { (user, request) =>
    val groups = all match {
      case false => DeviceGroups.findAllEnabled
      case true => DeviceGroups.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(groups))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.device.groups(groups))
    } else {
      BadRequest
    }
  }
}
