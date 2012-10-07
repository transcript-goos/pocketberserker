package end2end.auctionsniper

import org.hamcrest.CoreMatchers._

import com.objogate.wl.swing.driver.{JTableHeaderDriver, ComponentDriver, JTableDriver, JFrameDriver}
import com.objogate.wl.swing.gesture.GesturePerformer
import com.objogate.wl.swing.AWTEventQueueProber
import auctionsniper.ui.MainWindow
import com.objogate.wl.swing.matcher.{IterableComponentsMatcher, JLabelTextMatcher}
import javax.swing.table.JTableHeader

class AuctionSniperDriver(val timeoutMillis: Int)
  extends JFrameDriver(new GesturePerformer(),
    JFrameDriver.topLevelFrame(
      ComponentDriver.named(MainWindow.MAIN_WINDOW_NAME),
      ComponentDriver.showingOnScreen()),
    new AWTEventQueueProber(timeoutMillis, 100)) {

  def showsSniperStatus(statusText: String) {
    new JTableDriver(this).hasCell(JLabelTextMatcher.withLabelText(equalTo(statusText)))
  }

  def showsSniperStatus(itemId: String, lastPrice: Int, lastBid: Int, statusText: String) {
    val table = new JTableDriver(this)
    table.hasRow(
      IterableComponentsMatcher.matching(
        JLabelTextMatcher.withLabelText(itemId),
        JLabelTextMatcher.withLabelText(lastPrice.toString),
        JLabelTextMatcher.withLabelText(lastBid.toString),
        JLabelTextMatcher.withLabelText(statusText)))
  }

  def hasColumnTitles() {
    val headers = new JTableHeaderDriver(this, classOf[JTableHeader])
    headers.hasHeaders(IterableComponentsMatcher.matching(
      JLabelTextMatcher.withLabelText("Item"),
      JLabelTextMatcher.withLabelText("Last Price"),
      JLabelTextMatcher.withLabelText("Last Bid"),
      JLabelTextMatcher.withLabelText("State")
    ))
  }
}

