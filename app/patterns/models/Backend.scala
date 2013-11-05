package patterns.models

import play.Logger
import play.api.db.DB
import play.api.Play.current
import scala.slick.session.{ Database, Session }
import scala.slick.jdbc.{StaticQuery0, GetResult}

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
  val log = Logger.of("noxccc.BackendOperation")

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
      log.info(s"SQL:$method[$operation]: $loginfo...")
      log.debug(s"SQL:$method[$operation]: ${sql.getStatement}")
      val ret = f(sql)
      log.info(s"SQL:$method[$operation]: $loginfo done with return value [$ret]")
      ret
  }
}