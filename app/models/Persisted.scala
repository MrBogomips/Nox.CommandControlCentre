package models

import java.sql.Timestamp


/**
 * Represents a model class that is always persisted
 * 
 * A ``Persisted`` model class is designed to be exclusively fetched from the backend
 */
trait Persisted[A] {
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
  def refetch():Option[A]
    /**
   * Save the persisted object without checking the version of the record, that means that other updates
   * occurred between the fetch of the record and this update are silently ignored
   *
   * @return true if the update was executed successfully
   */
  def update():Boolean

  /**
   * Save the persisted object checking the version of the record, that means that no other updates
   * have been occurred since the fetch of the record
   *
   * @return true if the update was executed successfully
   */
  def updateWithVersion():Boolean

  /**
   * Delete permanently the persisted object. 
   *
   * @return true if the delete was executed successfully
   */
  def delete(): Boolean
}