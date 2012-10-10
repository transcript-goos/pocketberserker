package unit.auctionsniper.ui

import org.specs2.mutable.{Before, Specification}
import org.specs2.mock.Mockito
import javax.swing.event.{TableModelEvent, TableModelListener}
import auctionsniper.ui.{Column, SnipersTableModel}
import auctionsniper.{AuctionSniper, Auction, SniperSnapshot}
import org.hamcrest.Matchers

class SnipersTableModelTest extends Specification {
  isolated

  private val model = new SnipersTableModel()

  trait mock extends Mockito with Before {
    val listener = mock[TableModelListener]
    val auction = mock[Auction]
    val sniper = new AuctionSniper("item-id", auction)
    val anotherSniper = new AuctionSniper("another-item-id", auction)
    def before = model.addTableModelListener(listener)
  }

  "SniperTableModel" should {
    "have enough columns" in {
      model.getColumnCount must_== Column.values.length
    }

    "set sniper values in columns" in new mock {

      val bidding = sniper.getSnapshot.bidding(555, 666)

      model += sniper
      model.sniperStateChanged(bidding)

      assertRowMatchesSnapshot(0, bidding)

      got {
        atLeast(0)(listener).tableChanged(anArgThat(anyInsertionEvent))
        one(listener).tableChanged(anArgThat(aChangeInRow(0)))
      }
    }

    "set columns headings" in new mock {
      foreach(Column.values) {
        (column: Column) => model.getColumnName(column.ordinal) must_== column.name
      }
    }

    "notify listeners when adding a sniper" in new mock {

      model.getRowCount must_== 0

      model += sniper

      model.getRowCount must_== 1
      assertRowMatchesSnapshot(0, sniper.getSnapshot)

      there was one(listener).tableChanged(anArgThat(anInsertionAtRow(0)))
    }

    "hold snipers in addition order" in new mock {
      model += sniper
      model += anotherSniper

      cellValue(0, Column.ITEM_IDENTIFIER) must_== "item-id"
      cellValue(1, Column.ITEM_IDENTIFIER) must_== "another-item-id"
    }
  }

  private def assertRowMatchesSnapshot(row: Int, snapshot: SniperSnapshot) {
    cellValue(row, Column.ITEM_IDENTIFIER) must_== snapshot.itemId
    cellValue(row, Column.LAST_PRICE) must_== snapshot.lastPrice
    cellValue(row, Column.LAST_BID) must_== snapshot.lastBid
    cellValue(row, Column.SNIPER_STATE) must_== SnipersTableModel.textFor(snapshot.state)
  }

  private def cellValue(rowIndex: Int, column: Column) =
    model.getValueAt(rowIndex, column.ordinal)

  private def anyInsertionEvent =
    Matchers.hasProperty("type", Matchers.equalTo(TableModelEvent.INSERT))

  private def anInsertionAtRow(row: Int) =
    Matchers.samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT))

  private def aChangeInRow(row: Int) =
    Matchers.samePropertyValuesAs(new TableModelEvent(model, row))
}

