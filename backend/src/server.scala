package hello

import cats.effect.*
import cats.implicits.*
import com.comcast.ip4s.*
import org.http4s.*
import org.http4s.HttpRoutes
import org.http4s.Request
import org.http4s.Response
import org.http4s.dsl.io.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.middleware.*
import org.http4s.server.staticcontent.*
import shared.*
import smithy4s.http4s.SimpleRestJsonBuilder
import smithy4s.http4s.swagger.docs

object Main extends IOApp:
  // todo : https://github.com/http4s/http4s/issues/2977
  def run(args: List[String]): IO[ExitCode] =

    val ui = resourceServiceBuilder[IO]("/assets").toRoutes

    val helloRoutes = SimpleRestJsonBuilder.routes(HelloWorldImpl).make
    val todoRoutes = SimpleRestJsonBuilder.routes(TodoImpl).make
    val docHelloRoutes = docs[IO](HelloWorldService)
    val docTodoRoutes = docs[IO](TodoService)

    val assetRouter = Router("/assets" -> ui)
    val homeRoute = HttpRoutes.of[IO] { case req @ GET -> "ui" /: rest =>
      StaticFile.fromResource("index.html", req.some).getOrElseF(NotFound())
    }

    val apiRoutes = (todoRoutes, helloRoutes).mapN(_ <+> _)

    val allRoutes =
      apiRoutes.map(api => api <+> docTodoRoutes <+> docHelloRoutes <+> assetRouter <+> homeRoute).map(_.orNotFound)

    val server = allRoutes
      .map { routes =>
        println(" ---->> !!!! <<----")
        println("HEALTH RISK : This configuration bypasses CORS. You have been warned.")
        println(" ---->> !!!! <<----")
        println("dev server at http://localhost:8080")
        // Probably we only really want to GZIP the bundle, rather than the API routes, but I don't really know how to do that,
        // so I'm just gzipping the whole thing.
        val corsBypass = GZip(
          CORS.policy.withAllowOriginAll(
            ErrorHandling(routes)
          )
        )
        EmberServerBuilder
          .default[IO]
          .withPort(port"8080")
          .withHost(host"localhost")
          .withHttpApp(corsBypass)
          // .withHttpApp(ui.orNotFound)
          .build
      }
      .map(_.use(_ => IO.never).as(ExitCode.Success))

    server.getOrElse(IO(ExitCode.Error))
  end run
end Main
