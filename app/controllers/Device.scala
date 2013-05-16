package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Device extends Controller {
  
  import models.DeviceCommandRequest
  import models.DeviceCommandResponse._

  def receiveCommand(deviceId: String) = Action(parse.json) { request =>
    request.body.validate[DeviceCommandRequest].map{ c =>
	  val response = c.sendToDevice(request.body)
	  Ok(Json.toJson(response))
    }.recoverTotal{
      e => BadRequest("Invalid Command:"+ JsError.toFlatJson(e))
    }
  }
  
  def configureDevice(deviceId: String) = Action {
    Thread.sleep(2020)
    Ok(views.html.aria.device.configure(deviceId));
  }
}