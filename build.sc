import $file.scalablyTyped
import $ivy.`com.disneystreaming.smithy4s::smithy4s-mill-codegen-plugin::0.17.6`
//import $ivy.`com.lihaoyi::mill-contrib-bloop:`
import $ivy.`com.github.vic::mill-dotenv:0.6.0`
import $ivy.`com.goyeau::mill-scalafix::0.2.11`
import $ivy.`com.github.lolgab::mill-crossplatform::0.2.2`

import $file.CustomZincWorkerModule

import com.goyeau.mill.scalafix.ScalafixModule
import com.github.lolgab.mill.crossplatform._

import coursier.maven.MavenRepository

import mill._
import mill.modules.Assembly._
import mill.util.Ctx

import mill.define.Target
import mill.define.Task
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import mill.dotenv._

import smithy4s.codegen.mill._

// Allows mill to resolve the "meta-build" behind a corporate proxy
// object CustomZincWorkerModule extends ZincWorkerModule with CoursierModule {

// }

object Config {
  def scalaVersion = scalablyTyped.Versions.scalaVersion
  def scalaJSVersion = scalablyTyped.Versions.scalaJSVersion
  def laminarVersion = "15.0.1"
  def circeVersion = "0.14.5"
  val smithy4sVersion = smithy4s.codegen.BuildInfo.version
  val http4sVersion = "0.23.18"
  val scribeVersion = "3.10.3"

  def sharedDependencies = Agg(
    ivy"io.github.quafadas::dedav4s::0.8.1",
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
    ivy"org.tpolecat::skunk-core:0.3.2",
    ivy"is.cir::ciris:2.4.0",
    ivy"io.chrisdavenport::mules:0.6.0",
    ivy"org.flywaydb:flyway-core:9.4.0",
    ivy"org.postgresql:postgresql:42.5.0"
  )

  def jsDependencies = Agg(
    ivy"""com.raquo::laminar::$laminarVersion""",
    ivy"""be.doeraene::web-components-ui5::1.10.0""",
    ivy"""com.raquo::waypoint::6.0.0""",
    ivy"org.scala-js::scalajs-dom::2.3.0",
    ivy"org.scala-js::scalajs-java-securerandom::1.0.0".withDottyCompat(scalaVersion),
    ivy"org.http4s::http4s-dom::0.2.8",
    ivy"org.http4s::http4s-client::${http4sVersion}",
    ivy"io.laminext::core::0.15.0"
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

  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:0.6.0")
}
trait CommonJS extends Common with ScalaJSModule {
  def scalaJSVersion = Config.scalaJSVersion
}

object shared extends CrossPlatform  {
  trait Shared extends CrossPlatformScalaModule with Common with Smithy4sModule with CommonBuildSettings {
    def smithy4sInputDir = T.source { millSourcePath / os.up / "smithy" }
    def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies
  }
  object jvm extends Shared {
    object test extends CrossPlatformSources with Tests with TestModule.Munit {
      def ivyDeps = Agg(
        ivy"org.scalameta::munit::1.0.0-M6"
      )
    }
  }
  object js extends Shared with CommonJS
}

object backend extends Common with DotEnvModule  { // with ScalafixModule
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

  // object test extends Tests with TestModule.Munit with DotEnvModule with CommonBuildSettings{
  //   def ivyDeps = Agg(
  //     ivy"org.scalameta::munit::1.0.0-M6",
  //     ivy"org.typelevel::munit-cats-effect::2.0.0-M3"
  //   )

  //   override def dotenvSources = T.sources { os.pwd / ".env-test" }
  // }
}

object frontend extends CommonJS {
  // def repositoriesTask = CustomZincWorkerModule.CustomZincWorkerModule.repositoriesTask
  def moduleKind = ModuleKind.ESModule
  def moduleSplitStyle = ModuleSplitStyle.SmallModulesFor(List("frontend"))

  def dev = T {
    public(fastLinkJS)()
  }
  def publicProd = T {
    public(fullLinkJS)()
  }
  def moduleDeps = Seq(
    shared.js,
    scalablyTyped.stModule
  ) // ++ super.moduleDeps // ++ Seq(scalablytyped.stModule)

  def ivyDeps = super.ivyDeps() ++ Config.jsDependencies
}

private def public(jsTask: Task[Report]): Task[Map[String, os.Path]] = T.task {
  val jsDir = jsTask().dest.path
  // println(s"jsDir: $jsDir")
  Map("@public" -> jsDir)
}
