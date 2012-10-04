package auctionsniper

import javax.swing.SwingUtilities
import org.jivesoftware.smack.{Chat}

object Main {

  val STATUS_JOINING = "Joining"
  val STATUS_LOST = "Lost"

  def main(args: Array[String]) {
    val main = new Main()
  }
}

class Main {

  private var ui : Option[MainWindow] = None

  startUserInterface()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait(new Runnable() {
      def run() {
        ui = Some(new MainWindow())
      }
    })
  }
}
