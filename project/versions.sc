import mill._
import mill.scalalib._
import coursier.maven.MavenRepository
import mill.scalalib.bsp.ScalaMetalsSupport

val scala = "3.1.0"
val scalajs = "1.10.0"


// ./mill --no-server mill.contrib.Bloop/install

// Allows mill to resolve the "meta-build"
object CustomZincWorkerModule extends ZincWorkerModule with CoursierModule {
  def repositoriesTask = T.task {
    Seq(
        MavenRepository("https://packages.schroders.com/artifactory/maven/")
    ) ++ super.repositories
}
}

trait CommonBuildSettings extends ScalaModule with ScalaMetalsSupport {
  //def semanticDbVersion = "4.5.0"
  def repositoriesTask  = CustomZincWorkerModule.repositoriesTask
  def zincWorker        = CustomZincWorkerModule
  def scalaVersion      = scala
}