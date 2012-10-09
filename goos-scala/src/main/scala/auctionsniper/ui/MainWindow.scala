package auctionsniper.ui

import javax.swing.{JScrollPane, JTable, JFrame}
import java.awt.BorderLayout

object MainWindow {
  val MAIN_WINDOW_NAME = "Auction Sniper Main"
  val SNIPERS_TABLE_NAME = "Sniper Table"
  val APPLICATION_TITLE = "Auction Sniper"
  val NEW_ITEM_ID_NAME = "item id"
  val JOIN_BUTTON_NAME = "Join Auction"
}

import MainWindow._

class MainWindow(val snipers: SnipersTableModel) extends JFrame(APPLICATION_TITLE) {

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

