package models

import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.{ Database, Session }
import scala.slick.jdbc.{ StaticQuery0, SQLInterpolationResult, GetResult }

object BackendOperation extends Enumeration {
  type BackendOperation = Value

  val CREATE = Value("CREATE")
  val DROP = Value("DROP")
  val ALTER = Value("ALTER")
  val SELECT = Value("SELECT")
  val INSERT = Value("INSERT")
  val UPDATE = Value("UPDATE")
  val DELETE = Value("DELETE")
  val TRUNCATE = Value("TRUNCATE")
  val SCRIPT = Value("SCRIPT")
}
import BackendOperation._

/**
 * Provided backend interaction utilities
 */
trait Backend {
  /**
   * The backend db
   */
  val db = Database.forDataSource(DB.getDataSource())

  /**
   * Execute an INSERT statement and returns the number of rows affected
   */
  def executeInsert(loginfo: String, sql: StaticQuery0[Int])(implicit session: Session): Int =
    executeSql(BackendOperation.INSERT, loginfo, sql) { _.first }

  /**
   * Execute an UPDATE statement and returns the number of rows affected
   */
  def executeUpdate(loginfo: String, sql: StaticQuery0[Int])(implicit session: Session): Int =
    executeSql(BackendOperation.UPDATE, loginfo, sql) { _.first }

  /**
   * Execute a DELETE statement and returns the number of rows affected
   */
  def executeDelete(loginfo: String, sql: StaticQuery0[Int])(implicit session: Session): Int =
    executeSql(BackendOperation.DELETE, loginfo, sql) { _.first }

  /**
   * Execute a generic sql decorated with logging info
   *
   * @param f is the callback function where you provide the mani
   */
  def executeSql[P, A](operation: BackendOperation, loginfo: String, sql: StaticQuery0[P])(f: StaticQuery0[P] => A)(implicit session: Session, getResult: GetResult[P]): A = {
    val method = "executeSql"
    Logger.info(s"SQL:$method[$operation]: $loginfo...")
    Logger.debug(s"SQL:$method[$operation]: ${sql.getStatement}")
    val ret = f(sql)
    Logger.info(s"SQL:$method[$operation]: $loginfo done with return value [$ret]")
    ret
  }

  /**
   * Helper method to check if an ``Persisted`` object requires to be persisted or not
   *
   * @return ``None`` if the operation has been skipped because the object was already persisted
   */
  def withPersistableObject[A <: Persisted[A,C], B, C](obj: A, force: Boolean = false)(f: => B): Option[B] =
    withPersistableObject[A, Option[B]](obj, None, force)(Some(f))

  /**
   * Helper method to check if an ``Persisted`` object requires to be persisted or not
   *
   * @return ``defailt`` if the operation has been skipped because the object was already persisted
   */
  def withPersistableObject[A <: Persistable, B](obj: A, default: B, force: Boolean = false)(f: => B): B = {
    def persist = {
      val ret = f
      obj.persisted 
      ret
    }
    if (force) {
      if (obj.isPersisted)
        Logger.debug(s"object $obj is forced to be persisted even if it doesn't require it")
      else
        Logger.debug(s"object $obj requires to be persisted (with 'force' option)")
      persist
    } else if (!obj.isPersisted) {
      Logger.debug(s"object $obj requires to be persisted")
      persist
    } else {
      Logger.debug(s"object $obj is already persisted: skipped")
      default
    }
  }
}