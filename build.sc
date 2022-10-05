import $ivy.`com.disneystreaming.smithy4s::smithy4s-mill-codegen-plugin::0.16.2`
import $ivy.`com.lihaoyi::os-lib:0.8.0`
import $ivy.`com.lihaoyi::mill-contrib-bloop:`
import $ivy.`com.github.vic::mill-dotenv:0.6.0`
import $ivy.`com.goyeau::mill-scalafix::0.2.9`

import com.goyeau.mill.scalafix.ScalafixModule

import coursier.maven.MavenRepository

import mill._
import mill.modules.Assembly._
import mill.util.Ctx
import mill.dotenv._
import mill.define.Target
import mill.define.Task
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

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