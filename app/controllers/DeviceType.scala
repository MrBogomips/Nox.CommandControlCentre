package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models._

import org.joda.time.format.ISODateTimeFormat

object DeviceType extends Secured {
  /**
   * DeviceTypePersisted JSON serializer
   */
  implicit val typeJsonWriter = new Writes[DeviceTypePersisted] {
    def writes(d: DeviceTypePersisted): JsValue = {
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
  
  def index(onlyEnabled: Boolean = true) = WithAuthentication { (user, request) =>
    val types = onlyEnabled match {
      case true => DeviceTypes.findAllEnabled
      case false => DeviceTypes.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(types))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.device.types(types))
    } else {
      BadRequest
    }
  }
}