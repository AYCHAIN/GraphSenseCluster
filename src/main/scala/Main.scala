//package main.scala

// import org.apache.spark.sql.{Dataset, Row, SparkSession}

// import scala.collection.SortedMap

object Main extends App {
  def Test_Clustering_Algorithm() {
    println("Testing Address Clustering:")

    // Some Testdata
    val input_sets = Set(
      Set(1, 2, 3),
      Set(4, 5, 6),
      Set(7, 8, 9),
      Set(3, 4)).toIterator
    // .map(_.toIterator)
    // .toIterator
    println(" " + input_sets)

    // Clustering:
    import linking.common._
    val result_iterator = Clustering.getClustersImmutable(input_sets)

    // Output processing
    val representatives = result_iterator.toList
    representatives.foreach(println)
    val clusters = representatives
      .groupBy(_.cluster) // group by cluster identifier
      .mapValues(_.map(_.id)) // map from  Map[cluster -> List[Result(id,cluster)]] to Map[cluster -> List[id]]
    println(clusters)
  }

  def Test_Zero_Mixin_Removal() {
    import linking.xmr.{DataPreprocessor, ZeroMixinRemover}
    import scala.io._
    import linking.common.InOut

    // Test data file paths -
    def fileWSL(n: String) = s"/mnt/c/linking/git/Linkability/xmr_data/csv/chronologic_rings_$n.csv"
    def file(n: String) = s"c:/linking/git/Linkability/xmr_data/csv/chronologic_rings_$n.csv"



    // Preprocessing uses Spark. //! DEPRECATED
    // val spark = org.apache.spark.sql.SparkSession.builder
    //   .appName("Mixin Remover Preprocessing")
    //   .getOrCreate
    // Instantiate DataPreprocessor with SparkSession (to import implicits._)
    // val dp = DataPreprocessor.readCsvFile(spark,filePath)
    // And read file
    // val data = dp.readCsvFile(filePath)
    // And convert raw data to Maps of desired format, which is Map[Int, mutable.Set[Int]]

    val filePath = file("1E6")
    val lineSource = Source.fromFile(filePath)
    val lines = lineSource.getLines
    lines.next()
    val dataInts = lines.map(_.split(",").map(_.trim).map(_.toInt))
    val dataIO = dataInts.map(a => InOut(a(0),a(1)))
    val data = Array(dataIO.toSeq: _*)
    lineSource.close

    val (i, o) = DataPreprocessor.PreProcessData(data)
    val reducedData = ZeroMixinRemover(i,o).RemoveMixins()
  }

  def main() {
    println("Hello, AIT!")
    Test_Clustering_Algorithm()
    //Test_Zero_Mixin_Removal()
  }

  main()
}
