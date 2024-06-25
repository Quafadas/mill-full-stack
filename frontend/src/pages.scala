package frontend

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.*
import org.scalajs.dom
import org.scalajs.dom.document
import upickle.default.*
import urldsl.errors.DummyError


sealed trait Page derives ReadWriter
case class UserPage(userId: Int) extends Page derives ReadWriter
case object HomePage extends Page derives ReadWriter

val userRoute = Route(
  encode = (userPage: UserPage) => userPage.userId,
  decode = arg => UserPage(userId = arg),
  pattern = root / "app" / "user" / segment[Int] / endOfSegments
)

val homeRoute = Route.static(HomePage, root )

val router = new Router[Page](
  routes = List(userRoute, homeRoute),
  getPageTitle = _.toString, // mock page title (displayed in the browser tab next to favicon)
  serializePage = page => write(page), // serialize page data for storage in History API log
  deserializePage = pageStr => read(pageStr) // deserialize the above
)(
  popStateEvents = L.windowEvents(_.onPopState), // this is how Waypoint avoids an explicit dependency on Laminar
  owner = L.unsafeWindowOwner // this router will live as long as the window
)

def splitter(using a: Api, r: Router[Page]) = SplitRender[Page, HtmlElement](router.currentPageSignal)
  .collectSignal[UserPage] { userPageSignal => h1("user") }
  .collectStatic(HomePage) { HomePageRender.render() }


@main
def main: Unit =
  given a : Api = Api.create()
  given r: Router[Page] = router
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    div(
      child <-- splitter.signal
    )
  )