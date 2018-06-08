# `linking` Library
This folder contains several scala implementations of algorithms/heuristics that are used for linking transactions of cryptocurrencies.
Work in progress.

## Usage

* Start `sbt` in the root directory of this repository
* Enter `package` to create a jar
* Enter `publishLocal` to use the library from other projects on the current machine
* Add a reference to this package to the `build.sbt` file of your project by adding something along the line of
```
libraryDependencies += "at.ac.ait" %% "linking" % "1.0"
```
* Import the stuff related to clustering with `import linking.common._`
* Bring your data into the form `Iterator[Iterable[A]]`, where each `Iterable[A]` is some collection of items that should be grouped together. 
* Call `Clustering.getClustersMutable(data)` or `Clustering.getClustersImmutable(data)`. The result is an `Iterator[Result[A]]`, where each `Result[A]` has the fields `id: A` and `cluster: A`. 


### Examples
In `./example_jobs/` some `.scala` files can be found which should show how to use the library.

Currently, the following things are included:
* `BTC.scala`: For Bitcoin, the multiple input clustering heuristic is currently implemented. There are two implementations, one using mutable DS and one using immutable DS. Usually, the mutable implementation should be faster. Each test-case has a boolean parameter `both`, which, if set to `true`, calls both implementations (may take considerably longer). The following test-cases exist:
	* `RandomDataCluster(num_sets: Int, num_addrs: Int, max_addrs: Int, both : Boolean)`: Generates `num_sets` random transactions with `2 to max_addrs` (sampled uniformly) inputs, where the inputs are sampled from `1 to num_addrs` (again uniformly).
	* `ClusterFile(filePath : String, both : Boolean)`: Expects a path to a text file where each line represents a transaction. Each line should contain integers, seperated by `,`, referring to the input addresses.


## Supported
* common:
	* Address clustering (multi-input heuristic)
		* TODO: Add ['clustering safeguards'](http://bitfury.com/content/downloads/clustering_whitepaper.pdf)
* xmr:
	* 0-Mixin removal
	* TODO: Fork-Analysis (currently done in pgsql as RT on ancient laptop ~10min)
* zec:
	* Round Trip Transaction in/out of shielded pool ([Quesnelle, 2017](https://arxiv.org/abs/1712.01210))

## Naming convention
Files are often named in a sensible way. If names start with a `_`, these files are not relevant/used currently.
Should be cleaned up at some point.
