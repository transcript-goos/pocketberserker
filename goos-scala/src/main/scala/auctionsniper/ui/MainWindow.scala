package auctionsniper.ui

import javax.swing.{JScrollPane, JTable, JLabel, JFrame}
import javax.swing.border.LineBorder
import java.awt.{BorderLayout, Color}

object MainWindow {

  val STATUS_JOINING = "Joining"
  val STATUS_LOST = "Lost"
  val STATUS_BIDDING = "Bidding"
  val STATUS_WINNING = "Winning"
  val STATUS_WON = "Won"
  val MAIN_WINDOW_NAME = "Auction Sniper Main"
  val SNIPERS_TABLE_NAME = "Sniper Table"
}

class MainWindow extends JFrame("Auction Sniper") {

  import MainWindow._

  private val snipers = new SnipersTableModel()

  setName(MAIN_WINDOW_NAME)
  fillContentPane(makeSnipersTable())
  pack()
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setVisible(true)

  private def fillContentPane(snipersTable: JTable) {
    val contentPane = getContentPane
    contentPane.setLayout(new BorderLayout())

    contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER)
  }

  private def makeSnipersTable() = {
    val snipersTable = new JTable(snipers)
    snipersTable.setName(SNIPERS_TABLE_NAME)
    snipersTable
  }

  def showStatusText(statusText: String) {
    snipers.setStatusText(statusText)
  }
}

