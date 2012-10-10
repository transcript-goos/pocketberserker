package integration.auctionsniper.ui

import org.specs2.mutable.Specification
import auctionsniper.ui.MainWindow
import com.objogate.wl.swing.probe.ValueMatcherProbe
import org.hamcrest.Matchers
import auctionsniper.{SniperPortfolio, UserRequestListener}
import end2end.auctionsniper.AuctionSniperDriver
import org.specs2.mutable.After

class MainWindowTest extends Specification {

  private val portfolio = new SniperPortfolio
  private val mainWindow = new MainWindow(portfolio)

  "MainWindow" should {
    "make user request when join button clicked" in new tearDown {
      val buttonProbs = new ValueMatcherProbe[String](Matchers.equalTo("an item-id"), "join request")

      mainWindow += new UserRequestListener {
          def joinAuction(itemId: String) {
            buttonProbs.setReceivedValue(itemId)
          }
        }

      driver.startBiddingFor("an item-id", Int.MaxValue)
      driver.check(buttonProbs)
    }
  }

  trait tearDown extends After {
    val driver = new AuctionSniperDriver(100)
    def after = driver.dispose()
  }
}
