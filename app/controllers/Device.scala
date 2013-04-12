package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Device extends Controller {
  
  import models.DeviceCommand

  def receiveCommand = Action(parse.json) { request =>
    request.body.validate[DeviceCommand].map{ c =>
      Ok("Command " + c.command + " with arg "+c.arguments+"\n")
    }.recoverTotal{
      e => BadRequest("Invalid Command:"+ JsError.toFlatJson(e))
    }
  }
}