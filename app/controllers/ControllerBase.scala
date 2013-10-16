package controllers

import play.api._
import play.api.mvc
import play.api.mvc._
import play.api.http.MimeTypes

/**
 * Base controller
 */
trait ControllerBase extends Controller {
  /**
   * A shortcut to the application configuration
   */
  val conf = play.Configuration.root()
  
  /**
   * Authentication call back parameter name used to redirect clients to requested URL
   */
  lazy val auth_cb = conf.getString("nox.ccc.security.login_cb_param", "_cb")
  
  /**
   * Default uri upon a successfull authentication
   */
  lazy val default_auth_uri = conf.getString("nox.ccc.security.default_auth_uri", "/")  
  
  /**
   * The request accepts Json
   * 
   * We mean that the client issued an «application/json» accept header or decorated the uri with a ?json or &json
   * parameter
   */
  def acceptsJson(implicit request: RequestHeader) = {
    if (request.acceptedTypes.exists(_.mediaSubType == "json") || request.queryString.exists(_._1 == "json"))
    //if (request.acceptedTypes.exists(_.accepts(MimeTypes.JSON)) || request.queryString.exists(_._1 == "json"))
      true
    else
      false
  }
  
  /**
   * The request accepts Html
   * 
   * We mean that the client issued an «application/json» accept header or decorated the uri with a ?html or &html
   * parameter
   */
  def acceptsHtml(implicit request: RequestHeader) = {
    //if (request.acceptedTypes.exists(_.accepts(MimeTypes.HTML))  || request.queryString.exists(_._1 == "html")) {
    if (request.acceptedTypes.exists(_.mediaSubType == "html")  || request.queryString.exists(_._1 == "html")) {
      true
    }
    else
      false
  }

}