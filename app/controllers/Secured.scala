package controllers

import play.api._
import play.api.mvc._
import models.User
import java.net.URLEncoder
import java.net.URL

/**
 * Provide security features
 */
trait Secured extends ControllerBase {
  /**
   * Retrieve the connected user email.
   */
  private def account(request: RequestHeader) = request.session.get("login")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = {
    val loginUrl = s"${routes.Application.login.toString}?${auth_cb}=${URLEncoder.encode(request.uri, "UTF-8")}"  
    Logger.debug(loginUrl)
    
    Results.Redirect(loginUrl)
  }
  
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(account, onUnauthorized) { account =>
    Action(request => f(account)(request))
  }

  /**
   * Check if the connected user is a member of any groups
   */
  def IsMemberOfAny(groups: Seq[String])(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(User.isMemberOfAny(groups)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }
  
  /**
   * Check if the connected user is a member of all the groups
   */
  def IsMemberOfAll(groups: Seq[String])(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(User.isMemberOfAll(groups)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }

  def CanUseAnyFeature(features: Seq[String]) = ???
  
  def CanConfigureDevice(deviceId: Long) = ???
}