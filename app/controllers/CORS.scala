package controllers

import play.Logger
import play.api._
import play.api.mvc.Results._
import play.api.http
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.iteratee._
import play.api.data._
import play.api.data.Forms._
import org.joda.time.format.ISODateTimeFormat

import scala.concurrent._

/**
  * Action decorator that provide CORS support
  * 
  * @author Giovanni Costagliola
  */
case class WithCors(httpVerbs: String*)(action: EssentialAction) extends EssentialAction {
  val log = Logger.of("noxccc.cors")
  def apply(request: RequestHeader) = {
    implicit val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext
    val origin = request.headers.get("Origin").getOrElse("*")
    log.debug(s"Origin: $origin; ${request.method} ${request.uri} --> Allowed verbs: ${httpVerbs.mkString(", ")}")
    if (request.method == "OPTIONS") {
      val corsAction = Action { request =>
        Ok("").withHeaders(
        "Access-Control-Allow-Origin" -> origin,
        "Access-Control-Allow-Methods" -> (httpVerbs.toSet + "OPTIONS").mkString(", "),
        "Access-Control-Max-Age" -> "3600",
        "Access-Control-Allow-Headers" -> "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Auth-Token",
        "Access-Control-Allow-Credentials" -> "true")
      }
      corsAction(request)
    } else {
      action(request).map(res => res.withHeaders(
        "Access-Control-Allow-Origin" -> origin))
    }
  }
}

/*
case class WithCors(params: Seq[String]) extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A] => Future[SimpleResult])) = {
    block(request)
  }
}
*/