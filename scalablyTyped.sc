import mill._, mill.scalalib._, mill.scalajslib._
import $ivy.`com.github.lolgab::mill-scalablytyped::0.1.12`
import com.github.lolgab.mill.scalablytyped._

object Versions{
  val scalaVersion = "3.4.0"
  val scalaJSVersion = "1.15.0"
}


object stModule extends ScalaJSModule with ScalablyTyped {
  def scalaVersion = Versions.scalaVersion
  def scalaJSVersion = Versions.scalaJSVersion

  def scalablyTypedIgnoredLibs = Seq(
    "less",
     "@ui5/webcomponents",
    "@ui5/webcomponents-fiori",
    "zod",
    "vega-embed",
    "@ui5/webcomponents-icons",
    "@dqbd/tiktoken", 
    "axios"
  )

}