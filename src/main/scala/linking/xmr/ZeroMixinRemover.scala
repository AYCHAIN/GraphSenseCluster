package linking.xmr
//import java.io.PrintWriter

import linking.common.InOut
import scala.collection.mutable
import scala.annotation.tailrec



case object DataPreprocessor{
  // Not really useful
  // def readCsvFile(spark: SparkSession, filePath: String): Array[InOut] = {
  //   import org.apache.spark.rdd.RDD
  //   import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
  //   import org.apache.spark.sql.{Dataset, Row, SparkSession}
  //   import spark.implicits._
  //   val schema = new StructType()
  //     .add(StructField("inid", IntegerType, false))
  //     .add(StructField("outid", IntegerType, false))
  //   spark.read
  //     .format("csv")
  //     .option("header", "true")
  //     .schema(schema)
  //     .load(filePath)
  //     .withColumnRenamed("inid", "in") // for implicit encoding as InOut
  //     .withColumnRenamed("outid", "out") // same here
  //     .as[InOut]
  //     .collect()
  // }

  import scala.io._
  def readCsvFile(bufferedSource : BufferedSource, header : Boolean = true): Array[InOut] = {
    // import scala.io._
    // val lineSource = Source.fromFile(filePath) // 
    val lines = bufferedSource.getLines
    if (header){
      lines.next() // skip header line
    }
    val dataInts = lines.map(_.split(",").map(_.trim).map(_.toInt))
    val dataIO = dataInts.map(a => InOut(a(0),a(1)))
    val data = Array(dataIO.toSeq: _*)
    data
  }

  def PreProcessData(data: Array[InOut]) = {
    // Read data from <wherever> and create in->out and out->in maps
    val in_outs = to_map(data, key = 1)
    val out_ins = to_map(data, key = 2).mapValues(_.toArray) // should reduce overhead considerably
    (in_outs, out_ins)
  }

  // Takes a Dataset of InOut (basically wrapper for tuple) and generates either:
  // * A map from in to Set(out) (if key == 1)
  // * A map from out to Set(in) (if key == 2)
  private def to_map(in_out: Array[InOut], key: Int): mutable.Map[Int, mutable.Set[Int]] = {
    val value = 3 - key // Set(key,value) == Set(1,2), this ensures this (assuming key = 1 or 2)
    mutable.Map() ++ in_out // row: output and inputs where it's ref'd (OWA)
      .groupBy(a => a.get(key))
      .mapValues(v => v.map(a => a.get(value)))
      .mapValues(v => mutable.Set(v.toSeq: _*))
    //.map(io => (io.get(key), mutable.Set(io.get(value))))
    //   .groupByKey(_._1)
    //   .reduceGroups((a, b) => (a._1, a._2 ++ b._2))
    //   .map(_._2) // datasets do not support reduceByKey, therefore do this instead
    // out_ins.collect().toMap
  }
}

case class ZeroMixinRemover(var in_outs: mutable.Map[Int, mutable.Set[Int]],
                            var out_ins: scala.collection.Map[Int, Array[Int]]) {
  def RemoveMixins() = {
    val numInputs = in_outs.size;
    println(s"#Inputs/#Outputs: ${numInputs}/${out_ins.size}")
    // Should be equal to number of rows in raw_in_out_data
    val refs = countReferences(in_outs)

    println("Start matching algorithm:")
    // First find an initial set of matchable inputs (those with only 1 output)
    val Match = mutable.ArrayBuffer(
      in_outs.filter(a => a._2.size == 1).map(a => (a._1, a._2.head)).toSeq: _*)

    // Pass it to algorithm
    matchingAlgorithm(Match, 0)
    
    val matched = in_outs
      .filter(_._2.size == 1) // if only one remaining input
      .map(a => (a._1, a._2.head)) // put (tx,input) in result set
    val refsRem = countReferences(in_outs)
    val mixinsRem = refsRem - numInputs
    val mixins = refs - numInputs
    println(f"References remaining: $refsRem/$refs (${100.0 * refsRem / refs}%.2f%%)")
    println(f"Chaff refs remaining: $mixinsRem/$mixins (${100.0 * mixinsRem / mixins}%.2f%%)")
    println(f"Inputs matched: ${matched.size}/${numInputs} (${100.0 * matched.size / numInputs}%.2f&%%)")
    (matched, in_outs)
  }

  private def countReferences(in_outs: mutable.Map[Int, mutable.Set[Int]]): Int = {
    in_outs.map(_._2.size).sum
  }

  private def matchingAlgorithm(Match: Seq[(Int, Int)],iterations: Int) {
    if (Match.nonEmpty) {
      var rem = 0
      val matchNext = mutable.ArrayBuffer[(Int, Int)]() // cache elements that only have one remaining
      println(s"Iteration: $iterations, matching ${Match.size} new inputs")
      for ((in, spent) <- Match) { // match transactions with only one input
        // Remove inputs used in matched transactions from other Txs
        out_ins(spent) // get set of inputs that ref output 'spent'
          .filter(in != _) // inputs except in
          .foreach(input => {
            val outs = in_outs(input)
            if (outs.remove(spent) && outs.size == 1) { // if single output match in next step
              matchNext.append((input, outs.head))
            }
          })
      }
      matchingAlgorithm(matchNext, iterations + 1)
    }
  }
}
