# `linking` Library
This folder contains several scala implementations of algorithms/heuristics that are used for linking transactions of cryptocurrencies.
Work in progress.

## Naming convention
Files are often named in a sensible way. If names start with a `_`, these files are not relevant/used currently.
Should be cleaned up at some point.

## Supported
* common:
	* Address clustering (multi-input heuristic)
		* TODO: Add ['clustering safeguards'](http://bitfury.com/content/downloads/clustering_whitepaper.pdf)
* xmr:
	* 0-Mixin removal
	* TODO: Fork-Analysis (currently done in pgsql as RT on ancient laptop ~10min)
* zec:
	* Round Trip Transaction in/out of shielded pool ([Quesnelle, 2017](https://arxiv.org/abs/1712.01210))

## Usage

In `./example_jobs/` some `.scala` files can be found which should highlight the various features of the library. 
Currently, the following things are included:
* `BTC.scala`: For Bitcoin, the multiple input clustering heuristic is currently implemented. There are two implementations, one using mutable DS and one using immutable DS. Usually, the mutable implementation should be faster. Each test-case has a boolean parameter `both`, which, if set to `true`, calls both implementations (may take considerably longer). The following test-cases exist:
	* `RandomDataCluster(num_sets: Int, num_addrs: Int, max_addrs: Int, both : Boolean)`: Generates `num_sets` random transactions with `2 to max_addrs` (sampled uniformly) inputs, where the inputs are sampled from `1 to num_addrs` (again uniformly).
	* `ClusterFile(filePath : String, both : Boolean)`: Expects a path to a text file where each line represents a transaction. Each line should contain integers, seperated by `,`, referring to the input addresses.
	
Until a better documentation exists, just use the examples from there.
