package auctionsniper.ui

import javax.swing._
import java.awt.{FlowLayout, BorderLayout}

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
  fillContentPane(makeSnipersTable(), makeControls())
  pack()
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setVisible(true)

  private def fillContentPane(snipersTable: JTable, controls: JPanel) {
    val contentPane = getContentPane
    contentPane.setLayout(new BorderLayout())

    contentPane.add(controls, BorderLayout.NORTH)
    contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER)
  }

  private def makeSnipersTable() = {
    val snipersTable = new JTable(snipers)
    snipersTable.setName(SNIPERS_TABLE_NAME)
    snipersTable
  }

  private def makeControls() = {
    val controls = new JPanel(new FlowLayout())
    val itemIdField = new JTextField()
    itemIdField.setColumns(25)
    itemIdField.setName(NEW_ITEM_ID_NAME)
    controls.add(itemIdField)

    val joinAuctionButton = new JButton("Join Auction")
    joinAuctionButton.setName(JOIN_BUTTON_NAME)
    controls.add(joinAuctionButton)

    controls
  }
}

