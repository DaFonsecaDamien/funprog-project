package progfun

import better.files._

class InputParser {
  case class DonneesIncorectesException(message: String) extends Exception(message)

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def parseInputFile(filename: String): (Pelouse, List[Tondeuse]) = {
    val lines = File(filename).lines.toList
    if (lines.length < 3 || lines.length % 2 == 0) {
      throw DonneesIncorectesException("Le nombre de lignes du fichier d'entrée est incorrect")
    } else {
      // Récupération des dimensions de la pelouse
      val pelouseSize = lines.headOption.map(_.split(" ").map(_.toInt)).getOrElse(Array())
      val pelouse = Pelouse(pelouseSize(0), pelouseSize(1))

      // Récupération des tondeuses
      val tondeuses = lines.drop(1).grouped(2).map { case List(coordinates, instructions) =>
        val Array(x, y, orientation) = coordinates.split(" ")
        Tondeuse(x.toInt, y.toInt, orientation.head, instructions)
      }.toList

      (pelouse, tondeuses)
    }
  }
}
