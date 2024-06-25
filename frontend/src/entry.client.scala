package frontend

import be.doeraene.webcomponents.ui5.*
import cats.effect.*
import cats.effect.unsafe.implicits.global
import com.raquo.laminar.api.L.*
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global

def io2Es[A](in: IO[A]): EventStream[A] = EventStream.fromFuture(in.unsafeToFuture())

