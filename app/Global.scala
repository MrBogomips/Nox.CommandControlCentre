import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import patterns.models.ValidationException

package object globals {
  lazy val Configuration = play.Configuration.root()
}

object StatusCodes extends Enumeration {
  type StatusCodes = Value
  val OK = Value("OK")
  val KO = Value("KO")
  val KO_NO_ACTION = Value("KO_NO_ACTION")
  val KO_VALIDATION = Value("KO_VALIDATION")
}
import StatusCodes._

class Status(val code: StatusCodes, val description: String, val exception: Option[Throwable])

object Status {
  def apply(exception: Throwable) = exception match {
    case e: ValidationException => new Status(StatusCodes.KO_VALIDATION, exception.getMessage(), Some(exception))
    case e: Throwable           => new Status(StatusCodes.KO, exception.getMessage(), Some(exception))
  }
  def apply(code: StatusCodes, description: String) = new Status(code, description, None)
  def apply(code: StatusCodes) = new Status(code, "", None)
}

object Global extends GlobalSettings {
  
  implicit val stackTraceElementWriter: Writes[StackTraceElement] = new Writes[StackTraceElement] {
    def writes(ste: StackTraceElement) = {
      Json.obj(
    		  "fileName" -> ste.getFileName(),
    		  "lineNumber" -> ste.getLineNumber(),
    		  "className" -> ste.getClassName(),
    		  "methodName" -> ste.getMethodName(),
    		  "isNativeMethos" -> ste.isNativeMethod()
      )
    }
  }

  implicit val errorJsonWriter: Writes[Status] = new Writes[Status] {
    
    import models.json.validationErrorJsonWriter
    
    def writes(e: Status): JsValue = e.exception match {
      case Some(ex: ValidationException) => {
        Json.obj(
          "code" -> e.code.toString(),
          "description" -> e.description,
          "validationErrors" -> ex.validationErrors)
      } 
      case Some(ex: Throwable) => {
        Json.obj(
          "code" -> e.code.toString(),
          "description" -> e.description,
          "exception" -> Json.obj(
            "className" -> ex.getClass.getName(),
            "message" -> ex.getMessage(),
            "stackTrace" -> ex.getStackTrace()
            ))
      }
      case None => {
        Json.obj(
          "code" -> e.code.toString(),
          "description" -> e.description)
      }
    }
  }

  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }
  
  override def onHandlerNotFound(r: RequestHeader): Result = {
    NotFound(Json.toJson(Status(StatusCodes.KO_NO_ACTION, s"No action found for: [${r.method} ${r.uri}]")))
  }

  override def onError(request: RequestHeader, ex: Throwable) = ex.getCause() match {
    case ex: ValidationException => BadRequest(Json.toJson(Status(ex)))
    case ex: java.util.NoSuchElementException => NotFound(Json.toJson(Status(ex)))
    case ex                      => InternalServerError(Json.toJson(Status(ex)))
  }

  override def onBadRequest(request: RequestHeader, error: String) = {
    val json = Json.obj(
      "status" -> "KO_BAD_REQUEST",
      "description" -> error);
    BadRequest(json)
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    //Logger.debug("Accounting request: "+request.toString)
    super.onRouteRequest(request)
  }
}