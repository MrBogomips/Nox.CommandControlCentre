package patterns.models

import org.postgresql.util.PSQLException

import play.api.libs.json._

/**
  * Represents a validation exception
  */
case class ValidationException(cause: Throwable, val validationErrors: Seq[ValidationError]) extends RuntimeException(cause) {
  def this(cause: Throwable, key: String, conditions: String*) = this(cause, Seq(ValidationError(key, conditions.toSet)))
  def this(key: String, conditions: String*) = this(null, Seq(ValidationError(key, conditions.toSet)))
  def this(validationErrors: Seq[ValidationError]) = this(null, validationErrors)
  override def getMessage() = "ValidationException: "+validationErrors.mkString("; ")
  def toJsError: JsError = {
    ???
    //new JsError("ciccio", "ciccio")
  }
}

/*
 * Represents a validation error
 */
case class ValidationError(key: String, conditions: Set[String])

/*
 * Provides validation capabilities to a model
 */
trait Validatable {
  /*
   * Each concrete class must provide the validation logic
   */
  protected def validate: Unit

  lazy val validationErrors: Seq[ValidationError] = {
    validate
    errorsAccumulator.toSeq.map(e => ValidationError(e._1, e._2.toSet))
  }
  lazy val isValid = validationErrors.isEmpty

  def requireValidation: Unit = if (!isValid) throw new ValidationException(validationErrors)

  import collection.mutable.{ HashMap, MultiMap, Set }
  protected val errorsAccumulator = new HashMap[String, Set[String]] with MultiMap[String, String]

  /**
    * Material implication
    */
  import scala.language.{ implicitConversions, reflectiveCalls }
  protected implicit def extendedBoolean(a: Boolean) = new {
    def implies(b: => Boolean) = !a || b
  }

  protected def addValidationError(key: String, condition: String) =
    errorsAccumulator.addBinding(key, condition)

  /**
    * Helper method to check condition and accumulate errors
    */
  protected def validate(condition: => Boolean, key: String, message: String) =
    if (!condition)
      addValidationError(key, message)

  /**
    * @param key the key used to signal the violation to the end user form
    * @param value the value checked
    * @param minLength the minimum length required
    */
  protected def validateMinLength(key: String, string: String, minLength: Int) = {
    require(minLength > 0)
    validate(string.length() >= minLength, key, s"Minimum length is $minLength")
  }
  /**
    * Validate an exact length
    */
  protected def validateLength(key: String, string: String, length: Int) = {
    require(length > 0)
    validate(string.length() != length, key, s"Length must be $length")
  }
  /**
    * Validate an exact length
    */
  protected def validateImei(key: String, imei: String) = {
    validate(imei.matches("^[0-9]{15}$"), key, s"Not a valid IMEI")
  }
  protected def validateMinValue(key: String, value: Int, minValue: Int) = {
    validate(value >= minValue, key, s"Must be greater or equal to $minValue")
  }
  protected def validateMinValue(key: String, value: Long, minValue: Long) = {
    validate(value >= minValue, key, s"Must be greater or equal to $minValue")
  }
  protected def validateMinValue(key: String, value: Double, minValue: Double) = {
    validate(value >= minValue, key, s"Must be greater or equal to $minValue")
  }
  private val emailPattern = """\b[a-zA-Z0-9.!#$%&���*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*\b"""
  protected def validateEmail(key: String, email: String) = {
    validate(email.matches(emailPattern), key, s"Not a valid email")
  }
  protected def validateLesserThan[T <% Ordered[T]](lesserKey: String, lesser: T, greaterKey: String, greater: T) = {
    validate(lesser < greater, s"$lesserKey,$greaterKey", s"$lesserKey must be less than $greaterKey")
  }
  protected def validateLesserOrEqualThan[T <% Ordered[T]](lesserKey: String, lesser: T, greaterKey: String, greater: T) = {
    validate(lesser <= greater, s"$lesserKey,$greaterKey", s"$lesserKey must be less or equal than $greaterKey")
  }
  protected def validateGreaterThan[T <% Ordered[T]](lesserKey: String, lesser: T, greaterKey: String, greater: T) = {
    validate(lesser < greater, s"$lesserKey,$greaterKey", s"$lesserKey must be great than $greaterKey")
  }
  protected def validateGreaterOrEqualThan[T <% Ordered[T]](lesserKey: String, lesser: T, greaterKey: String, greater: T) = {
    validate(lesser <= greater, s"$lesserKey,$greaterKey", s"$lesserKey must be great or equal than $greaterKey")
  }
  protected def validateEmailList(key: String, emails: String, separator: String = ";") = {
    ???
  }
  protected def validateUri(key: String, uri: String) = {
    ???
  }
}

trait ValidationRequired extends Validatable {
  requireValidation
}

/*
 * Utility object to catch and chain validation errors
 * 
 * The main purpose is to ensure that the model entity is in a valid state before to perform any persistent operation
 * and, moreover, remaps persistence layer exception (unique key violations, foreign keys… and so on) to a more
 * meaningful `ValidationException`
 */
object WithValidation {
  /**
    * @param exMapper: a user provided DB exception to ValidationException mapper
    */
  def apply[A <: Validatable, B](obj: A)(f: A => B)(implicit exMapper: (PSQLException => Nothing)): B = {
    if (!obj.isValid) throw new ValidationException(obj.validationErrors)
    try {
      f(obj)
    } catch {
      case e: PSQLException => exMapper(e)
    }
  }
  def apply[A](f: => A)(implicit exMapper: (PSQLException => Nothing)): A = {
    try {
      f
    } catch {
      case e: PSQLException => exMapper(e)
    }
  }
}