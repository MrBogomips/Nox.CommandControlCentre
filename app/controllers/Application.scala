package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.Jsonp
import play.api.libs.json.Json

import globals.Demo

import models._
import views._

object Application extends ControllerBase {
  
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
    val config = globals.Configuration
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
        "eventsWebSocket" -> app.eventsWebSocket,
        "eventsOutOfSequencePolicy" -> config.getString("nox.ccc.events_out_of_seq_policy")
    )
      
      
    request.queryString.get("callback").flatMap(_.headOption) match {
      case Some(callback) => Ok(Jsonp(callback, json)).as(JAVASCRIPT)
      case None => Ok(json).as(JSON)
    }
  }
  
  // -- Authentication taks
   val loginForm = Form(
    tuple(
      "login" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (login, password) => User.authenticate(login, password).isDefined
    })
  )
  /**
   * Build the action URL of the login form preserving the querystring passed
   */
  private def loginActionUrl(implicit request: Request[AnyContent]): String = {
     s"${routes.Application.authenticate.toString}?${request.rawQueryString}"
   }
  /**
   * Login page.
   */
  def login = Action { implicit request =>
    play.api.mvc.Request
    Ok(html.login(loginForm, loginActionUrl))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors, loginActionUrl)),
      login => {
        //Logger.debug(s"uri = [${request.uri}]")
        //Logger.debug(s"auth_cb = [$auth_cb]")
        //Logger.debug(s"request.getQueryString(auth_cb) = [${request.getQueryString(auth_cb)}]")
        request.getQueryString(auth_cb).map { uri =>
          Logger.debug(s"Redirecting to callback page [$uri]")
          Redirect(uri) 
        }.getOrElse{
          Logger.debug(s"Redirecting to default authenticated page [$default_auth_uri]")
          Redirect(default_auth_uri) 
        }.withSession("login" -> login._1)
      }
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  } 
}