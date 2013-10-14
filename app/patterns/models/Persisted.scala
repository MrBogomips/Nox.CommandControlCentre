package patterns.models

import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple._

/**
 * Represents a model
 */
trait Model[+A <: Validatable] extends Validatable with ValidationRequired


/**
 * Represents a persisted model class
 */
trait Persisted[+A <: Model[_]] extends Validatable {
  /**
   * The record surrogate primary key
   */
  val id: Int
  /**
   * The record creation time
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  val creationTime: Timestamp
  /**
   * The record last modification time
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  val modificationTime: Timestamp
  /**
   * The record version number
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  val version: Int
}
