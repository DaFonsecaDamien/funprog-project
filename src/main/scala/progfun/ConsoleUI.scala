package progfun

import scala.io.StdIn

class ConsoleUI {

  def run(): Unit = {
    println("Bienvenue dans le logiciel FunProg afin d'automatiser le contrôle de tondeuses à gazon.")
    println("Veuillez entrer les données nécessaires pour lancer le programme.")
    println("1. Pour entrer un fichier de séquence")
    println("2. Pour quitter")
    val input = StdIn.readLine()
    input match {
      case "1" =>
        println("Entrer le chemin du fichier de séquence:")
        println("Exemple: C:\\Users\\user\\Desktop\\sequence.txt")
        val filename = StdIn.readLine()
        val parser = new InputParser()
        val exporter = new Export()
        val (pelouse, tondeuses) = parser.parseInputFile(filename)
        val tondeusesResult = tondeuses.map(t => (t, t.executeInstructions(pelouse, t)))
        println("Entrer le chemin du fichier de sortie sans l'extension:")
        println("Exemple: C:\\Users\\user\\Desktop\\resultat")
        val outputFile = StdIn.readLine()
        println("Entrer le format de sortie:")
        println("1. JSON")
        println("2. CSV")
        println("3. YAML")
        println("4. Les 3 formats")
        val outputFormat = StdIn.readLine()
        outputFormat match {
          case "1" => exporter.exportResultToJson(outputFile, pelouse, tondeusesResult)
          case "2" => exporter.exportResultToCsv(outputFile, tondeusesResult)
          case "3" => exporter.exportResultToYaml(outputFile, pelouse, tondeusesResult)
          case "4" =>
            exporter.exportResultToJson(outputFile, pelouse, tondeusesResult)
            exporter.exportResultToCsv(outputFile, tondeusesResult)
            exporter.exportResultToYaml(outputFile, pelouse, tondeusesResult)
          case _ => println("Format de sortie incorrect")
        }
      case "2" => println("Au revoir!")
      case _ => println("Entrée invalide")
    }
  }

}
