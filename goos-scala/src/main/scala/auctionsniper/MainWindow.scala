package auctionsniper

import javax.swing.{JFrame}

object MainWindow {
  val MAIN_WINDOW_NAME = "Auction Sniper Main"
  val SNIPER_STATUS_NAME = "sniper status"
}

class MainWindow extends JFrame("Auction Sniper") {

  import MainWindow._

  setName(MAIN_WINDOW_NAME)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setVisible(true)
}

