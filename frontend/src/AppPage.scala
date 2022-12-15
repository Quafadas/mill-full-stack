package frontend

import com.raquo.waypoint.*
import upickle.default._
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import urldsl.vocabulary.{Segment, Param, UrlMatching}
import urldsl.language.PathSegment
import urldsl.errors.DummyError

sealed trait AppPage {}
implicit val rw: ReadWriter[AppPage] = macroRW

object AppPage:
  case object Home extends AppPage


  val uiPath : PathSegment[Unit, DummyError] = root / "ui"

  val homeRoute : Route[Home.type, Unit] = Route.static(AppPage.Home, uiPath / endOfSegments)

  def renderPage(using router: Router[AppPage ]) : Signal[HtmlElement] = 
    SplitRender[AppPage, HtmlElement](router.$currentPage)
      .collectStatic(AppPage.Home)(HomePage.render())
      .$view

  val router: Router[AppPage] = new Router[AppPage](
    routes = List(homeRoute),
    getPageTitle = _.toString,
    serializePage = page => write(page)(rw),
    deserializePage = pageStr => read(pageStr)(rw)
  )(
    $popStateEvent = L.windowEvents.onPopState, // this is how Waypoint avoids an explicit dependency on Laminar
    owner = L.unsafeWindowOwner // this router will live as long as the window
  )
