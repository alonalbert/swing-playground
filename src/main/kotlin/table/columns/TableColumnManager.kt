package table.columns

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractButton
import javax.swing.JCheckBoxMenuItem
import javax.swing.JPopupMenu
import javax.swing.JTable
import javax.swing.event.ChangeEvent
import javax.swing.event.ListSelectionEvent
import javax.swing.event.TableColumnModelEvent
import javax.swing.event.TableColumnModelListener
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel

/**
 * The TableColumnManager can be used to manage TableColumns. It will give the
 * user the ability to hide columns and then reshow them in their last viewed
 * position. This functionality is supported by a popup menu added to the
 * table header of the table. The TableColumnModel is still used to control
 * the view for the table. The manager will invoke the appropriate methods
 * of the TableColumnModel to hide/show columns as required.
 *
 */
@Suppress("unused")
class TableColumnManager(private val table: JTable) :
    ActionListener, TableColumnModelListener {
  private val tcm: TableColumnModel get() = table.columnModel
  private val allColumns = tcm.columns.toList().toMutableList()

  init {
    table.tableHeader.addMouseListener(object : MouseAdapter() {
      override fun mousePressed(e: MouseEvent) {
        showColumnSelectionPopup(e)
      }

      override fun mouseReleased(e: MouseEvent) {
        showColumnSelectionPopup(e)
      }
    })
  }

  /**
   * Hide a column from view in the table.
   *
   * @param  columnName   the column name of the column to be removed
   */
  private fun hideColumn(columnName: String) {
    for (i in 0 until tcm.columnCount) {
      val column = tcm.getColumn(i)
      if (columnName == column.headerValue) {
        hideColumn(column)
        break
      }
    }
  }

  /**
   * Hide a column from view in the table.
   *
   * @param  column  the TableColumn to be removed from the
   * TableColumnModel of the table
   */
  private fun hideColumn(column: TableColumn) {
    if (tcm.columnCount == 1) return

    //  Ignore changes to the TableColumnModel made by the TableColumnManager
    tcm.removeColumnModelListener(this)
    tcm.removeColumn(column)
    tcm.addColumnModelListener(this)
  }

  /**
   * Show a hidden column in the table.
   *
   * @param  columnName   the column name from the TableModel
   * of the column to be added
   */
  private fun showColumn(columnName: Any) {
    for (column in allColumns) {
      if (column.headerValue == columnName) {
        showColumn(column)
        break
      }
    }
  }

  /**
   * Show a hidden column in the table. The column will be positioned
   * at its proper place in the view of the table.
   *
   * @param  column   the TableColumn to be shown.
   */
  private fun showColumn(column: TableColumn) {
    //  Ignore changes to the TableColumnModel made by the TableColumnManager
    tcm.removeColumnModelListener(this)

    //  Add the column to the end of the table
    tcm.addColumn(column)

    //  Move the column to its position before it was hidden.
    //  (Multiple columns may be hidden, so we need to find the first
    //  visible column before this column so the column can be moved
    //  to the appropriate position)
    val position = allColumns.indexOf(column)
    val from = tcm.columnCount - 1
    var to = 0
    for (i in position - 1 downTo -1 + 1) {
      try {
        val visibleColumn = allColumns[i]
        to = tcm.getColumnIndex(visibleColumn.headerValue) + 1
        break
      } catch (_: IllegalArgumentException) {
      }
    }
    tcm.moveColumn(from, to)
    tcm.addColumnModelListener(this)
  }

  private fun showColumnSelectionPopup(e: MouseEvent) {
    if (e.isPopupTrigger) {
      val header = table.tableHeader
      val column = header.columnAtPoint(e.getPoint())
      val headerValue = tcm.getColumn(column).headerValue
      val columnCount = tcm.columnCount
      val popup = JPopupMenu()

      //  Create a menu item for all columns managed by the table column
      //  manager, checking to see if the column is shown or hidden.
      for (tableColumn in allColumns) {
        val value = tableColumn.headerValue
        val item = JCheckBoxMenuItem(value.toString())
        item.addActionListener(this)
        try {
          tcm.getColumnIndex(value)
          item.setSelected(true)
          if (columnCount == 1) item.setEnabled(false)
        } catch (e: IllegalArgumentException) {
          item.setSelected(false)
        }
        popup.add(item)
        if (value === headerValue) popup.setSelected(item)
      }

      //  Display the popup below the TableHeader
      val r = header.getHeaderRect(column)
      popup.show(header, r.x, r.height)
    }
  }

  //
  //  Implement ActionListener
  //
  /*
	 *  A table column will either be added to the table or removed from the
	 *  table depending on the state of the menu item that was clicked.
	 */
  override fun actionPerformed(event: ActionEvent) {
    if (event.source is AbstractButton) {
      val button = event.source as AbstractButton
      val column = event.actionCommand
      if (button.isSelected) showColumn(column) else hideColumn(column)
    }
  }

  //
  //  Implement TableColumnModelListener
  //
  override fun columnAdded(e: TableColumnModelEvent) {
    //  A table column was added to the TableColumnModel, so we need
    //  to update the manager to track this column
    val column = tcm.getColumn(e.toIndex)
    if (allColumns.contains(column)) return else allColumns.add(column)
  }

  override fun columnMoved(e: TableColumnModelEvent) {
    if (e.fromIndex == e.toIndex) return

    //  A table column has been moved one position to the left or right
    //  in the view of the table, so we need to update the manager to
    //  track the new location
    var index = e.toIndex
    val column = tcm.getColumn(index)
    allColumns.remove(column)
    if (index == 0) {
      allColumns.add(0, column)
    } else {
      index--
      val visibleColumn = tcm.getColumn(index)
      val insertionColumn = allColumns.indexOf(visibleColumn)
      allColumns.add(insertionColumn + 1, column)
    }
  }

  override fun columnMarginChanged(e: ChangeEvent) {}
  override fun columnRemoved(e: TableColumnModelEvent) {}
  override fun columnSelectionChanged(e: ListSelectionEvent) {}
}
