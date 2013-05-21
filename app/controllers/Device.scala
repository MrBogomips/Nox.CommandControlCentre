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
      //if (c.device == "4000") throw new IllegalArgumentException("Nun me piace")
	  Logger.debug("HTTP REQUEST: " + request.body.toString)
      val response = Json.toJson(c.sendToDevice())
	  Logger.debug("HTTP RESPONSE: " + response.toString)
      Ok(response)
    }.recoverTotal{
      e => BadRequest("Invalid Command:"+ JsError.toFlatJson(e))
    }
  }
  
  def configureDevice(deviceId: String) = Action {
    //Thread.sleep(1020)
    Ok(views.html.aria.device.configure(deviceId));
  }
}