package example

import cats.effect._
import org.http4s.ember.server._
import org.http4s.implicits._
import com.comcast.ip4s._
import smithy4s.http4s.swagger.docs
import smithy4s.http4s.SimpleRestJsonBuilder
import smithy4s.hello.{HelloWorldService, TodoService}
import cats.implicits._
import org.http4s.server.middleware._
import org.http4s.server.staticcontent._
import java.io.File
import org.http4s.server.Router

object Main extends IOApp {
    
    def run(args: List[String]): IO[ExitCode] = 

        val ui =  resourceServiceBuilder[IO]("").toRoutes
        val ui2 = fileService[IO](FileService.Config("./assets"))
        
        val helloRoutes = SimpleRestJsonBuilder.routes(HelloWorldImpl).make
        val todoRoutes = SimpleRestJsonBuilder.routes(TodoImpl).make
        val docHelloRoutes = docs[IO](HelloWorldService)
        val docTodoRoutes = docs[IO](TodoService)
        
        val uiRouter = Router("/assets" -> ui)

        val allRoutes = (todoRoutes, helloRoutes).mapN(_ <+> _).map(in => 
                in <+> docTodoRoutes <+> docHelloRoutes <+> uiRouter
            ).map(_.orNotFound)        

        val server = allRoutes.map {
            routes => 
                println(" ---->> !!!! <<----")
                println("HEALTH RISK : This configuration bypasses CORS. You have been warned.")
                println(" ---->> !!!! <<----")
                val corsBypass = CORS.policy.withAllowOriginAll(routes)
                EmberServerBuilder.default[IO]
                .withPort(port"8080")
                .withHost(host"localhost")
                .withHttpApp(corsBypass)
                //.withHttpApp(ui.orNotFound)
                .build
        }.map(_.use(_ => IO.never).as(ExitCode.Success))
        
        server.getOrElse(IO(ExitCode.Error))
        
}