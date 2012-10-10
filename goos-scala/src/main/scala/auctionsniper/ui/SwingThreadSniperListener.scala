package auctionsniper.ui

import auctionsniper.{SniperSnapshot, SniperListener}
import javax.swing.SwingUtilities

class SwingThreadSniperListener(val delegate: SniperListener) extends SniperListener {

  def sniperStateChanged(snapshot: SniperSnapshot) {
    SwingUtilities.invokeLater(new Runnable {
      def run() {
        delegate.sniperStateChanged(snapshot)
      }
    })
  }
}

