package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models._
import org.omg.CosNaming.NamingContextPackage.NotFound

object Device extends Secured {

  import models.DeviceCommandRequest
  import models.DeviceCommandResponse._

  def receiveCommand(deviceId: String) = WithAuthentication(parse.json) { (user, request) =>
    request.body.validate[DeviceCommandRequest].map { c =>
      //if (c.device == "4000") throw new IllegalArgumentException("Nun me piace")
      Logger.debug("HTTP REQUEST: " + request.body.toString)
      val response = Json.toJson(c.sendToDevice())
      Logger.debug("HTTP RESPONSE: " + response.toString)
      Ok(response)
    }.recoverTotal {
      e => BadRequest("Invalid Command:" + JsError.toFlatJson(e))
    }
  }

  def configureDevice(deviceId: String) = WithAuthentication {
    //Thread.sleep(1020)
    Ok(views.html.aria.device.configure(deviceId));
  }

  def index = WithAuthentication {
    val devices = Devices.findAll
    Ok(views.html.aria.device.index(devices));
  }
  
  def get(id: Int) = WithAuthentication {
    Devices.findById(id).map{d => 
    	Ok(Json.obj(
    	    "id" -> d.id, 
    	    "name" -> d.name, 
    	    "display_name" -> d.displayName, 
    	    "description" -> d.description,
    	    "enabled" -> d.enabled, 
    	    "type_id" -> d.deviceType.id, 
    	    "group_id" -> d.deviceGroup.id,
    	    "creation_time" -> d.creationTime.toGMTString(),
    	    "modification_time" -> d.modificationTime.toGMTString()
    	))
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
                description = description
                )
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
          Devices.findById(id).map{ x =>
            var d = x
            val dev_group = DeviceGroups.findById(group_id).get
            val dev_type = DeviceTypes.findById(type_id).get
            //if (display_name.isDefined) d = d.copy(displayName = display_name.get)
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

  def types = WithAuthentication {
    val types = DeviceTypes.findAllEnabled
    val json = Json.obj("types" -> types.map(d => Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "display_name" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled
        )))
    Ok(json)
  }

  def groups = WithAuthentication {
    val groups = DeviceGroups.findAllEnabled
    val json = Json.obj("groups" -> groups.map(d => Json.obj(
        "id" -> d.id,
        "name" -> d.name,
        "display_name" -> d.displayName,
        "description" -> d.description,
        "enabled" -> d.enabled
        )))
    Ok(json)
  }
}