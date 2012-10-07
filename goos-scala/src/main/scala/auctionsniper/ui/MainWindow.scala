package auctionsniper.ui

import javax.swing.{JScrollPane, JTable, JLabel, JFrame}
import java.awt.BorderLayout
import auctionsniper.SniperSnapshot

object MainWindow {
  val MAIN_WINDOW_NAME = "Auction Sniper Main"
  val SNIPERS_TABLE_NAME = "Sniper Table"
}

class MainWindow(val snipers: SnipersTableModel) extends JFrame("Auction Sniper") {

  import MainWindow._

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
}

