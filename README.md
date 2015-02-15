CGTA/open 0.2.2
====

Open Source CGTA Libraries


The CGTA/open code is MIT licensed + I had to embed a bit of ScalaCheck code 
(which is BSD licensed) for generators / arbitrary that is used by the serialization 
library to generate random typesafe instances of objects on demand for unit testing.

# What's in it?
3 sub-projects at the moment

## CGTA/oscala 
low level wrappers around scala / scalajs that don't have any 3rd party dependencies adds 
lot of nice low level util type stuff.

## CGTA/serland
Serialization library recently updated to support scala-js that we have been using
for years in house on the jvm.

Here is a very simple example of use:

1. (optional) Mix cgta.serland.SerlandExports into your package object this will add 
toJsonCompact/toJsonPretty on any object with a SerClass and fromJson[A : SerClass] onto Strings

2. For a case class make it a memeber of the SerClass typeclass by adding the following to it's companion

```scala
    import cgta.serland.{SerClass, SerBuilder}

    object Point {implicit var ser = SerBuilder.forCase(this.apply _)}
    case class Point(x: Int, y: Int)
```

3. To/From Json example
 
```scala
    val str = Point(1,2).toJsonCompact // yields {"x":1,"y":2}
    val pnt = str.fromJson[Point] // yields Point(1,2)
    //Or without the sugar
    import cgta.serland.backends.{SerJsonOut, SerJsonIn}
    val str = SerJsonOut.toJsonCompact(x) // yields {"x":1,"y":2}
    val pnt = SerJsonIn.fromJsonString[Point](x) // yields Point(1,2)
```

4. Generate a random instance for unit testing

```scala
    val rnd = Point.ser.gen.sample.get
```

## CGTA/cenum
The enumeration type we built and use in house.
It features exhaustive match checking, and is supported
by the serland serialization library
```scala
    object Fruits extends CEnum {
      type EET = Fruit
      sealed abstract class Fruit(color: String, isSweet: Boolean) extends EnumElement
      case object Apple extends Fruit("red", true)
      case object Orange extends Fruit("orange", true)
      case object Pear extends Fruit("green", true)
      case object Banana extends Fruit("yellow", true)
      case object Tomato extends Fruit("red", false)
  
      override val elements = CEnum.getElements(this)
    }
```


# Rough around the edges

There isn't any documentation yet, I was hoping to write some up but have
been too busy with other things for work. I wanted to get this out there now
because it provides an example of 3 cross compiled scalajs / scalajvm libraries
of varying levels of complexity.

My suggestion for poking around is to just read the unit tests they should be
fairly thorough.

There are some dependencies that need to be removed, for example `serland` the
serialization library has a dependency on mongo because it can encode objects as 
BSON BasicDBObjects. I want to spin that off into a separate project, or just drop 
altogether since we are getting from mongo at work anyhow.

Since it's still very a much a work in progress I haven't published anything up to Sonatype
yet. It's more of a proof of concept that you can cross compile scala-js + scala-jvm fairly
easily. We are using the jvm side of the code for production software, and the scala-js
code has been used in a few experimental visualization for our traders so far.

# Intellij

Among other things, intellij seems to work fine for us in our cross-compile use-cases.

We don't use the symlink approach, even though we are all linux, we have been using an alternate
solution that seems to work:

We generate the intellij project files with a script under the projects bin folder called gen-idea
Also each project turns into 5 intellij modules: shared/jvm/sjs/jvm-test/sjs-test
The basic project structure end up looking like this:

![Intellij Screenshot](http://i.imgur.com/rRDxYKI.png)


# SBT builds

for sbt builds the shared project and aggregates all the jvm and sjs projects so you can run both sets
of tests at the same time.

## Other projects: CGTA/otest & CGTA/sbt-x-sjs-plugin 
We are also using [otest](https://github.com/cgta/otest) for our unit testing.
This plugin [sbt-x-sjs-plugin](https://github.com/cgta/sbt-x-sjs-plugin) helps with 
organizing cross builds.

# Try it out!

    git clone https://github.com/cgta/open
    sbt test
    

to generate the files for intellij simply run

    bin/gen-idea


# Use it as a dependency in ScalaJs or ScalaJvm

    "biz.cgta" %% "oscala-jvm" % "0.2.2"
    "biz.cgta" %% "cenum-jvm" % "0.2.2"
    "biz.cgta" %% "serland-jvm" % "0.2.2"

    "biz.cgta" %%% "oscala-sjs" % "0.2.2"
    "biz.cgta" %%% "cenum-sjs" % "0.2.2"
    "biz.cgta" %%% "serland-sjs" % "0.2.2"
