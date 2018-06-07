package linking.common
import scala.annotation.tailrec
import scala.collection.mutable

// If common is imported, this object should be used to cluster addresses.
// There are two implementations, one uses mutable DS and the other does not.
// Most likely, the mutable implementation is faster, though this has to be tested in production first. 
// Usage:
//   val input_sets = Set(Set(1,2),Set(2,3), Set(4,5))
//   val result = Clustering.getClusters(input_sets.toIterator) OR
//   val result = Clustering.getClustersMutable(input_sets.toIterator)
//   result.foreach(println) =>
//        Result(1,1)
//        Result(2,1)
//        Result(3,1)
//        Result(4,4)
//        Result(5,4)
case object Clustering {
  def getClusters(tx_inputs: Iterator[Iterable[Int]]): Iterator[Result] = {
    val mapper = UnionFind(Map.empty)
    val am = doGrouping(mapper, tx_inputs)
    am.collect
  }

  def getClustersMutable(tx_inputs: Iterator[Iterable[Int]]): Iterator[Result] = {
    val mapper = UnionFindMutable(mutable.Map.empty)
    val am = doGroupingMutable(mapper, tx_inputs)
    am.collect
  }

  def getClustersVeryMutable(tx_inputs: Iterator[Iterable[Int]]): Iterator[Result] = {
    val mapper = UnionFindVeryMutable(mutable.Map.empty)
    val am = doGroupingVeryMutable(mapper, tx_inputs)
    am.collect
  }

  // Naming is maybe a little suboptimal, as the return value is not really
  // an iterator over the clusters (in the example above: [1,2,3],[4,5])
  // but an iteratover over the representatives of each node, i.e.
  // something similar to [1->1, 2->1, 3->1, 4->4, 5->4]


  // From here on private stuff.
  @tailrec
  private def doGrouping(am: UnionFind, inputs: Iterator[Iterable[Int]]): UnionFind = {
    if (inputs.hasNext) {
      val addresses = inputs.next()
      doGrouping(am.union(addresses), inputs)
    } else {
      am
    }
  }

  @tailrec
  private def doGroupingMutable(am: UnionFindMutable, tx_inputs: Iterator[Iterable[Int]]): UnionFindMutable = {
    if (tx_inputs.hasNext) {
      val addresses = tx_inputs.next()
      doGroupingMutable(am.union(addresses), tx_inputs)
    } else {
      am
    }
  }
  @tailrec
  private def doGroupingVeryMutable(am: UnionFindVeryMutable, tx_inputs: Iterator[Iterable[Int]]): UnionFindVeryMutable = {
    if (tx_inputs.hasNext) {
      val addresses = tx_inputs.next()
      doGroupingVeryMutable(am.union(addresses), tx_inputs)
    } else {
      am
    }
  }
}

// For each node fed into the algorithm, one of these is returned.
//   id:      Address of node
//   cluster: Address of representative of this node
case class Result(id: Int, cluster: Int) {
	override def toString() : String = {
		s"$id -> $cluster"
	}
}

// For each element in UF-DS an instance of Representative is stored that refers to the root of the cluster
//   address: The representative of the element. If the "owner" has the same address, he's the root of the cluster
//   height:  Union by rank/size is used to always add the smaller to the larger
//            This prevents degeneration to linear (instead of logarithmic) lists
private[common] case class Representative(address: Int, height: Byte){
  def apply(exclusive: Boolean) = {
    if (exclusive) Representative(this.address, (this.height+1).toByte)
    else this
  }
}
private[common] case class UnionFind(entries: Map[Int, Representative]) {
  @tailrec
  private def find(address: Int): Representative =
    if (entries.contains(address)) {
      val entry = entries(address)
      if (entry.address == address) entry // if root of cluster is found
      else find(entry.address) // look for root
    } else Representative(address, 0) // if not yet in DS, create new cluster with this element

  def union(addresses: Iterable[Int]): UnionFind = {
    val representatives = addresses.map(find)
    val (highestRepresentative, exclusive) =
      representatives.tail.foldLeft((representatives.head, true)) { (b, a) =>
        if (b._1.height > a.height) b
        else if (b._1.height == a.height) (b._1, false)
        else (a, true)
      }
    val setRepresentative = highestRepresentative(exclusive)
    // val newEntries = representatives.map(r => (find(r.address).address,setRepresentative)) // find not needed as all r are already representatives
    val newEntries = representatives.map(r => (r.address,setRepresentative))
    UnionFind(entries ++ newEntries)
  }

  def collect = {
    for (a <- entries.keysIterator)
      yield Result(a, find(a).address)
  }
}



private[common] case class UnionFindMutable(entries: mutable.Map[Int, Representative]) {
  //@tailrec
  private def find(address: Int): Representative =
    if (entries.contains(address)) {
      val entry = entries(address)
      if (entry.address == address) {
        entry // if root of cluster is found
      } else  {
        val root = find(entry.address) // look for root
        entries.put(address,root)
        root
      }
    } else Representative(address, 0) // if not yet in DS, create new cluster with this element

  def union(addresses: Iterable[Int]): UnionFindMutable = {
    val representatives = addresses.map(find)
    val (highestRepresentative, exclusive) =
      representatives.tail.foldLeft((representatives.head, true)) { (b, a) =>
        if (b._1.height > a.height) b
        else if (b._1.height == a.height) (b._1, false)
        else (a, true)
      }
      
    val height =
      if (exclusive) highestRepresentative.height
      else (highestRepresentative.height + 1).toByte
    val representative = Representative(highestRepresentative.address, height)
    representatives.foreach(r=>{entries.put(r.address,representative)})
    this
  }

  def collect = {
    for (a <- entries.keysIterator)
      yield Result(a, find(a).address)
  }
}

private[common] case class UnionFindVeryMutable(entries: mutable.Map[Int, Int]) {
  //@tailrec
  private def find(address: Int): Int =
    if (entries.contains(address)) {
      val entry = entries(address)
      if (entry == address) {
        entry // if root of cluster is found
      } else  {
        val root = find(entry) // look for root
        entries.put(address,root)
        root
      }
    } else address // if not yet in DS, create new cluster with this element
   

  def union(addresses: Iterable[Int]): UnionFindVeryMutable = {
    val representatives = addresses.map(find)
    val firstRep = representatives.head
    representatives.foreach(r=>{entries.put(r,firstRep)})
    this
  }

  def collect = {
    for (a <- entries.keysIterator)
      yield Result(a, find(a))
  }
}
