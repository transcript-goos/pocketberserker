package end2end.auctionsniper

import org.hamcrest.CoreMatchers._

import com.objogate.wl.swing.driver.{ComponentDriver, JTableDriver, JFrameDriver}
import com.objogate.wl.swing.gesture.GesturePerformer
import com.objogate.wl.swing.AWTEventQueueProber
import auctionsniper.ui.MainWindow
import com.objogate.wl.swing.matcher.{IterableComponentsMatcher, JLabelTextMatcher}

class AuctionSniperDriver(val timeoutMillis: Int)
  extends JFrameDriver(new GesturePerformer(),
    JFrameDriver.topLevelFrame(
      ComponentDriver.named(MainWindow.MAIN_WINDOW_NAME),
      ComponentDriver.showingOnScreen()),
    new AWTEventQueueProber(timeoutMillis, 100)) {

  def showsSniperStatus(statusText: String) {
    new JTableDriver(this).hasCell(JLabelTextMatcher.withLabelText(equalTo(statusText)))
  }

  implicit def showsSniperStatus(itemId: String, lastPrice: Int, lastBid: Int, statusText: String) {
    val table = new JTableDriver(this)
    table.hasRow(
      IterableComponentsMatcher.matching(
        JLabelTextMatcher.withLabelText(itemId),
        JLabelTextMatcher.withLabelText(lastPrice.toString()),
        JLabelTextMatcher.withLabelText(lastBid.toString()),
        JLabelTextMatcher.withLabelText(statusText)))
  }
}
