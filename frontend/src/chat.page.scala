package frontend

import cats.syntax.option.*
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.Router

object ChatPage:

  private val currentText = Var(
    "Understand how to make a POST request to openai /completions. Once understood, make the POST request with the prompt `tell me a joke`."
  )
  private val response = Var[Option[String]]("".some)
  private val loginError = Var[Option[Throwable]](None)
  private val hasloginError = loginError.signal.map(_.isDefined)

  def render()(using api: Api, router: Router[Page]): Div =
    div(
    )

  end render
end ChatPage
