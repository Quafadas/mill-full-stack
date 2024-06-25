
package backend

import cats.effect.IO
import shared.HelloWorldService

object Docs:
  val myDocRoutes = smithy4s.http4s.swagger.docs[IO](HelloWorldService)
end Docs
