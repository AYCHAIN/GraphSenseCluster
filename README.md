# A library implementing cryptocurrency clustering algorithms

This repository contains several Scala implementations of algorithms/heuristics
that are used for linking cryptocurrency transactions.

## Prerequisites

Make sure [sbt >= 1.0][scala-sbt] is installed:

    sbt about

## Building and Packaging

Package the library

    sbt package

Publish the packed library into your local repository

    sbt publishLocal

## Usage

Add a reference to this package to the `build.sbt` file of your project by adding

    libraryDependencies += "at.ac.ait" %% "graphsense-clustering" % "0.3.3"

Applying multi-input-heuristic for e.g., Bitcoin:

* Import the stuff related to clustering with `import at.ac.ait.clustering.common._`

* Bring your data into the form `Iterator[Iterable[A]]`, where each
  `Iterable[A]` is some collection of items that should be grouped together. 

* Call `Clustering.getClustersMutable(data)` or
  `Clustering.getClustersImmutable(data)`. The result is an
  `Iterator[Result[A]]`, where each `Result[A]` has the fields `id: A` and
  `cluster: A`.

[scala-sbt]: http://www.scala-sbt.org
