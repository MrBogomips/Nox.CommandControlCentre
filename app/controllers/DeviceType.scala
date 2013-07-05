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

  def index(all: Boolean = false) = WithAuthentication { (user, request) =>
    val types = all match {
      case false => DeviceTypes.findAllEnabled
      case true => DeviceTypes.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(types))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.devicetype.index(types))
    } else {
      BadRequest
    }
  }
  def get(id: Int) = WithAuthentication { implicit request ⇒
    DeviceTypes.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.devicetype.item(d.id))
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
      "enabled" -> optional(text)))

  def create = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, display_name, description, enabled) ⇒
          if (DeviceTypes.findByName(name).isDefined) {
            BadRequest("""{"name": ["A device type with the same name already exists"]}""").as("application/json")
          } else {
            var d = new DeviceType(name, description)
            display_name.map(desc ⇒ d = d.copy(displayName = desc))
            d = d.copy(
              enabled = enabled match { case Some("on") ⇒ true case _ ⇒ false },
              description = description)
            val id = DeviceTypes.insert(d)
            Ok(s"id=$id")
          }
      })
  }

  def update(id: Int) = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, display_name, description, enabled) ⇒
          DeviceTypes.findById(id).map { x ⇒
            var d = x
            display_name.map(desc ⇒ d = d.copy(displayName = desc))
            d = d.copy(
              name = name,
              description = description,
              enabled = enabled match { case Some("on") ⇒ true case _ ⇒ false })
            if (DeviceTypes.update(d)) {
              Ok(s"Device type $id updated successfully")
            } else {
              NotFound("Device type $id wasn't updated")
            }
          }.getOrElse(NotFound)
      })
  }

  def delete(id: Int) = WithAuthentication {
    DeviceTypes.deleteById(id) match {
      case true ⇒ Ok(s"Device type $id deleted successfully")
      case _ ⇒ NotFound
    }
  }
}


