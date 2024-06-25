package frontend

import be.doeraene.webcomponents.ui5._
import be.doeraene.webcomponents.ui5.configkeys._
import cats.effect._
import cats.effect.unsafe.implicits.global
import com.raquo.laminar.api.L._
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.scalajs.js

def io2Es[A](in: IO[A]): EventStream[A] = EventStream.fromFuture(in.unsafeToFuture())

