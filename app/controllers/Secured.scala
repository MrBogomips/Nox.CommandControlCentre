package controllers

import play.api._
import play.api.mvc._
import java.net.URLEncoder
import java.net.URL
import models._
import security._
import playguard.AuthenticatedController


/**
 * Provide security features
 */
trait Secured extends ControllerBase with AuthenticatedController[UserPersisted] {
  /**
   * Retrieve the connected user email.
   */
  def user(request: RequestHeader): Option[UserPersisted] = request.session.get("login").flatMap(Users.findByLogin(_))

  /**
   * Redirect to login if the user in not authorized.
   */
  def onUnauthenticated(request: RequestHeader) : Result = {
    val loginUrl = s"${routes.Application.login.toString}?${auth_cb}=${URLEncoder.encode(request.uri, "UTF-8")}"  
    Logger.debug(loginUrl)
    
    Results.Redirect(loginUrl)
  }
  /**
   * Just for the sake of explanation: default beahviour already return a 403 code
   */
  override def onUnauthorizedUser(user: UserPersisted, request: RequestHeader) = Results.Forbidden
}