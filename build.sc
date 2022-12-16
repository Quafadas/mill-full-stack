import $ivy.`com.disneystreaming.smithy4s::smithy4s-mill-codegen-plugin::0.16.2`
import $ivy.`com.lihaoyi::os-lib:0.8.0`
import $ivy.`com.lihaoyi::mill-contrib-bloop:`
import $ivy.`com.github.vic::mill-dotenv:0.6.0`
import $ivy.`com.goyeau::mill-scalafix::0.2.9`

import $file.CustomZincWorkerModule

import com.goyeau.mill.scalafix.ScalafixModule

import coursier.maven.MavenRepository

import mill._
import mill.modules.Assembly._
import mill.util.Ctx

import mill.define.Target
import mill.define.Task
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

//import mill.dotenv._

import smithy4s.codegen.mill._

// Allows mill to resolve the "meta-build"
object CustomZincWorkerModule extends ZincWorkerModule with CoursierModule {

}

object Config {
  def scalaVersion = "3.2.0"
  def scalaJSVersion = "1.12.0"
  def laminarVersion = "0.14.5"
  def circeVersion = "0.15.0-M1"
  val smithy4sVersion = smithy4s.codegen.BuildInfo.version
  val http4sVersion = "0.23.16"
  val scribeVersion = "3.10.3"

  def sharedDependencies = Agg(
    ivy"io.github.quafadas::dedav4s::0.8.1",
    ivy"com.disneystreaming.smithy4s::smithy4s-core::${smithy4sVersion}",
    ivy"com.disneystreaming.smithy4s::smithy4s-http4s::${smithy4sVersion}",
    ivy"com.outr::scribe::$scribeVersion",
    ivy"com.outr::scribe-cats::$scribeVersion",
    ivy"com.lihaoyi::upickle::2.0.0"
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
    ivy"""com.raquo::laminar::0.14.5""",
    ivy"""com.github.sherpal:LaminarSAPUI5Bindings:1.3.0-8f02a832""",
    ivy"""com.raquo::waypoint::0.5.0""",
    ivy"org.scala-js::scalajs-dom::2.3.0",
    ivy"org.scala-js:scalajs-java-securerandom_sjs1_2.13:1.0.0",
    ivy"org.http4s::http4s-dom::0.2.3",
    ivy"org.http4s::http4s-client::${http4sVersion}",
    ivy"io.laminext::core::0.14.4"
  )
}

trait CommonBuildSettings extends ScalaModule {
  // def semanticDbVersion = "4.5.0"
  def repositoriesTask = CustomZincWorkerModule.CustomZincWorkerModule.repositoriesTask
  def zincWorker = CustomZincWorkerModule.CustomZincWorkerModule
  def scalaVersion = Config.scalaVersion
}

trait Common extends ScalaModule with CommonBuildSettings with ScalafixModule {
  // def repositories = super.repositories ++ Seq(
  // MavenRepository("https://jitpack.io")
  // )
  def zincWorker = CustomZincWorkerModule.CustomZincWorkerModule

  def scalaVersion = Config.scalaVersion

  def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies

  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:0.6.0")
}

object shared extends Module {
  object jvm extends Common with Smithy4sModule with CommonBuildSettings {
    override def millSourcePath = super.millSourcePath / os.up
    def smithy4sInputDir        = T.source { super.millSourcePath / os.up / "smithy" }
    def ivyDeps                 = super.ivyDeps() ++ Config.sharedDependencies
    object test extends Tests with TestModule.Munit with CommonBuildSettings {

      def sources = T.sources { super.millSourcePath / os.up / "test" }
      // override def scalaJSVersion = Config.scalaJSVersion
      override def scalaVersion = Config.scalaVersion
      def ivyDeps = Agg(
        ivy"org.scalameta::munit::1.0.0-M6"
      )
    }
  }

  object js extends Common with Smithy4sModule with CommonBuildSettings with ScalaJSModule {
    override def millSourcePath = super.millSourcePath / os.up
    def smithy4sInputDir        = T.source { super.millSourcePath / os.up / "smithy" }
    def scalaJSVersion          = Config.scalaJSVersion
  }

}

object backend extends Common { // with ScalafixModule
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

object frontend extends Common with ScalaJSModule {
  def repositoriesTask = CustomZincWorkerModule.CustomZincWorkerModule.repositoriesTask
  def scalaJSVersion = Config.scalaJSVersion
  def moduleKind = ModuleKind.ESModule
  def moduleSplitStyle = ModuleSplitStyle.SmallModulesFor(
    List(
      "frontend"
    )
  )

  def publicDev = T {
    public(fastLinkJS)()
  }
  def publicProd = T {
    public(fullLinkJS)()
  }

  // override def generatedSources: T[Seq[PathRef]] = T {
  //    val (scalaOutput, _) = shared.smithy4sCodegen()
  //    scalaOutput +: super.generatedSources()
  // }

  // override def smithy4sInputDir: T[PathRef] = T.source {
  //   PathRef(shared.millSourcePath / "smithy")
  // }

  // def sources = T.sources(
  //   millSourcePath / "src",
  //   millSourcePath / os.up / "shared" / "src"
  // )

  def moduleDeps = Seq(
    shared.js
  ) // ++ super.moduleDeps // ++ Seq(scalablytyped.stModule)

  def ivyDeps = super.ivyDeps() ++ Config.jsDependencies
}

// Needed for the frontend publicDev / publicProd tasks...
case class Alias(find: String, replacement: os.Path)
object Alias {
  import upickle.default._
  implicit val rw: ReadWriter[Alias] = macroRW
}

private def public(jsTask: Task[Report]): Task[Seq[Alias]] = T.task {
  val jsDir = jsTask().dest.path
  // println(s"jsDir: $jsDir")
  Seq(Alias("@public", jsDir))
}
