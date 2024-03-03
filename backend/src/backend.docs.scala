package hello

import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s.*
import cats.effect.IO
import cats.effect.Resource
import hello.HelloWorldService

object Docs:
  val myDocRoutes = smithy4s.http4s.swagger.docs[IO](HelloWorldService)
end Docs
