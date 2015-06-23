package controllers

import play.Logger
import play.api._
import play.api.mvc._
import controllers._
import models._
import security._
import playguard._
import playguard.Import._

object Map extends Secured {
  
  val rule1 = Allow	// Custom rule
  val rule2 = Allow // Custom rule
  val rule3 = Allow // Custom rule

  private def getEffectiveChannel(channel: Option[String]) = channel.getOrElse(conf.getString("nox.ccc.map.default_channel"))
  def cesium(channel: Option[String]) = WithAuthentication {
    Ok(views.html.aria.map.cesium(getEffectiveChannel(channel)))
  }

  def fullmap(channel: Option[String]) = WithAuthentication {
    Ok(views.html.aria.map.full(getEffectiveChannel(channel)))
  }

  def index = WithAuthentication {  (user, request) =>
    implicit val u = user
    implicit val r = request
    
    WithPolicy(rule1).apply {
    	Logger.debug("Eseguito solo se")
    }
    
    val numero: Int = WithPolicy(rule2).apply{
					    	1
					    }.getOrElse{
					    	2
					    }
    
	WithPolicyElse(rule3).apply{
		Logger.debug("Plicy holds")
	}{
		Logger.debug("Plicy doesn't hold")
	}
	
    Ok(views.html.aria.map.index(user))
  }



  def history = WithAuthentication {  (user, request) =>
    implicit val u = user
    implicit val r = request
	
    Ok(content = views.html.aria.map.historical(user))
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