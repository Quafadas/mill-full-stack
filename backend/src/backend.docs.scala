
package backend

import smithy4s.http4s.SimpleRestJsonBuilder
import org.http4s.*
import cats.effect.IO
import cats.effect.Resource
import shared.HelloWorldService

object Docs:
  val myDocRoutes = smithy4s.http4s.swagger.docs[IO](HelloWorldService)
end Docs
