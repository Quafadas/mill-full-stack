import $ivy.`com.disneystreaming.smithy4s::smithy4s-mill-codegen-plugin::0.18.23`
import $ivy.`com.goyeau::mill-scalafix::0.4.0`
import $ivy.`com.github.lolgab::mill-crossplatform::0.2.4`

import com.goyeau.mill.scalafix.ScalafixModule
import com.github.lolgab.mill.crossplatform._

import coursier.maven.MavenRepository

import mill._
import mill.modules.Assembly._

import mill.define.Target
import mill.define.Task
import mill.scalalib._
import mill.scalalib.scalafmt._
import mill.scalajslib._
import mill.scalajslib.api._
import mill.scalanativelib._

import os.{GlobSyntax, /}
import smithy4s.codegen.mill._

object Config {
  def scalaVersion = "3.4.2"
  def scalaJSVersion = "1.16.0"
  def laminarVersion = "17.0.0"
  def circeVersion = "0.14.8"
  val smithy4sVersion = smithy4s.codegen.BuildInfo.version
  val http4sVersion = "0.23.27"
  val scribeVersion = "3.15.0"

  def sharedDependencies = Agg(
    ivy"com.disneystreaming.smithy4s::smithy4s-core::${smithy4sVersion}",
    ivy"com.disneystreaming.smithy4s::smithy4s-http4s::${smithy4sVersion}",
    ivy"com.outr::scribe::$scribeVersion",
    ivy"com.outr::scribe-cats::$scribeVersion",
  ) ++ Agg(
    "io.circe::circe-core:",
    "io.circe::circe-generic:",
    "io.circe::circe-parser:"
  ).map(x => {
    ivy"$x$circeVersion"
  })

  def jvmDependencies = Agg(
    ivy"org.http4s::http4s-ember-server::${http4sVersion}",
    ivy"org.http4s::http4s-ember-client::${http4sVersion}",
    ivy"org.http4s::http4s-circe:${http4sVersion}",
    ivy"com.disneystreaming.smithy4s::smithy4s-http4s-swagger:${smithy4sVersion}",
    ivy"org.tpolecat::skunk-core:0.6.4",
    ivy"is.cir::ciris:3.6.0",
    ivy"io.github.quafadas::frontend-routes:0.2.0"
  )

  def jsDependencies = Agg(
    ivy"io.github.quafadas::dedav4s::0.9.0",
    ivy"""com.raquo::laminar::$laminarVersion""",
    ivy"""be.doeraene::web-components-ui5::1.21.2""",
    ivy"""com.raquo::waypoint::8.0.0""",
    ivy"org.scala-js::scalajs-dom::2.8.0",
    ivy"org.scala-js::scalajs-java-securerandom::1.0.0".withDottyCompat(scalaVersion),
    ivy"org.http4s::http4s-dom::0.2.11",
    ivy"org.http4s::http4s-client::${http4sVersion}",
    ivy"io.laminext::core::0.17.0"
  )
}

trait CommonBuildSettings extends ScalaModule {
  def scalaVersion = Config.scalaVersion

  override def scalacOptions: Target[Seq[String]] = super.scalacOptions() ++ Seq(
    "-Wunused:all",
  )
}

trait Common extends ScalaModule with CommonBuildSettings with ScalafixModule {

  def scalaVersion = Config.scalaVersion
  def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies
}
trait CommonJS extends Common with ScalaJSModule {
  def scalaJSVersion = Config.scalaJSVersion
}

trait CommonNative extends Common with ScalaNativeModule {
  def scalaNativeVersion = "0.4.0"
}

object shared extends CrossPlatform {
  trait Shared extends CrossPlatformScalaModule with Common with Smithy4sModule with CommonBuildSettings  with ScalafmtModule {
    def smithy4sInputDir = T.source { millSourcePath / os.up / "smithy" }
    def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies
  }

  // object native extends Shared with CommonNative {

  // }
  object jvm extends Shared {
    object test extends CrossPlatformSources with ScalaTests with TestModule.Munit {
      def ivyDeps = Agg(
        ivy"org.scalameta::munit::1.0.0"
      )
    }
  }
  object js extends Shared with CommonJS
}

object backend extends Common with ScalafmtModule with ScalafixModule {

  def ivyDeps =
    super.ivyDeps() ++ Config.jvmDependencies ++ Config.sharedDependencies

  def moduleDeps = Seq(shared.jvm)

  def frontendResources = T{PathRef(frontend.fullLinkJS().dest.path)}

  def staticAssets = T.source{PathRef(frontend.millSourcePath / "ui")}

  def allClasspath = T{localClasspath() ++ Seq(frontendResources()) ++ Seq(staticAssets())  }

  override def assembly: T[PathRef] = T{
    val internals = os.list( frontendResources().path).filter(p => p.last.startsWith("internal-") && os.isFile(p) && p.last.endsWith(".js")).map{ p =>
       s"""<link rel="modulepreload" href="${p.last}">"""
    }

    val index = os.read(staticAssets().path / "index.html")

    val modulesStringsInject = internals.mkString("\n", "\n", "\n")
    val headCloseTag = "</head>"
    val insertionPoint = index.indexOf(headCloseTag)

    val newHtmlContent = index.substring(0, insertionPoint) +
      modulesStringsInject +
      index.substring(insertionPoint)

    os.write.over(staticAssets().path / "index.html", newHtmlContent)

    Assembly.createAssembly(
      Agg.from(allClasspath().map(_.path)),
      manifest(),
      prependShellScript(),
      Some(upstreamAssembly2().pathRef.path),
      assemblyRules
    )
  }
}

object frontend extends CommonJS with ScalafmtModule  {

  def moduleKind = ModuleKind.ESModule

  def moduleSplitStyle = ModuleSplitStyle.SmallModulesFor(
    List("frontend", "shared")
  )

  override def scalaJSImportMap = T {
    Seq(
      ESModuleImportMapping.Prefix("@ui5/webcomponents-localization/", "https://cdn.jsdelivr.net/npm/@ui5/webcomponents-localization@1.24.7/"),
      ESModuleImportMapping.Prefix("@ui5/webcomponents/", "https://cdn.jsdelivr.net/npm/@ui5/webcomponents@1.24.7/"),
      ESModuleImportMapping.Prefix("@ui5/webcomponents-fiori/", "https://cdn.jsdelivr.net/npm/@ui5/webcomponents-fiori@1.24.7/"),
      ESModuleImportMapping.Prefix("@ui5/webcomponents-icons/", "https://cdn.jsdelivr.net/npm/@ui5/webcomponents-icons@1.24.7/")
    )
  }

  def moduleDeps = Seq(
    shared.js,
  )

  def ivyDeps = super.ivyDeps() ++ Config.jsDependencies
}