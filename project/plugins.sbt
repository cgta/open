resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

//addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.3")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.5")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

// !!!! Change version in Deps Too !!!!
//addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")

//addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")


//2014-11-05
//Commenting this out because it slows startup maybe I can add it as a local plugin under my .sbt folder
//addSbtPlugin("com.jamesward" %% "play-auto-refresh" % "0.0.8")


resolvers += Resolver.url("jetbrains-bintray", url("http://dl.bintray.com/jetbrains/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "0.1.1")
