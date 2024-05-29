package frontend

import com.raquo.waypoint.*
import upickle.default.*
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import urldsl.vocabulary.{Segment, Param, UrlMatching}
import urldsl.language.PathSegment
import urldsl.errors.DummyError
import hello.TodoService
import cats.effect.IO
import scala.concurrent.Future
import cats.effect.unsafe.implicits.global
import org.http4s.Uri
import org.http4s.dom.FetchClientBuilder
import hello.TodoClient

sealed trait Pages
given rw: ReadWriter[Pages] = macroRW

object Pages:
  case object Home extends Pages
  case object Chat extends Pages

  given home: ReadWriter[Home.type] = macroRW
  given chat: ReadWriter[Chat.type] = macroRW

  val uiPath: PathSegment[Unit, DummyError] = root

  val homeRoute: Route[Home.type, Unit] = Route.static(Pages.Home, uiPath / endOfSegments)

  val chatRoute: Route[Chat.type, Unit] = Route.static(Pages.Chat, uiPath / "chat" / endOfSegments)

  def renderPage(using router: Router[Pages], api: Api): Signal[HtmlElement] =
    SplitRender[Pages, HtmlElement](router.currentPageSignal)
      .collectStatic(Pages.Home)(HomePage.render())
      .collectStatic(Pages.Chat)(ChatPage.render())
      .signal

  val router: Router[Pages] = new Router[Pages](
    routes = List(
      homeRoute,
      chatRoute
    ),
    getPageTitle = _.toString,
    serializePage = page => write(page)(using rw),
    deserializePage = pageStr => read(pageStr)(using rw)
  )(
    popStateEvents = L.windowEvents(_.onPopState), // this is how Waypoint avoids an explicit dependency on Laminar
    owner = L.unsafeWindowOwner // this router will live as long as the window
  )
end Pages
