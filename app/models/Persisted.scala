package models

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

/**
 * Represents a model class that could require persistance
 */
trait Persistable {
  private var _isPersisted: Boolean = true
  /**
   * Indicate if the object has been modified since the last fetch
   */
  def isPersisted = _isPersisted

  private[models] def persisted = _isPersisted = true

  def prepareCopy[A <: Persistable](f: => A): A = {
    val ret = f
    ret._isPersisted = false
    ret
  }
}

/**
 * Represents a model class that is always persisted
 *
 * A ``Persisted`` model class is designed to be exclusively fetched from the backend
 */
trait Persisted[+T, +T2] extends Persistable {
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

  /**
   *  Retrieve a refreshed version of the persisted object
   */
  def refetch(): Option[T]
  /**
   * Save the persisted object without checking the version of the record, that means that other updates
   * occurred between the fetch of the record and this update are silently ignored
   *
   * @return true if the update was executed successfully
   */
  def update(): Boolean

  /**
   * Save the persisted object checking the version of the record, that means that no other updates
   * have been occurred since the fetch of the record
   *
   * @return true if the update was executed successfully
   */
  def updateWithVersion(): Boolean

  /**
   * Delete permanently the persisted object.
   *
   * @return true if the delete was executed successfully
   */
  def delete(): Boolean
}

trait PersistedTableTrait[T <: Persisted[T, T2], T2] {
  def findAll: Seq[T]
  def findById(id: Int): Option[T]
  def insert(obj: T2): Int
  def update(obj: T): Boolean
  def updateWithVersion(obj: T): Boolean
  def delete(obj: T): Boolean
  def deleteById(id: Int): Boolean
}

abstract class PersistedTable[T <: Persisted[T, T2], T2](override val schemaName: Option[String], override val tableName: String)
  extends Table[T](schemaName, tableName)
  with PersistedTableTrait[T, T2] 
  with Backend		  
{
  def this(tableName: String) = this(None, tableName)
  
}