package example

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
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
import cask.model.Status.OK
import org.http4s.Response
import org.http4s.Status
import scala.io.Source
import org.http4s.Request
import org.http4s.HttpRoutes

object Main extends IOApp {
    // todo : https://github.com/http4s/http4s/issues/2977
    def run(args: List[String]): IO[ExitCode] = 

        val ui =  resourceServiceBuilder[IO]("").toRoutes        
        
        val helloRoutes = SimpleRestJsonBuilder.routes(HelloWorldImpl).make
        val todoRoutes = SimpleRestJsonBuilder.routes(TodoImpl).make
        val docHelloRoutes = docs[IO](HelloWorldService)
        val docTodoRoutes = docs[IO](TodoService)
        
        val assetRouter = Router("/assets" -> ui)        
        val homeRoute = HttpRoutes.of[IO] {
            case req @ GET -> Root =>
                StaticFile.fromResource("index.html", req.some).getOrElseF(NotFound())
            case req @ GET -> Root / "index.html" =>
                //Ok("hi")
                StaticFile.fromResource("index.html", req.some).getOrElseF(NotFound())
        }
        
        val apiRoutes = (todoRoutes, helloRoutes).mapN(_ <+> _)

        val allRoutes = apiRoutes.map(api => api <+> docTodoRoutes <+> docHelloRoutes <+> assetRouter <+> homeRoute).map(_.orNotFound)        

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