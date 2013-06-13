package models

import java.sql.Timestamp

trait Persistable {
  /**
   * returns ``true`` if the object was persisted
   */
  def isPersisted: Boolean
  /**
   * The record creation time
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def creationTime: Timestamp
  /**
   * The record last modification time
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def modificationTime: Timestamp
  /**
   * The record version number
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def version: Int
  /**
   * Utility method to support persistance assertions
   */
  def requirePersistance =
    require(isPersisted, "this instance is not persisted on the backend")
}

/**
 * Represents a model class that could be persisted on the backedn
 */
trait MaybePersisted extends Persistable {
  /**
   * The recordInfo provided by the instance
   */
  val recordInfo: Option[RecordInfo]
  /**
   * returns ``true`` if the object was persisted
   */
  def isPersisted = recordInfo.isDefined
  /**
   * The record creation time
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def creationTime = recordInfo.get.creationTime
  /**
   * The record last modification time
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def modificationTime = recordInfo.get.creationTime
  /**
   * The record version number
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def version = recordInfo.get.version
}

/**
 * Represents a model class that is always persisted
 */
trait Persisted extends Persistable {
  /**
   * The recordInfo provided by the instance
   */
  val recordInfo: RecordInfo
  /**
   * returns ``true`` if the object was persisted
   */
  def isPersisted = true
  /**
   * The record creation time
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def creationTime = recordInfo.creationTime
  /**
   * The record last modification time
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def modificationTime = recordInfo.creationTime
  /**
   * The record version number
   *
   * @throws NoSuchElementException if the object wasn't persisted
   */
  def version = recordInfo.version
}