# A library implementing cryptocurrency clustering heuristics

This repository Scala implementation of the basic building blocks that are needed to group
multiple addresses into maximal subsets (clusters) that can be likely assigned to the same real-world actor.
They can be used standalone or integrated into the [GraphSense transformation pipeline][graphsense-transformation].


At the moment, this libarary provides the following heuristics:

- *Multiple-Input Clustering Heuristics*: the underlying intuition is that if two addresses (i.e. A and B)
are used as inputs in the same transaction while one of these addresses along with another address (i.e. B and C)
are used as inputs in another transaction, then the three addresses (A, B and C) must somehow be controlled by the same actor,
who conducted both transactions and therefore possesses the private keys corresponding to all three addresses.

However, despite the promising benefits of above heuristics, please note that there are certain types of
transactions (e.g. Coin-Join) could distort clustering results, unify entities that have no association in the real-world
and can lead to the formation of so called super-clusters.


## Prerequisites

Make sure [sbt >= 1.0][scala-sbt] is installed:

    sbt about

## Building and Packaging

Run the tests

	sbt test

Package the library

    sbt package

Publish the packed library into your local repository

    sbt publishLocal

## Usage

Add a reference to this package to the `build.sbt` file of your project by adding

    libraryDependencies += "at.ac.ait" %% "graphsense-clustering" % "0.4-SNAPSHOT"

Applying multi-input-heuristic for e.g., Bitcoin:

* Import the clustering module  `import at.ac.ait.clustering._`

* Bring your data into the form `Iterator[Iterable[A]]`, where each
  `Iterable[A]` is some collection of items that should be grouped together. 

* Call `Clustering.getClustersMutable(data)` or
  `Clustering.getClustersImmutable(data)`. The result is an
  `Iterator[Result[A]]`, where each `Result[A]` has the fields `id: A` and
  `cluster: A`.

[scala-sbt]: http://www.scala-sbt.org
[graphsense-transformation]: https://github.com/graphsense/graphsense-transformation
