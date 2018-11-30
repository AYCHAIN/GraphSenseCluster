package at.ac.ait.clustering.common

case class InOut(in : Int, out : Int) {
  def get(v : Int) : Int  = {
    if (v == 1) in
    else out
  }
}
