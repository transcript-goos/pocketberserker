package unit.auctionsniper.ui

import org.specs2.mutable.{Before, Specification}
import org.specs2.mock.Mockito
import javax.swing.event.{TableModelEvent, TableModelListener}
import auctionsniper.ui.{Column, SnipersTableModel}
import auctionsniper.SniperSnapshot
import org.hamcrest.Matchers

class SnipersTableModelTest extends Specification {
  isolated

  private val model = new SnipersTableModel()

  trait mock extends Mockito with Before {
    val listener = mock[TableModelListener]
    def before = model.addTableModelListener(listener)
  }

  "SniperTableModel" should {
    "have enough columns" in {
      model.getColumnCount must_== Column.values.length
    }

    "set sniper values in columns" in new mock {

      val joining = SniperSnapshot.joining("item-id")
      val bidding = joining.bidding(555, 666)

      model.addSniper(joining)
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

      val joining = SniperSnapshot.joining("item123")

      model.getRowCount must_== 0

      model.addSniper(joining)

      model.getRowCount must_== 1
      assertRowMatchesSnapshot(0, joining)

      there was one(listener).tableChanged(anArgThat(anInsertionAtRow(0)))
    }

    "hold snipers in addition order" in new mock {
      model.addSniper(SniperSnapshot.joining("item 0"))
      model.addSniper(SniperSnapshot.joining("item 1"))

      cellValue(0, Column.ITEM_IDENTIFIER) must_== "item 0"
      cellValue(1, Column.ITEM_IDENTIFIER) must_== "item 1"
    }
  }

  private def assertColumnEquals(column: Column, expected: Any) {
    val rowIndex = 0
    val columnIndex = column.ordinal
    model.getValueAt(rowIndex, columnIndex) must_== expected
  }

  private def aRowChangedEvent =
    Matchers.samePropertyValuesAs(new TableModelEvent(model, 0))

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

