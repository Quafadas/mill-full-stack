package frontend

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.{*, given}
import io.laminext.syntax.core._
import cats.syntax.option.*

import com.raquo.waypoint.Router
import scala.scalajs.js.annotation.JSImport


object ChatPage {

  private val currentText = Var("Understand how to make a POST request to openai /completions. Once understood, make the POST request with the prompt `tell me a joke`.")
  private val response = Var[Option[String]]("".some)
  private val loginError = Var[Option[Throwable]](None)
  private val hasloginError = loginError.signal.map(_.isDefined)  


  def render()(using api: Api, router: Router[Pages]): Div =
    
    div(
     
    )

  end render
}
