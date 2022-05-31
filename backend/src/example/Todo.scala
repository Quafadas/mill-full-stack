package example

import smithy4s.hello._
import cats.effect.IO
import cats.syntax.all._

object TodoImpl extends TodoService[IO] {

    def getTodo(id: String) = IO.pure{
        Todo(id, "do something".some, false.some)
    }

    def getTodos() = IO.pure{
        Todos(
            List(
                Todo("1", "do something".some, false.some),
                Todo("2", "do something".some, false.some)
            ).some
        )        
    }

}
