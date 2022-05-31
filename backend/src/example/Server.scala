package example

import cats.effect._
import org.http4s.ember.server._
import org.http4s.implicits._
import com.comcast.ip4s._
import smithy4s.http4s.swagger.docs
import smithy4s.http4s.SimpleRestJsonBuilder
import smithy4s.hello.{HelloWorldService, TodoService}
import cats.implicits._

object Main extends IOApp {
    
    def run(args: List[String]): IO[ExitCode] = 

        val helloRoutes = SimpleRestJsonBuilder.routes(HelloWorldImpl).make
        val todoRoutes = SimpleRestJsonBuilder.routes(TodoImpl).make
        val docHelloRoutes = docs[IO](HelloWorldService)
        val docTodoRoutes = docs[IO](TodoService)

        val allRoutes = (todoRoutes, helloRoutes).mapN(_ <+> _).map(in => 
                in <+> docTodoRoutes <+> docHelloRoutes
            ).map(_.orNotFound)        

        val server = allRoutes.map {
            routes => 
                EmberServerBuilder.default[IO]
                .withPort(port"8080")
                .withHost(host"localhost")
                .withHttpApp(routes)
                .build
        }.map(_.use(_ => IO.never).as(ExitCode.Success))
        
        server.getOrElse(IO(ExitCode.Error))
        
}