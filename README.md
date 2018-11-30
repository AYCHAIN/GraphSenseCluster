# A library implementing cryptocurrency clustering algorithms

This folder contains several Scala implementations of algorithms/heuristics that are used for linking cryptocurrency transactions.


## Prerequistes

Make sure [sbt > 1.0][scala-sbt] is installed:

    sbt about

## Building and Packaging

Package the library

     sbt package

Publish the packed library into your local repository

     sbt publishLocal

## Usage

Add a reference to this package to the `build.sbt` file of your project by adding something along the line of

```
libraryDependencies += "at.ac.ait" %% "graphsense-clustering" % "0.3.3"
```

* Applying multi-input-heuristic for e.g., Bitcoin:
	* Import the stuff related to clustering with `import linking.common._`
	* Bring your data into the form `Iterator[Iterable[A]]`, where each `Iterable[A]` is some collection of items that should be grouped together. 
	* Call `Clustering.getClustersMutable(data)` or `Clustering.getClustersImmutable(data)`. The result is an `Iterator[Result[A]]`, where each `Result[A]` has the fields `id: A` and `cluster: A`.

### Example Jobs
In `./example_jobs/` some `.scala` files can be found which should show how to use the library.

Currently, the following things are included:
#### `BTC.scala`
For Bitcoin, the multiple input clustering heuristic is currently implemented. There are two implementations, one using mutable DS and one using immutable DS. Usually, the mutable implementation should be faster:
* `RandomDataCluster(num_sets: Int, num_addrs: Int, max_addrs: Int, both : Boolean)`: Generates `num_sets` random transactions with `2 to max_addrs` (sampled uniformly) inputs, where the inputs are sampled from `1 to num_addrs` (again uniformly). If `both` is set to true, it maps all integers to SHA256 addresses and also runs the clustering algorithm on this modified dataset. 
* `ClusterFile(filePath : String, both : Boolean)`: Expects a path to a text file where each line represents the set of inputs to a transaction (one or multiple integers, seperated by `,`). If the parameter `both` is set to true, not only the mutable but also the immutable clustering algorithm are ran and their runtimes and results are compared.
#### [`Spark-BTC.scala`](https://github.com/graphsense/graphsense-clustering/blob/master/example_jobs/Spark-BTC.scala)

Showcases, how this library would be used in a spark-job. Basically
```
import linking.common._
val data_iterator = dataSet.collect().iterator // collect and call iterator
val clusterDataSet = Clustering.getClustersMutable(data_iterator).toList.toDS()
clusterDataSet.show()
     +---+-------+
     | id|cluster|
     +---+-------+
     |  8|      7|
     |  2|      1|
     |  5|      1|
     |  4|      1|
     |  7|      7|
     |  1|      1|
     |  9|      7|
     |  3|      1|
     |  6|      1|
     +---+-------+

```
Algorithm runs locally, so make sure that your machine does not explode when calling `collect()` (or at any other point).

## Planned features

* common:
	* Address clustering (multi-input heuristic)
		* TODO: Add ['clustering safeguards'](http://bitfury.com/content/downloads/clustering_whitepaper.pdf)
* xmr:
	* 0-Mixin removal
* zec:
	* Round Trip Transaction in/out of shielded pool ([Quesnelle, 2017](https://arxiv.org/abs/1712.01210))



[scala-sbt]: http://www.scala-sbt.org