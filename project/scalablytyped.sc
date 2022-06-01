import mill._, mill.scalalib._, mill.scalajslib._
import $ivy.`com.github.lolgab::mill-scalablytyped::0.0.5`
import com.github.lolgab.mill.scalablytyped._
import $file.versions

object stModule extends ScalaJSModule with ScalablyTyped with versions.CommonBuildSettings  {
  def scalaVersion = versions.scala
  def scalaJSVersion = versions.scalajs
  def scalablyTypedIgnoredLibs = Seq("vega-embed")
}