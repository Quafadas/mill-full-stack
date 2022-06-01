import $ivy.`com.disneystreaming.smithy4s::smithy4s-codegen:0.13.1`

import coursier.maven.MavenRepository
import mill._
import mill.api.PathRef
import mill.scalalib._
import os.Path
import smithy4s.codegen.{Codegen => Smithy4s}
import smithy4s.codegen.CodegenArgs

import scala.util._

trait Smithy4sModule extends ScalaModule with versions.CommonBuildSettings {

  val skipScala = false

  def openapiOutput = super.millSourcePath / "src" / "gen" / "openapi"

  /** Input directory for .smithy files */
  protected def smithy4sInputDir: T[PathRef] = T.source {
    PathRef(millSourcePath / "smithy")
  }

  def smithy4sCodegen: T[(PathRef, PathRef)] = T {
    val specFiles = if (os.exists(smithy4sInputDir().path)) {
      os.walk(smithy4sInputDir().path, skip = _.ext != "smithy")
    } else Seq.empty

/* 
    val scalaOutput   = T.ctx().dest / "scala" 
    val openapiOutput = T.ctx().dest / "openapi"
 */ 
    def scalaOutput   = super.millSourcePath / "src" / "gen" / "scala"
    //def openapiOutput = super.millSourcePath / "src" / "gen" / "openapi"

    val args = CodegenArgs(
      specs         = specFiles.toList,
      output        = scalaOutput,
      openapiOutput = openapiOutput,
      skipScala     = skipScala,
      skipOpenapi   = false,
      allowedNS     = None,
      repositories  = List.empty,
      dependencies  = List.empty,
      transformers  = List.empty,
      discoverModels = true,
      excludedNS = Some(Set.empty)
    )

    Smithy4s.processSpecs(args)
    (PathRef(scalaOutput), PathRef(openapiOutput))
  }

  override def generatedSources: T[Seq[PathRef]] = T {
    val (scalaOutput, _) = smithy4sCodegen()
    scalaOutput +: super.generatedSources()
  }

  // Considering the openapi output as localclasspath so that
  // its contents can be served by http servers
  override def localClasspath: T[Seq[PathRef]] = T {
    val (_, openapiOutput) = smithy4sCodegen()
    openapiOutput +: super.localClasspath()
  }

}