package unit.auctionsniper.xmpp

import org.specs2.specification.AfterExample
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import java.util.logging.{LogManager, Logger}
import auctionsniper.xmpp.LoggingXMPPFailureReporter

class LoggingXMPPFailureReporterTest extends Specification with AfterExample with Mockito {

  private val logger = mock[Logger]
  private val reporter = new LoggingXMPPFailureReporter(logger)

  def after = LogManager.getLogManager.reset()

  "LoggingXMPPFailureReporter" should {
    "write message translation failure to log" in {
      reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"))

      there was one(logger).severe("<auction id> "
          + "Could not translate message \"bad message\" "
          + "because \"java.lang.Exception: bad\""
          )
    }
  }
}
