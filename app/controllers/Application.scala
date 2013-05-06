package controllers

import play.api._
import play.api.mvc._

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
}