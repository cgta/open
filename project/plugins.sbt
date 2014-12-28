addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

// !!!! Change version in Deps Too !!!!
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.4")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.3")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.4")

addSbtPlugin("org.scala-lang.modules.scalajs" % "scalajs-sbt-plugin" % "0.5.6")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

//2014-11-05
//Commenting this out because it slows startup maybe I can add it as a local plugin under my .sbt folder
//addSbtPlugin("com.jamesward" %% "play-auto-refresh" % "0.0.8")