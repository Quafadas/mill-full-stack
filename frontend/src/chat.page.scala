package frontend

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.{*, given}
import io.laminext.syntax.core._
import cats.syntax.option.*

import com.raquo.waypoint.Router
import typings.langchain.llmsOpenaiMod.OpenAIChat
import typings.openai.mod.ChatCompletionRequestMessageRoleEnum
import typings.openai.distApiMod.ChatCompletionRequestMessage
import typings.openai.distConfigurationMod.ConfigurationParameters
import typings.langchain.anon.PartialOpenAIChatInputParAzureOpenAIApiCompletionsDeploymentName
import typings.langchain.llmsOpenaiMod.OpenAI
import typings.langchain.distToolsJsonMod.JsonObject
import typings.langchain.anon.PartialOpenAIInputPartial
import typings.langchain.distAgentsMod.OpenApiToolkit
import org.scalablytyped.runtime.StringDictionary
import typings.langchain.distToolsJsonMod.JsonSpec
import typings.langchain.agentsMod.JsonToolkit
import typings.jsYaml.mod.load
//import typings.langchain.agentsMod

object ChatPage {

  // private val at            = "at_something"
  private val currentText = Var("tell me about insurance linked securities at schroders in 10 words")
  private val sdeSpec = Var[Option[String]](None)
  private val response = Var[Option[String]]("".some)
  private val loginError = Var[Option[Throwable]](None)
  private val hasloginError = loginError.signal.map(_.isDefined)
  private val jwt = Var[String]("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")
  private val chat = jwt.signal.map(t => constructConversation(t, ChatModelProvider.Azure))  
  private val responseLoading = Var(false)
  private val openApiSpec = FetchStream.get("https://raw.githubusercontent.com/openai/openai-openapi/master/openapi.yaml").toSignal("")

  enum ChatModelProvider(val url: String):
    case OpenAI extends ChatModelProvider("https://dev.int.api.schroders.com/openai/v1")
    case Azure extends ChatModelProvider("https://dev.int.api.schroders.com/microsoft/openai/inferences/v1/deployments/gpt-35-turbo")
  end ChatModelProvider

  private def constructConversation(apiKey: String, cm: ChatModelProvider): OpenAIChat =
    val seedRole =
      ChatCompletionRequestMessage("You are a helpful AI assistent to the ILS team.", ChatCompletionRequestMessageRoleEnum.System)
    val messages = scalajs.js.Array(seedRole)
    val chatConf = ConfigurationParameters()
    chatConf.setApiKey(s"$apiKey")
    //chatConf.setBasePath(cm.url)

    val chain = PartialOpenAIChatInputParAzureOpenAIApiCompletionsDeploymentName()
    chain.setOpenAIApiKey(s"$apiKey")
    chain.setTemperature(0.0)
    OpenAIChat(chain, chatConf)

  end constructConversation

  private def constructAgent(apiKey: String, cm: ChatModelProvider, spec: String)(using api: Api) =
    println("here")
    println(spec)
    val chatConf = ConfigurationParameters()
    chatConf.setApiKey(s"$apiKey")
    //chatConf.setBasePath(cm.url)

    val model = PartialOpenAIInputPartial()
    model.setOpenAIApiKey(s"$apiKey")
    model.setTemperature(0.0)

    val baseModel = OpenAI(model, chatConf)

    val bah = load(spec).asInstanceOf[JsonObject]
    //println(bah)
    val toolkit = OpenApiToolkit(
      JsonSpec(bah),
      baseModel, 
      StringDictionary("Content-Type" -> "application/json", "Authorization" -> apiKey)
    )
    //agentsMod.createOpenApiAgent(baseModel, toolkit)

  end constructAgent

  // val chain =

  // private val msalApp = new PublicClientApplication(config)

  // private val tokenRequest = TokenRequest(
  //   scopes = scalajs.js.Array(
  //     https://dev.int.api.schroders.com/.default
  //     // "User.Read.All"
  //   )
  // )

  private def getToken: EventStream[String] = EventStream.fromValue(
    "token"
  )

  def render()(using api: Api, router: Router[Pages]): Div =
    println("rendering")
    val apiAgent = jwt.signal.combineWith(openApiSpec)map((t, s) => constructAgent(t, ChatModelProvider.Azure, s))
    val sendRequest = EventBus[Unit]()
    div(
      "Welcome to an experimewnt in langchain",
      //apiAgent --> sendRequest,
      chat
        .combineWith(sendRequest.events.toWeakSignal)
        .changes
        .flatMap((c, o) =>
          o match
            case None => EventStream.fromValue("".some)
            case Some(_) =>
              println("make request")
              response.set(None)
              responseLoading.set(true)
              EventStream.fromJsPromise(c.call(currentText.now()).`then` { in =>
                in.some
              })
        ) --> response,
      TextArea(
        _.value := currentText.now(),
        _.events.onInput.mapToValue --> currentText
      ),
      span(
        Button(
          "ILS GPT",
          _.events.onClick.mapTo(()) --> sendRequest
        )
      ),
      p(),
      responseLoading.signal.childWhenTrue {
        div("loading")
      },
      child.maybe <-- response.signal.map { s =>
        responseLoading.set(false)
        s.map(s1 =>
          val asHtml = typings.marked.mod.marked.parse(s1)
          foreignHtmlElement(
            com.raquo.laminar.DomApi.unsafeParseHtmlString(s"<div>$asHtml</div>")
          )
        )
      }
    )

  end render
}
