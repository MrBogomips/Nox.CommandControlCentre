package patterns.models

import scala.slick.driver.PostgresDriver.simple._

/**
  * Represents basic CRUD operations supported by all DAOs
  * 
  * @tparam A Represents the model contract
  * @tparam B Represents the model class
  * @tparam C Represents the persisted class
  */
trait CrudOperations[A <: Validatable, B <: Model[A], C <: Persisted[B]] {
  def find(enabled: Option[Boolean] = None): Seq[C]
  def findById(id: Int): Option[C]
  def insert(uobj: B): Int
  def update(uobj: C): Boolean
  def updateWithVersion(uobj: C): Boolean
  def delete(obj: C): Boolean = deleteById(obj.id)
  def deleteById(id: Int): Boolean
}

/**
 * Represents CRUD operation supported by entities that have an alternate key based on a name
 * 
 * @tparam A Represents the model contract
 * @tparam B Represents the model class
 * @tparam C Represents the persisted class
 */
trait NameEntityCrudOperations[A <: Validatable, B <: Model[A], C <: Persisted[B]]
  extends CrudOperations[A, B, C] {
  def findByName(name: String): Option[C]
}