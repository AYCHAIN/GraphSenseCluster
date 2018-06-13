import org.apache.spark.sql.{Dataset}
import linking.common._

// Example transaction inputs
val tx1 = Set(1,2,3)
val tx2 = Set(4,5,6)
val tx3 = Set(7,8,9)
val tx4 = Set(3,4)
val txs = List(tx1,tx2,tx3,tx4)

// Spark DataSet
val dataSet = txs.toDS()

// Get Iterator[Iterable[A]] from dataset
// there is a function toLocalIterator() for spark-DS but it returns a java iterator which is not compatible per default.
val data_iterator = dataSet.collect().iterator // first collect and then call iterator


// Clustering happens here. Result is an iterator, if you want to save results use .toList or similar
val clusterData = Clustering.getClustersMutable(data_iterator).toList
clusterData.foreach(println)

val clusterDS = clusterData.toDS()
clusterDS.show()