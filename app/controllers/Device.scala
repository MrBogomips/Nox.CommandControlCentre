package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Device extends Controller {
  
  import models.DeviceCommandRequest
  import models.DeviceCommandResponse._

  def receiveCommand = Action(parse.json) { request =>
    request.body.validate[DeviceCommandRequest].map{ c =>
	  val response = c.sendToDevice()
	  Ok(Json.toJson(response))
    }.recoverTotal{
      e => BadRequest("Invalid Command:"+ JsError.toFlatJson(e))
    }
  }
}