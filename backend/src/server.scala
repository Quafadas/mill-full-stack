package hello

import cats.effect.*

import com.comcast.ip4s.*
import org.http4s.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.server.middleware.*

import scala.concurrent.duration.*

object Main extends IOApp:
  // todo : https://github.com/http4s/http4s/issues/2977
  def run(args: List[String]): IO[ExitCode] =
    val allRoutes = makeRoutes

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
          .withHttpApp(corsBypass.orNotFound)
          .withShutdownTimeout(10.millis)
          // .withHttpApp(ui.orNotFound)
          .build
      }
      .map(_.use(_ => IO.never).as(ExitCode.Success))

    server.getOrElse(IO(ExitCode.Error))
  end run
end Main
