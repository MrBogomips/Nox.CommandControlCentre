import org.specs2.mutable._
import org.specs2.specification._
import org.specs2.execute.{ AsResult, Result }

import play.api.test._
import play.api.test.Helpers._

import models.{ Operators, Operator, OperatorPersisted }

abstract class WithOperator2 extends WithApplication with Scope {
  override def around[T: AsResult](t: => T): Result = super.around {
    t
  }
}

abstract class WithOperator[OperatorPersisted] extends WithApplication
  with Outside[models.OperatorPersisted] {
  private var operator: Option[models.OperatorPersisted] = None

  override def around[T: AsResult](t: => T): Result = super.around {
    val operatorId = Operators.insert(Operator("Giovanni", "Costagliola", None, true))
    operator = Operators.findById(operatorId)
    val result = t
    Operators.deleteById(operatorId)
    result
  }

  def outside: models.OperatorPersisted = operator.get
}

class OperatorSpec extends Specification {
  sequential
  "Operator model" should {
    /*
    "be inserted by an Operator instance" in new WithApplication {
      val op = Operator("Giovanni", "Costagliola", None, true)
      Operators.insert(op) > 0
    }

    "testing DB WithApplication" in new WithApplication {
      Operators.findById(0)
      todo
    }
*/
    "failure" in {
      failure
    }
    "todo" in {
      todo
    }
    "testing DB WithApplication" in new WithApplication {
      Operators.findById(0)
      0 == 1
    }
    /*
    "testing DB WithOperator" in new WithOperator2 {
      Operators.findById(0)
      todo
    }
	*/
    /*
    "be retrieved by id" in new WithOperator { (operator: OperatorPersisted) =>
      {
        val id = operator.id
        Operators.findById(id).map {
          _ => success
        }.getOrElse(failure)
      }
    }
    * *
    */
    //val operatorId = operator.id
    //val op = Operator("Giovanni", "Costagliola", None, true)
    //Operators.insert(op) must equalTo(true)

    /*
    "be retrieved by id" in new WithApplication {
      val Some(obj) = Operators.findById(1)
      obj must not beNull
    }
    "start with 'Hello'" in {
      "Hello world" must startWith("Hello")
    }
    "end with 'world'" in {
      "Hello world" must endWith("world")
    }
    */
  }
}