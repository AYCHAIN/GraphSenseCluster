import linking.xmr.{DataPreprocessor => DP, ZeroMixinRemover}

def fileWSL(n: String) = s"/mnt/c/linking/git/Linkability/xmr_data/csv/chronologic_rings_$n.csv"
def file(n: String) = s"c:/linking/git/Linkability/xmr_data/csv/chronologic_rings_$n.csv"


def LinkData(filePath : String) = {
	import scala.io._
	val bufferedSource = Source.fromFile(filePath)
	// Preprocessing uses Spark.
	// Instantiate DataPreprocessor with SparkSession (to import implicits._)
	// val dp = DataPreprocessor(spark)
	// And read file
	val data = DP.readCsvFile(bufferedSource)
	bufferedSource.close
	// And convert raw data to Maps of desired format, which is Map[Int, mutable.Set[Int]]
	val (i, o) = DP.PreProcessData(data)
	val ZMR = ZeroMixinRemover(i,o)
	// From here on spark is not needed any more. Mixins are removed, everyone is happy
	val reducedData = ZMR.RemoveMixins()
	print(reducedData)
}

def run() = LinkData("./sample_data/monero_rings.csv")
def run(x : String){
	LinkData(file(x))
}
