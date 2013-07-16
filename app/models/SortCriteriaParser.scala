package models

/**
  * Utility class to define sorting criterias over a query (i.e. a table)
  *
  * A sorting criteria is a string made up of characters, hyphen and underscore.
  */
abstract class SortCriteriaParser(criterias: Map[String, String]) {
  require(criterias != Nil, "Criterias cannot be emmpty")
  
  private val tokenReSyntax = "[a-z_]+"

  criterias.map { c =>
    require(c._1.matches("[a-z_]+"), s"""Criteria's key "${c._1}" must match the regex "[a-z_]+"""")
  }

  /**
    * Parse a string of sorting criteria
    *
    * Example
    * ```
    * field1,field2+,field3- is parsed to
    * ORDER BY <FIELD1> ASC, <FIELD2> ASC, <FIELD3> DESC
    */
  def parseOrderBy(sortingCriteria: String): String = {
    val re = "([_a-z]+)([-+]?)".r
    
    val tokens = for {
      t <- sortingCriteria.split(",").map(_.trim.toLowerCase)
      re(token, ord) <- re findFirstIn t if (criterias.contains(token))
      dir = if (ord == "-") "DESC" else "ASC"
    } yield s"${criterias(token)} $dir"
    
    tokens match {
      case Array() => ""
      case _ => s"""ORDER BY ${tokens mkString ", "}""" 
    }
  }
}
/**
 * A usage example
 */
object SortCriteriaTest extends SortCriteriaParser(Map("name" -> "T1.chr_name", "surname" -> "T2.surname"))

