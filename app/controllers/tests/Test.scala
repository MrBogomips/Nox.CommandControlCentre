package controllers.tests

import play.api._
import play.api.mvc
import play.api.mvc._
import play.api.libs._
import play.api.libs.iteratee._

object Test extends Controller {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  def asyncTest = Action.async {
    val futureInt = scala.concurrent.Future { Thread.sleep(5000); 1 }
    futureInt.map(i => Ok("Got result: "+i))
  }

  def fibonacci = WebSocket.using[String] { request =>
    val fibo = new Iterator[String] {
      var f0: BigInt = -1
      var f1: BigInt = 1
      def hasNext = true
      def next = {
        Thread.sleep(100)
        val f2 = f0 + f1
        f0 = f1
        f1 = f2
        f2.toString
      }
    }
    
    val ciccio = new Iterator[String] {
      def hasNext = true
      def next = {
        Thread.sleep(200) 
        "ciccio"
      }
    }
    
    // Just consume and ignore the input
    val in = Iteratee.ignore[String]

    // Send a single 'Hello!' message and close
    val out = Enumerator("Fibonacci's numbers")
      .andThen(Enumerator.enumerate(fibo) interleave Enumerator.enumerate(ciccio))

    (in, out)
  }
  
} 