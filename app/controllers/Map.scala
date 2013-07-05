package controllers

import play.api._
import play.api.mvc._
import controllers._
import models._
import security._
import playguard._

object Map extends Secured {
  
  val rule1 = Allow
  val rule2 = Allow
  val rule3 = Allow
  
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
	
    Ok(views.html.aria.map.index(user))
  }
  
  
  
  def receiveJsonAuthenticated = WithAuthentication(parse.json) { (user, json) =>
    Ok
  }
  def receiveWithASimpleRule = WithRule((rule1 && rule2) || rule3)(parse.json) { json => Ok }
  def receiveJsonAuthorized = WithRule(Allow)(parse.json) { json =>
    Ok
  }
  
  def public = Action {Ok}
  
  def authenticatedWithUserAndRequest = WithAuthentication { (user, request) => Ok}
  def authenticatedWithRequest = WithAuthentication { request => Ok }
  def authenticated = WithAuthentication { Ok }
  
  
  
  implicit val userContext = UserAuthorizationContext(Anonymous)
  
  def authorizedWithUserAndRequest = WithAuthorization(Allow) { (user, request) =>
    Ok
  }
  
  def authorizedWithRequest = WithAuthorization(Allow) { request =>
    Ok
  }
  
  def authorized = WithAuthorization(Allow) { Ok}
  
}