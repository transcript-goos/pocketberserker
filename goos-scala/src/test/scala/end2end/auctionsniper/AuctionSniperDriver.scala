package end2end.auctionsniper

import org.hamcrest.CoreMatchers._

import com.objogate.wl.swing.driver.{ComponentDriver, JLabelDriver, JFrameDriver}
import com.objogate.wl.swing.gesture.GesturePerformer
import com.objogate.wl.swing.AWTEventQueueProber
import auctionsniper.ui.MainWindow

class AuctionSniperDriver(val timeoutMillis: Int)
  extends JFrameDriver(new GesturePerformer(),
    JFrameDriver.topLevelFrame(
      ComponentDriver.named(MainWindow.MAIN_WINDOW_NAME),
      ComponentDriver.showingOnScreen()),
    new AWTEventQueueProber(timeoutMillis, 100)) {

  def showsSniperStatus(statusText: String) {
    new JLabelDriver(this, ComponentDriver.named(MainWindow.SNIPER_STATUS_NAME)).hasText(equalTo(statusText))
  }
}
