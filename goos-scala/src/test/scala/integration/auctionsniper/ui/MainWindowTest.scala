package integration.auctionsniper.ui

import org.specs2.mutable.Specification
import auctionsniper.ui.MainWindow
import com.objogate.wl.swing.probe.ValueMatcherProbe
import org.hamcrest.Matchers
import auctionsniper.{Item, SniperPortfolio, UserRequestListener}
import end2end.auctionsniper.AuctionSniperDriver
import org.specs2.mutable.After

class MainWindowTest extends Specification {

  private val portfolio = new SniperPortfolio
  private val mainWindow = new MainWindow(portfolio)

  "MainWindow" should {
    "make user request when join button clicked" in new tearDown {
      val itemProbs = new ValueMatcherProbe[Item](
        Matchers.equalTo(Item("an item-id", 789)), "join request")

      mainWindow += new UserRequestListener {
          def joinAuction(item: Item) {
            itemProbs.setReceivedValue(item)
          }
        }

      driver.startBiddingFor("an item-id", 789)
      driver.check(itemProbs)
    }
  }

  trait tearDown extends After {
    val driver = new AuctionSniperDriver(100)
    def after = driver.dispose()
  }
}
