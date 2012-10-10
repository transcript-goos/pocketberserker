package auctionsniper.ui

import javax.swing._
import java.awt.{FlowLayout, BorderLayout}
import auctionsniper.{SniperPortfolio, UserRequestListener}
import auctionsniper.util.Announcer
import java.awt.event.{ActionEvent, ActionListener}
import java.text.NumberFormat

object MainWindow {
  val MAIN_WINDOW_NAME = "Auction Sniper Main"
  val SNIPERS_TABLE_NAME = "Sniper Table"
  val APPLICATION_TITLE = "Auction Sniper"
  val NEW_ITEM_ID_NAME = "item id"
  val JOIN_BUTTON_NAME = "Join Auction"
  val NEW_ITEM_STOP_PRICE_NAME = "stop price"
}

import MainWindow._

class MainWindow(val portfolio: SniperPortfolio) extends JFrame(APPLICATION_TITLE) {

  private val userRequests = Announcer.to[UserRequestListener]

  setName(MAIN_WINDOW_NAME)
  fillContentPane(makeSnipersTable(portfolio), makeControls())
  pack()
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setVisible(true)

  private def fillContentPane(snipersTable: JTable, controls: JPanel) {
    val contentPane = getContentPane
    contentPane.setLayout(new BorderLayout())

    contentPane.add(controls, BorderLayout.NORTH)
    contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER)
  }

  private def makeSnipersTable(portfolio: SniperPortfolio) = {
    val model = new SnipersTableModel()
    portfolio += model
    val snipersTable = new JTable(model)
    snipersTable.setName(SNIPERS_TABLE_NAME)
    snipersTable
  }

  private def makeControls() = {
    val controls = new JPanel(new FlowLayout())
    controls.add(new JLabel("item:"))

    val itemIdField = makeItemIdField
    controls.add(itemIdField)

    controls.add(new JLabel("Stop price:"))
    controls.add(makeStopPriceField)

    val joinAuctionButton = new JButton("Join Auction")
    joinAuctionButton.setName(JOIN_BUTTON_NAME)
    joinAuctionButton.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        userRequests.announce().joinAuction(itemIdField.getText)
      }
    })
    controls.add(joinAuctionButton)

    controls
  }

  def +=(listener: UserRequestListener) {
    userRequests += listener
  }

  private def makeItemIdField = {
    val itemIdField = new JTextField()
    itemIdField.setColumns(25)
    itemIdField.setName(NEW_ITEM_ID_NAME)
    itemIdField
  }

  private def makeStopPriceField = {
    val stopPriceField = new JFormattedTextField(NumberFormat.getIntegerInstance)
    stopPriceField.setColumns(7)
    stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME)
    stopPriceField
  }
}

