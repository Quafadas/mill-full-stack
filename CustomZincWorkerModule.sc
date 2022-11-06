
import mill._, scalalib._

import coursier.maven.MavenRepository

// Run this to reimport the build. I need to do this fairly often when changing library versions etc
// ./mill --no-server mill.contrib.bloop.Bloop/install
// ./mill --no-server mill.contrib.Bloop/install

// Allows mill to resolve the "meta-build"
object CustomZincWorkerModule extends ZincWorkerModule with CoursierModule {

  override def repositoriesTask = T.task {    
    super.repositoriesTask() ++ Seq(
      MavenRepository("https://jitpack.io")
    )
  }

}

