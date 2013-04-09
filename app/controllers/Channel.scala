package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Channel extends Controller {
  def index = Action {
    val channels = Json.obj(
    		"channels" -> Json.arr("MCT1", "NOXT1", "NOXCOM")
    		)
    Ok(channels)
  }
}