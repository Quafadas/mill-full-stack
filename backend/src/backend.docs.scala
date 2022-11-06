package backend

import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s._
import cats.effect.IO
import cats.effect.Resource
import smithy4s.hello.HelloWorldService

object Docs {
    val myDocRoutes = smithy4s.http4s.swagger.docs[IO](HelloWorldService)
}