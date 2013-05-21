package controllers

import play.api._
import play.api.mvc._
import play.api.libs.Jsonp
import play.api.libs.json.Json
import globals.Demo

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  } 
  
  // -- Javascript routing
  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
        Routes.javascriptRouter("jsRoutes")(
          Device.receiveCommand
      )
    ).as("text/javascript") 
  }
  
  // -- Client Confguration
  def clientConfiguration = Action { implicit request =>
    val app = globals.Application
    val json = Json.obj(
        "applicationId" -> app.applicationId,
        "applicationKey" -> app.applicationKey,
        "userId" -> Demo.userId,
        "sessionId" -> Demo.sessionId,
        "mqttClientTopic" -> Demo.mqttClientTopic,
        "mqttApplicationTopic" -> Demo.mqttApplicationTopic,
        "mqttUserTopic" -> Demo.mqttUserTopic,
        "mqttSessionTopic" -> Demo.mqttSessionTopic,
        "eventsWebSocket" -> app.eventsWebSocket
    )
      
      
    request.queryString.get("callback").flatMap(_.headOption) match {
      case Some(callback) => Ok(Jsonp(callback, json)).as(JAVASCRIPT)
      case None => Ok(json).as(JSON)
    }
  }
}