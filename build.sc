import $file.project.scalablytyped
import $file.project.smithyModule
import $file.project.versions
import $ivy.`com.lihaoyi::os-lib:0.8.0`
import $ivy.`com.lihaoyi::mill-contrib-bloop:`

import mill._
import mill.define.Target
import mill.define.Task
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._
import mill.util.Ctx
import coursier.maven.MavenRepository
import smithyModule.Smithy4sModule


// Run this to reimport the build. I need to do this fairly often when changing library versions etc
// ./mill --import mill.contrib.bloop.Bloop/install

object Config {
  def scalaVersion = versions.scala
  def scalaJSVersion = versions.scalajs
  def laminarVersion = "0.14.2"
  def circeVersion = "0.15.0-M1"
  val smithy4sVersion = "0.13.2"
  val http4sVersion = "0.23.12"

  def sharedDependencies = Agg(
      ivy"org.http4s::http4s-ember-client::${http4sVersion}",
      ivy"io.github.quafadas::dedav4s::0.8.0",  
      ivy"com.disneystreaming.smithy4s::smithy4s-core:${smithy4sVersion}",
      ivy"com.disneystreaming.smithy4s::smithy4s-http4s:${smithy4sVersion}",
      ivy"com.disneystreaming.smithy4s::smithy4s-http4s-swagger:${smithy4sVersion}",
    ) ++ Agg(
      "io.circe::circe-core:",
      "io.circe::circe-generic:",
      "io.circe::circe-parser:"
    ).map(x => {
      ivy"$x$circeVersion"      
    } 
  )

  def jvmDependencies = Agg(
    ivy"org.http4s::http4s-ember-server::${http4sVersion}",
  )

  def jsDependencies = Agg(
    ivy"com.raquo::laminar_sjs1:$laminarVersion"
  )

}

trait Common extends ScalaModule with versions.CommonBuildSettings {  

  // waiting covenant release
  def repositories = super.repositories ++ Seq(
    // MavenRepository("https://jitpack.io")
  )

  def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies

  def sources = T.sources(
    millSourcePath / "src",
    millSourcePath / os.up / "shared" / "src"
  )

  object test extends Tests with TestModule.Munit {
    def ivyDeps = Agg(ivy"org.scalameta::munit:1.0.0-M4")
  }

}
object shared extends Common with Smithy4sModule {

} 

object backend extends Common with Smithy4sModule {
  def ivyDeps = super.ivyDeps() ++ Config.jvmDependencies

  override def smithy4sInputDir: T[PathRef] = T.source {
    PathRef(shared.millSourcePath / "smithy")
  }

  // Not 100% clear that this _should_ be necessary, but as far as I can tell.... it is! 
  override def smithy4sCodegen = T {
    val codegen = super.smithy4sCodegen()  
    os.copy.over(
      shared.openapiOutput,
      super.openapiOutput
    )
    codegen 
  }

  override val skipScala = true

  def mainClass = Some("example.Main")
}

object frontend extends ScalaJSModule with Common {
  def scalaJSVersion = Config.scalaJSVersion

  def moduleSplitStyle = ModuleSplitStyle.SmallModulesFor(List("frontend"))

  def moduleDeps = super.moduleDeps ++ Seq(scalablytyped.stModule)

  def scalablyTypedIgnoredLibs = Seq("std")

  def publicDev = T {
    public(fastLinkJS)()
  }
  def publicProd = T {
    public(fullLinkJS)()
  }

  def ivyDeps = super.ivyDeps() ++ Config.jsDependencies
}

// Needed for the frontend publicDev / publicProd tasks... 
case class Alias(find: String, replacement: os.Path)
object Alias {
  import upickle.default._
  implicit val rw: ReadWriter[Alias] = macroRW
}

private def public(jsTask: Task[Report]): Task[Seq[Alias]] =
  T.task {
    val jsDir = jsTask().dest.path
    Seq(Alias("@public", jsDir))
  }