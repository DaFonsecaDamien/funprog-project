package progfun

case class Tondeuse(x: Int, y: Int, orientation: Char, instructions: String) {

  def executeInstruction(pelouse: Pelouse, tondeuse: Tondeuse, instruction: Char): Tondeuse = {
    val (x, y, orientation) = (tondeuse.x, tondeuse.y, tondeuse.orientation)

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
        case _ => orientation
      }
      case 'D' => orientation match {
        case 'N' => 'E'
        case 'E' => 'S'
        case 'W' => 'N'
        case 'S' => 'W'
        case _ => orientation
      }
      case _ => orientation
    }

    if (pelouse.isOutOfBound(newX, newY)) {
      tondeuse
    } else {
      Tondeuse(newX, newY, newOrientation, tondeuse.instructions)
    }
  }

  def executeInstructions(pelouse: Pelouse, tondeuse: Tondeuse): Tondeuse = {
    tondeuse.instructions.foldLeft(tondeuse)((tondeuse, instruction) => executeInstruction(pelouse, tondeuse, instruction))
  }
}
