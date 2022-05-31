package example

import smithy4s.hello._
import cats.effect.IO
import cats.syntax.all._

object HelloWorldImpl extends HelloWorldService[IO] {
  def hello(name: String, town: Option[String]) = IO.pure {
    town match {
      case None => Greeting("hi")
      case Some(t) => Greeting(s"hello $name from $t !")
    }
  }

  def greet(name: String) = IO.pure {
    GreetOutput(s"hi $name".some)
  }

}
