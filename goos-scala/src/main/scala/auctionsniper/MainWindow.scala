package auctionsniper

import javax.swing.{JLabel, JFrame}
import javax.swing.border.LineBorder
import java.awt.Color

object MainWindow {
  val MAIN_WINDOW_NAME = "Auction Sniper Main"
  val SNIPER_STATUS_NAME = "sniper status"

  private def createLabel(initialText: String) = {
    val result = new JLabel(initialText)
    result.setName(SNIPER_STATUS_NAME)
    result.setBorder(new LineBorder(Color.BLACK))
    result
  }
}

class MainWindow extends JFrame("Auction Sniper") {

  import Main._
  import MainWindow._

  private val sniperStatus = createLabel(STATUS_JOINING)

  setName(MAIN_WINDOW_NAME)
  add(sniperStatus)
  pack()
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setVisible(true)
}
