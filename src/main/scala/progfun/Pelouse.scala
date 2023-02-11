package progfun

case class Pelouse(x: Int, y: Int) {
  def isOutOfBound(x: Int, y: Int): Boolean = {
    x < 0 || x > this.x || y < 0 || y > this.y
  }
}
