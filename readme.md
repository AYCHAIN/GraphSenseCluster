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

In `src/main/scala/Main.scala` all features currently supported by library are tested in a seperate function.
Until a better documentation exists, just use the examples from there.