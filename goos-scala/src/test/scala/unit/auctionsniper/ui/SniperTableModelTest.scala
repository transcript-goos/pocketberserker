package unit.auctionsniper.ui

import org.specs2.mutable.{Before, Specification}
import org.specs2.mock.Mockito
import javax.swing.event.{TableModelEvent, TableModelListener}
import auctionsniper.ui.{Column, SnipersTableModel}
import auctionsniper.{SniperState, SniperSnapshot}
import org.hamcrest.Matchers
import org.specs2.matcher.DataTables
import auctionsniper.ui.Column.{SNIPER_STATE, LAST_BID, LAST_PRICE, ITEM_IDENTIFIER}

class SniperTableModelTest extends Specification {

  private val model = new SnipersTableModel()

  trait mock extends Mockito with Before {
    val listener = mock[TableModelListener]
    def before = model.addTableModelListener(listener)
  }

  "SniperTableModel" should {
    "has enough columns" in {
      model.getColumnCount must_== Column.values.length
    }

    "sets sniper values in columns" in new mock {
      model.sniperStateChanged(SniperSnapshot("item id", 555, 666, SniperState.BIDDING))

      there was one(listener).tableChanged(anArgThat(aRowChangedEvent))

      assertColumnEquals(Column.ITEM_IDENTIFIER, "item id")
      assertColumnEquals(Column.LAST_PRICE, 555)
      assertColumnEquals(Column.LAST_BID, 666)
      assertColumnEquals(Column.SNIPER_STATE, SnipersTableModel.textFor(SniperState.BIDDING))
    }

    "set columns headings" in new mock {
      foreach(Column.values) {
        (column: Column) => model.getColumnName(column.ordinal) must_== column.name
      }
    }
  }

  private def assertColumnEquals(column: Column, expected: Any) {
    val rowIndex = 0
    val columnIndex = column.ordinal
    model.getValueAt(rowIndex, columnIndex) must_== expected
  }

  def aRowChangedEvent =
    Matchers.samePropertyValuesAs(new TableModelEvent(model, 0))
}

