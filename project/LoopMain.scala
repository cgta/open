import sbt._
import sbt.Keys._
import spray.revolver.RevolverPlugin.Revolver

object LoopMain {
  lazy val loopMain = inputKey[spray.revolver.AppProcess]("Call ~loopMain a.b.Class to make sbt revolve a main") := {
    import Revolver._
    import spray.revolver.Actions._
    val main :: args = sbt.complete.Parsers.spaceDelimited("<arg>").parsed.toList
    //DREAM Someday properly parse the remaining options using
    //startArgsParser from sbt.Revolver so that the user can set jvm options as well
    val dashes = "---"
    var seenDashes = false
    val (startArgs, jvmArgs) = args.partition { arg => seenDashes = seenDashes || arg == dashes; !seenDashes}
    restartApp(
      streams.value,
      reLogTag.value,
      thisProjectRef.value,
      reForkOptions.value,
      Some(main),
      (fullClasspath in reStart).value,
      reStartArgs.value,
      ExtraCmdLineOptions(jvmArgs = jvmArgs.filterNot(_ == dashes), startArgs = startArgs))
  }
}

