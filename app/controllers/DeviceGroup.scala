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

  def index(all: Boolean = false) = WithAuthentication { (user, request) ⇒
    val groups = all match {
      case false ⇒ DeviceGroups.findAllEnabled
      case true  ⇒ DeviceGroups.findAll
    }
    if (acceptsJson(request)) {
      Ok(Json.toJson(groups))
    } else if (acceptsHtml(request)) {
      Ok(views.html.aria.devicegroup.index(groups))
    } else {
      BadRequest
    }
  }
  
   def get(id: Int) = WithAuthentication { implicit request ⇒
    DeviceGroups.findById(id).map { d ⇒
      if (acceptsJson(request)) {
        Ok(Json.toJson(d))
      } else if (acceptsHtml(request)) {
        Ok(views.html.aria.devicegroup.item(d.id))
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
          if (DeviceGroups.findByName(name).isDefined) {
            BadRequest("""{"name": "A device group with the same name already exists"}""")
          } else {
            var d = new DeviceGroup(name, description)
            display_name.map(desc ⇒ d = d.copy(displayName = desc))
            d = d.copy(
              enabled = enabled match { case Some("on") ⇒ true case _ ⇒ false },
              description = description)
            val id = DeviceGroups.insert(d)
            Ok(s"id=$id")
          }
      })
  }
  
  def update(id: Int) = WithAuthentication { implicit request ⇒
    createForm.bindFromRequest.fold(
      errors ⇒ BadRequest(errors.errorsAsJson).as("application/json"),
      {
        case (name, display_name, description, enabled) ⇒
          DeviceGroups.findById(id).map { x ⇒
            var d = x
            display_name.map(desc ⇒ d = d.copy(displayName = desc))
            d = d.copy(
              name = name,
              description = description,
              enabled = enabled match { case Some("on") ⇒ true case _ ⇒ false })
            if (DeviceGroups.update(d)) {
              Ok(s"Device group $id updated successfully")
            } else {
              NotFound("Device group $id wasn't updated")
            }
          }.getOrElse(NotFound)
      })
  }
  
  def delete(id: Int) = WithAuthentication {
    DeviceGroups.deleteById(id) match {
      case true ⇒ Ok(s"Device type $id deleted successfully")
      case _ ⇒ NotFound
    }
  }
}
