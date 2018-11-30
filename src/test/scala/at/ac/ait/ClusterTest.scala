package at.ac.ait

import at.ac.ait.clustering.common._
import org.scalatest.FlatSpec
import org.scalatest._

class StackSpec extends FlatSpec {
  "Each input ID" should "be in in output and thus have some representative" in {
  val inputs = List(List(1),List(2),List(3),List(4),List(4,5))
  val results = Clustering.getClustersMutable(inputs.toIterator).toList
  val (s1,s2) = (results.map(_.id).toSet, inputs.flatten.toSet)
  assert((s1 diff s2.toSet).size == 0) // No difference between results
  assert((s2 diff s1.toSet).size == 0) // No difference between results

  }
  "Merging 123, 456, 789 and 34" should "result in two Clusters of size 6 and 3" in {
    val input_sets = Set(Set(1, 2, 3),Set(4, 5, 6),Set(7, 8, 9),Set(3, 4))
    // Clustering:
    val result_iterator = Clustering.getClustersImmutable(input_sets.toIterator)
    // Output processing
    val representatives = result_iterator.toList
    // representatives.foreach(println)
    val clusters = representatives
      .groupBy(_.cluster) // group by cluster identifier
      .mapValues(_.map(_.id)) // map from  Map[cluster -> List[Result(id,cluster)]] to Map[cluster -> List[id]]
      .mapValues(_.size).values.toList
    assert(clusters.contains(6))
    assert(clusters.contains(3))
  }

  "Both algorithms" should "produce identical results and the mutable should be faster" in {
    // Timer
    def time[R](msg : String = "Elapsed")(block: => R): (Long,R) = {
      val t0 = System.currentTimeMillis()
      val result = block
      val t1 = System.currentTimeMillis()
      if (t1-t0 > 10000)println(msg  + " time: "+ (t1 - t0) / 1000 + "s")
      else println(msg + " time: " + (t1-t0)+"ms")
      (t1-t0,result)
    }
    // Data generation
    def GenerateTestData(num_sets: Int, num_addrs: Int, max_addrs: Int) = {
      val r = scala.util.Random
      val more_sets = (1 to num_sets) // 
        .map(_ => 1 to (2 + r.nextInt(max_addrs -2))) // number of elements 
        .map(x => x.map(_ => r.nextInt(num_addrs)).toSet).toSet // map to addrs
      more_sets
    }
    
    val TESTSIZE = 1E4.toInt
    val data = GenerateTestData(TESTSIZE,TESTSIZE,5)

    val (t_mutable, result_mutable) = time("mutable"){
      val result_iterator = Clustering.getClustersMutable(data.toIterator)
      val clusters = result_iterator.toList
      .groupBy(_.cluster)
      .mapValues(_.map(_.id).sorted) 
      clusters.values
    }
    val (t_immutable, result_immutable) = time("immutable"){
      val result_iterator = Clustering.getClustersImmutable(data.toIterator)
      val clusters = result_iterator.toList
      .groupBy(_.cluster)
      .mapValues(_.map(_.id).sorted)
      clusters.values
    }
    val (s1,s2) = (result_immutable.toSet,result_mutable.toSet)
    assert((s1 diff s2.toSet).size == 0) // No difference between results
    assert((s2 diff s1.toSet).size == 0) // No difference between results
    assert(t_mutable < t_immutable) // mutable is faster
  }
}