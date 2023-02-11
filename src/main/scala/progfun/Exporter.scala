package progfun

import better.files.File
import play.api.libs.json.Json

class Exporter {

  def exportResultToJson(path: String, pelouse: Pelouse, tondeuses: List[(Tondeuse, Tondeuse)]): Unit = {
    val limite = Json.obj("x" -> pelouse.x, "y" -> pelouse.y)
    val tondeusesJson = tondeuses.map { case (tondeuseStart, tondeuseEnd) =>
      Json.obj(
        "debut" -> Json.obj(
          "point" -> Json.obj("x" -> tondeuseStart.x, "y" -> tondeuseStart.y),
          "direction" -> tondeuseStart.orientation.toString
        ),
        "instructions" -> tondeuseStart.instructions,
        "fin" -> Json.obj(
          "point" -> Json.obj("x" -> tondeuseEnd.x, "y" -> tondeuseEnd.y),
          "direction" -> tondeuseEnd.orientation.toString
        )
      )
    }
    val json = Json.obj("limite" -> limite, "tondeuses" -> tondeusesJson)
    val file = File(path+".json")
    val result = file.createIfNotExists().overwrite(Json.stringify(json))
    println(s"Le fichier de sortie en JSON a été créé: ${result.pathAsString}")
  }

  def exportResultToCsv(path: String, tondeuses: List[(Tondeuse, Tondeuse)]): Unit = {
    val file = File(path+".csv")
    val result = file.createIfNotExists().overwrite("numéro;début_x;début_y;début_direction;fin_x;fin_y;fin_direction;instructions\n")
    val lines = tondeuses.zipWithIndex.map { case ((tondeuseStart, tondeuseEnd), index) =>
      s"${(index + 1).toString};${tondeuseStart.x.toString};${tondeuseStart.y.toString};${tondeuseStart.orientation.toString};${tondeuseEnd.x.toString};${tondeuseEnd.y.toString};${tondeuseEnd.orientation.toString};${tondeuseStart.instructions}"
    }
    result.appendLines(lines.mkString("\n"))
    println(s"Le fichier de sortie en CSV a été créé: ${result.pathAsString}")
  }

  def exportResultToYaml(path: String, pelouse: Pelouse, tondeuses: List[(Tondeuse, Tondeuse)]): Unit = {
    val limite = s"""limite:\n  x: ${pelouse.x.toString}\n  y: ${pelouse.y.toString}"""
    val tondeusesYaml = tondeuses.map { case (tondeuseStart, tondeuseEnd) =>
      s"""  - debut:
         |      point:
         |        x: ${tondeuseStart.x.toString}
         |        y: ${tondeuseStart.y.toString}
         |      direction: ${tondeuseStart.orientation.toString}
         |    instructions: ${tondeuseStart.instructions.map(i => s"\n      - ${i.toString}").mkString}
         |    fin:
         |      point:
         |        x: ${tondeuseEnd.x.toString}
         |        y: ${tondeuseEnd.y.toString}
         |      direction: ${tondeuseEnd.orientation.toString}""".stripMargin
    }
    val yaml = s"""$limite\ntondeuses:\n${tondeusesYaml.mkString("\n")}"""
    val file = File(path+".yaml")
    val result = file.createIfNotExists().overwrite(yaml)
    println(s"Le fichier de sortie en YAML a été créé: ${result.pathAsString}")
  }
}
