package fr.esgi.al.funprog
import better.files._
import play.api.libs.json._
object Main extends App {
  case class DonneesIncorectesException(message: String) extends Exception(message)
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def parseInputFile(filename: String): ((Int, Int), List[(Int, Int, Char, String)]) = {
    val lines = File(filename).lines.toList
    if (lines.length < 3 || lines.length % 2 == 0) {
      throw DonneesIncorectesException("Le nombre de lignes du fichier d'entrée est incorrect")
    }else{
      // Récupération des dimensions de la pelouse
      val pelouseSize = lines.headOption.map(_.split(" ").map(_.toInt)).getOrElse(Array())

      // Récupération des tondeuses
      val tondeuses = lines.drop(1).grouped(2).map { case List(coordinates, instructions) =>
        val Array(x, y, orientation) = coordinates.split(" ")
        (x.toInt, y.toInt, orientation.head, instructions)
      }.toList

      ((pelouseSize(0), pelouseSize(1)), tondeuses)
    }
  }

  def executeInstruction(pelouseSize: (Int, Int), tondeuse: (Int, Int, Char), instruction: Char): (Int, Int, Char) = {
    val (x, y, orientation) = tondeuse

    val (newX, newY) = instruction match {
      case 'A' => orientation match {
        case 'N' => (x, y + 1)
        case 'E' => (x + 1, y)
        case 'W' => (x - 1, y)
        case 'S' => (x, y - 1)
      }
      case _ => (x, y)
    }

    val newOrientation = instruction match {
      case 'G' => orientation match {
        case 'N' => 'W'
        case 'E' => 'N'
        case 'W' => 'S'
        case 'S' => 'E'
      }
      case 'D' => orientation match {
        case 'N' => 'E'
        case 'E' => 'S'
        case 'W' => 'N'
        case 'S' => 'W'
      }
      case _ => orientation
    }

    if (newX < 0 || newX > pelouseSize._1 || newY < 0 || newY > pelouseSize._2) {
      // La tondeuse ne sort pas du terrain
      tondeuse
    } else {
      // La tondeuse reste sur le terrain
      (newX, newY, newOrientation)
    }
  }

  def executeInstructions(pelouseSize: (Int, Int), tondeuse: (Int, Int, Char, String)): (Int, Int, Char) = {
    val (x, y, orientation, instructions) = tondeuse
    instructions.foldLeft((x, y, orientation))((tondeuse, instruction) => executeInstruction(pelouseSize, tondeuse, instruction))
  }

  def runTondeuses(pelouseSize: (Int,Int),tondeuses: List[(Int, Int, Char, String)]): List[(Int, Int, Char)] = {
    tondeuses.map(tondeuse => executeInstructions(pelouseSize, tondeuse))
  }

  def exportResultToFile(path: String, pelouseSize: (Int, Int), tondeuses: List[((Int, Int, Char, String), (Int, Int, Char))]): Unit = {
    val limite = s"""{"x": ${pelouseSize._1.toString}, "y": ${pelouseSize._2.toString}}"""
    val tondeusesJson = tondeuses.map { case ((x, y, orientation, instructions), (x2, y2, orientation2)) =>
      s"""{
              "debut": {
                  "point": {"x": ${x.toString}, "y": ${y.toString}},
                  "direction": "${orientation.toString}"
              },
              "instructions": "$instructions",
              "fin": {
                  "point": {"x": ${x2.toString}, "y": ${y2.toString}},
                  "direction": "${orientation2.toString}"
              }
          }"""
    }
    val json =
      s"""{
          "limite": $limite,
          "tondeuses": [${tondeusesJson.mkString(",")}]
      }"""
    val file = File(path)
    val result = file.createIfNotExists().overwrite(json)
    println(s"Le fichier de sortie en JSON a été créé: ${result.pathAsString}")
  }

  def exportResultToJson(path: String, pelouseSize: (Int, Int), tondeuses: List[((Int, Int, Char, String), (Int, Int, Char))]): Unit = {
    val limite = Json.obj("x" -> pelouseSize._1, "y" -> pelouseSize._2)
    val tondeusesJson = tondeuses.map { case ((x, y, orientation, instructions), (x2, y2, orientation2)) =>
      Json.obj(
        "debut" -> Json.obj(
          "point" -> Json.obj("x" -> x, "y" -> y),
          "direction" -> orientation.toString
        ),
        "instructions" -> instructions,
        "fin" -> Json.obj(
          "point" -> Json.obj("x" -> x2, "y" -> y2),
          "direction" -> orientation2.toString
        )
      )
    }
    val json = Json.obj("limite" -> limite, "tondeuses" -> tondeusesJson)
    val file = File(path)
    val result = file.createIfNotExists().overwrite(Json.stringify(json))
    println(s"Le fichier de sortie en JSON a été créé: ${result.pathAsString}")
  }

  def exportResultToCsv(path: String, tondeuses: List[((Int, Int, Char, String), (Int, Int, Char))]): Unit = {
    val file = File(path)
    val result = file.createIfNotExists().overwrite("numéro;début_x;début_y;début_direction;fin_x;fin_y;fin_direction;instructions\n")
    val lines = tondeuses.zipWithIndex.map { case (((x, y, orientation, instructions), (x2, y2, orientation2)), index) =>
      s"${(index + 1).toString};${x.toString};${y.toString};${orientation.toString};${x2.toString};${y2.toString};${orientation2.toString};$instructions"
    }
    result.appendLines(lines.mkString("\n"))
    println(s"Le fichier de sortie en CSV a été créé: ${result.pathAsString}")
  }

  def exportResultToYaml(path: String, pelouseSize: (Int, Int), tondeuses: List[((Int, Int, Char, String), (Int, Int, Char))]): Unit = {
    val limite = s"""limite:\n  x: ${pelouseSize._1.toString}\n  y: ${pelouseSize._2.toString}"""
    val tondeusesYaml = tondeuses.map { case ((x, y, orientation, instructions), (x2, y2, orientation2)) =>
      s"""  - debut:
         |      point:
         |        x: ${x.toString}
         |        y: ${y.toString}
         |      direction: ${orientation.toString}
         |    instructions: ${instructions.map(i => s"\n      - ${i.toString}").mkString}
         |    fin:
         |      point:
         |        x: ${x2.toString}
         |        y: ${y2.toString}
         |      direction: ${orientation2.toString}""".stripMargin
    }
    val yaml = s"""$limite\ntondeuses:\n${tondeusesYaml.mkString("\n")}"""
    val file = File(path)
    val result = file.createIfNotExists().overwrite(yaml)
    println(s"Le fichier de sortie en YAML a été créé: ${result.pathAsString}")
  }

  val file = parseInputFile("E:\\ESGI\\funprojet\\funprog-al\\src\\main\\scala\\example\\test")
  exportResultToFile("E:\\ESGI\\funprojet\\funprog-al\\src\\main\\scala\\example\\resultFile-JSON.txt", file._1, file._2.zip(runTondeuses(file._1, file._2)))
  exportResultToCsv("E:\\ESGI\\funprojet\\funprog-al\\src\\main\\scala\\example\\resultCSV.csv", file._2.zip(runTondeuses(file._1, file._2)))
  exportResultToYaml("E:\\ESGI\\funprojet\\funprog-al\\src\\main\\scala\\example\\resultYAML.yaml", file._1, file._2.zip(runTondeuses(file._1, file._2)))
  exportResultToJson("E:\\ESGI\\funprojet\\funprog-al\\src\\main\\scala\\example\\resultJSON.json", file._1, file._2.zip(runTondeuses(file._1, file._2)))
  print("Done")
}