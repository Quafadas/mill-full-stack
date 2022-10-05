import $ivy.`com.disneystreaming.smithy4s::smithy4s-mill-codegen-plugin::0.16.2`

import $ivy.`com.lihaoyi::mill-contrib-bloop:`
import smithy4s.codegen.mill._

// Run this to reimport the build. I need to do this fairly often when changing library versions etc
// ./mill --no-server mill.contrib.bloop.Bloop/install
// ./mill --no-server mill.contrib.Bloop/install


object Config {
  def scalaVersion = "3.2.0"

  val smithy4sVersion = smithy4s.codegen.BuildInfo.version


  def sharedDependencies = Agg(            
      ivy"com.disneystreaming.smithy4s::smithy4s-core::${smithy4sVersion}",
      ivy"com.disneystreaming.smithy4s::smithy4s-http4s::${smithy4sVersion}",
  )


}

object shared extends Smithy4sModule {
  def scalaVersion      = Config.scalaVersion
  def scalaJSVersion = Config.scalaJSVersion
  def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies


} 