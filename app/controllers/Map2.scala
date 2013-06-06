package controllers

import play.api._
import play.api.mvc._

import controllers._
import models._
import security._

object Map2 extends Secured {
  def index = WithAuthentication {  (user, request) =>
    implicit val u = user
    implicit val r = request
    
    WithPolicy(Allow).apply {
    	Logger.debug("Eseguito solo se")
    }
    
    WithPolicy(Allow).apply(
    	Logger.debug("Eseguito solo se")
    )
    
    val numero: Int = WithPolicy(Allow).apply{
					    	1
					    }.getOrElse{
					    	2
					    }
    
	WithPolicyElse(Allow).apply{
		Logger.debug("Plicy holds")
	}{
		Logger.debug("Plicy doesn't hold")
	}
    
    Ok(views.html.aria.map.index())
  }
  
  
  
  
  def public = Action {Ok}
  
  def authenticatedWithUserAndRequest = WithAuthentication { (user, request) => Ok}
  def authenticatedWithRequest = WithAuthentication { request => Ok }
  def authenticated = WithAuthentication { Ok }
  
  
  
  
  
  def authorizedWithUserAndRequest = WithAuthorization(Allow) { (user, request) =>
    Ok
  }
  
  def authorizedWithRequest = WithAuthorization(Allow) { request =>
    Ok
  }
  
  def authorized = WithAuthorization(Allow) { Ok}
  
}