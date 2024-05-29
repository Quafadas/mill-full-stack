import $ivy.`com.disneystreaming.smithy4s::smithy4s-mill-codegen-plugin::dev-SNAPSHOT`
import $ivy.`com.goyeau::mill-scalafix::0.3.2`
import $ivy.`com.github.lolgab::mill-crossplatform::0.2.4`

import $file.CustomZincWorkerModule

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

import os.{GlobSyntax, /}
import smithy4s.codegen.mill._

// Allows mill to resolve the "meta-build" behind a corporate proxy
// object CustomZincWorkerModule extends ZincWorkerModule with CoursierModule {

// }

object Config {
  def scalaVersion = "3.4.0"
  def scalaJSVersion = "1.16.0"
  def laminarVersion = "17.0.0-M6"
  def circeVersion = "0.14.6"
  val smithy4sVersion = smithy4s.codegen.BuildInfo.version
  val http4sVersion = "0.23.25"
  val scribeVersion = "3.13.0"

  def sharedDependencies = Agg(
    ivy"io.github.quafadas::dedav4s::0.9.0-RC9",
    ivy"com.disneystreaming.smithy4s::smithy4s-core::${smithy4sVersion}",
    ivy"com.disneystreaming.smithy4s::smithy4s-http4s::${smithy4sVersion}",
    ivy"com.outr::scribe::$scribeVersion",
    ivy"com.outr::scribe-cats::$scribeVersion",
    //ivy"com.lihaoyi::upickle::3.1.0"
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
    ivy"org.tpolecat::skunk-core:0.6.3",
    ivy"is.cir::ciris:3.5.0",
    ivy"io.chrisdavenport::mules:0.7.0",
    ivy"org.flywaydb:flyway-core:10.8.1",
    ivy"org.postgresql:postgresql:42.7.2"
  )

  def jsDependencies = Agg(
    ivy"""com.raquo::laminar::$laminarVersion""",
    ivy"""be.doeraene::web-components-ui5::1.21.0""",
    ivy"""com.raquo::waypoint::7.0.0""",
    ivy"org.scala-js::scalajs-dom::2.8.0",
    ivy"org.scala-js::scalajs-java-securerandom::1.0.0".withDottyCompat(scalaVersion),
    ivy"org.http4s::http4s-dom::0.2.11",
    ivy"org.http4s::http4s-client::${http4sVersion}",
    // ivy"io.laminext::core::0.16.2"
  )
}

trait CommonBuildSettings extends ScalaModule {
  // def repositoriesTask = CustomZincWorkerModule.CustomZincWorkerModule.repositoriesTask
  // def zincWorker = CustomZincWorkerModule.CustomZincWorkerModule
  def scalaVersion = Config.scalaVersion
}

trait Common extends ScalaModule with CommonBuildSettings with ScalafixModule {
  // def repositories = super.repositories ++ Seq(
  //   MavenRepository("https://jitpack.io")
  // )
  // def zincWorker = CustomZincWorkerModule.CustomZincWorkerModule

  def scalaVersion = Config.scalaVersion

  def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies
}
trait CommonJS extends Common with ScalaJSModule {
  def scalaJSVersion = Config.scalaJSVersion
}

object shared extends CrossPlatform {
  trait Shared extends CrossPlatformScalaModule with Common with Smithy4sModule with CommonBuildSettings  with ScalafmtModule {
    def smithy4sInputDir = T.source { millSourcePath / os.up / "smithy" }
    def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies
  }
  object jvm extends Shared {
    object test extends CrossPlatformSources with ScalaTests with TestModule.Munit {
      def ivyDeps = Agg(
        ivy"org.scalameta::munit::1.0.0-M11"
      )
    }
  }
  object js extends Shared with CommonJS
}

object backend extends Common with ScalafmtModule with ScalafixModule { // with ScalafixModule
  def repositoriesTask = CustomZincWorkerModule.CustomZincWorkerModule.repositoriesTask
  def ivyDeps =
    super.ivyDeps() ++ Config.jvmDependencies ++ Config.sharedDependencies

  def moduleDeps = Seq(shared.jvm)

  def assemblyRules = {
    // Run a the full frontend build
    println(
      "assuming you've already built and packaged the frontend with npm run build... "
    )
    val resourcePath = backend.millSourcePath / "resources" / "assets"
    if (os.exists(resourcePath)) {
      os.remove.all(resourcePath)
    }
    println(
      "assuming you've already built and packaged the frontend with npm run build... "
    )
    os.makeDir.all(resourcePath)
    os.copy(
      frontend.millSourcePath / "ui" / "dist" / "index.html",
      resourcePath / "index.html"
    )
    os.copy(
      frontend.millSourcePath / "ui" / "dist" / "assets",
      resourcePath,
      mergeFolders = true
    )
    /*     os.copy(
      backend.millSourcePath / "src" / "gen" / "openapi",
      backend.millSourcePath / "resources",
      mergeFolders = true
    ) */

    val assets = os.walk(backend.millSourcePath / "resources")
    println(assets)
    assets.map(x => Rule.Append(x.toString))
  }
}

object frontend extends CommonJS with ScalafmtModule  {
  // def repositoriesTask = CustomZincWorkerModule.CustomZincWorkerModule.repositoriesTask
  def moduleKind = ModuleKind.ESModule
  def moduleSplitStyle = ModuleSplitStyle.SmallModulesFor(List("frontend"))

  override def scalaJSImportMap = T {
    Seq(
      ESModuleImportMapping.Prefix("@ui5/webcomponents", "https://unpkg.com/@ui5/webcomponents"),
      ESModuleImportMapping.Prefix("@ui5/webcomponents-fiori", "https://unpkg.com/@ui5/webcomponents-fiori"),
      ESModuleImportMapping.Prefix("@ui5/webcomponents-icons", "https://unpkg.com/@ui5/webcomponents-icons")
    )
  }

  // def dev = T {
  //   public(fastLinkJS)()
  // }
  // def publicProd = T {
  //   public(fullLinkJS)()
  // }
  def moduleDeps = Seq(
    shared.js,
  ) // ++ super.moduleDeps // ++ Seq(scalablytyped.stModule)

  def ivyDeps = super.ivyDeps() ++ Config.jsDependencies
}

private def public(jsTask: Task[Report]): Task[Map[String, os.Path]] = T.task {
  val jsDir = jsTask().dest.path
  Map(
    "@public" -> jsDir
  )
}

def millw() = T.command {
  val target = mill.util.Util.download("https://raw.githubusercontent.com/lefou/millw/main/millw")
  val millw = build.millSourcePath / "millw"
  os.copy.over(target.path, millw)
  os.perms.set(millw, os.perms(millw) + java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE)
  target
}