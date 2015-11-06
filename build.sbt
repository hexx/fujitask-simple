val commonSettings = Seq(
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint", "-language:_")
)

lazy val root = (project in file(".")).aggregate(fujitask, fujitaskScalikeJDBC, domain)

lazy val fujitask = project.settings(commonSettings:_*).settings(
  commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      "com.github.scalaprops" %% "scalaprops" % "0.1.15" % "test",
      "com.github.scalaprops" %% "scalaprops-scalazlaws" % "0.1.15" % "test"
    ),
    testFrameworks += new TestFramework("scalaprops.ScalapropsFramework")
  ):_*
)

lazy val fujitaskScalikeJDBC = (project in file("fujitask-scalikejdbc")).settings(
  commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scalikejdbc" %% "scalikejdbc" % "2.2.9"
    )
  ):_*
).dependsOn(fujitask)

lazy val domain = project.settings(
  commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scalikejdbc" %% "scalikejdbc-config"  % "2.2.9",
      "com.h2database" % "h2" % "1.4.190"
    ),
    initialCommands in console := """
    import scala.concurrent._
    import scala.concurrent.duration.Duration
    import domain.service._
    import domain.repository.scalikejdbc._
    def getValue[A](f: Future[A]): A = Await.result(f, Duration(300, "seconds"))
    DomainDB.setup()
    DomainDB.createTables()
    """
  ):_*
).dependsOn(fujitaskScalikeJDBC)
