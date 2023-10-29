package table.columns

import java.awt.Component
import java.awt.event.*
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ListSelectionEvent
import javax.swing.event.TableColumnModelEvent
import javax.swing.event.TableColumnModelListener

class DynamicColumnTable(private val table: JTable, private val config: MutableList<ColumnInfo>) {
  val component get() = table
  private val tcm get() = table.columnModel
  private val allColumns = tcm.columns.asSequence().associateByTo(LinkedHashMap()) { it.headerValue }
  private val configColumns = config.associateBy { it.name }
  private val columnModelListener = ColumnModelListener()
  private val popupActionListener = PopupActionListener()
  private var initialUpdateDone = false

  init {
    tcm.addColumnModelListener(columnModelListener)

    if (config.size < tcm.columnCount) {
      tcm.columns.asSequence().map { it.headerValue }.forEach { name ->
        if (config.find { it.name == name } == null) {
          config.add(ColumnInfo(name.toString(), false, 0.1))
        }
      }
    }
    setupColumns()

    table.addComponentListener(object : ComponentAdapter() {
      override fun componentResized(e: ComponentEvent) {
        withoutColumnModelListener {
          allColumns.values.forEach {
            val configColumn = configColumns[it.headerValue] ?: return@forEach
            it.preferredWidth = (table.width * configColumn.widthRatio).toInt()
          }
        }
        initialUpdateDone = true
      }
    })

    table.tableHeader.addMouseListener(object : MouseAdapter() {
      override fun mousePressed(e: MouseEvent) {
        showColumnSelectionPopup(e)
      }

      override fun mouseReleased(e: MouseEvent) {
        showColumnSelectionPopup(e)
      }
    })

  }


  private fun setupColumns() {
    withoutColumnModelListener {
      allColumns.values.forEach { table.removeColumn(it) }
      config.filter { it.visible }.forEach {
        val tableColumn = allColumns[it.name]
        if (tableColumn == null) {
          println("Unknown column: '${it.name}'")
          return@forEach
        }
        table.addColumn(tableColumn)
      }
    }
  }

  private fun showColumnSelectionPopup(e: MouseEvent) {
    if (!e.isPopupTrigger) {
      return
    }
    val header = table.tableHeader
    val column = header.columnAtPoint(e.getPoint())
    val headerValue = tcm.getColumn(column).headerValue
    val columnCount = tcm.columnCount
    val popup = SelectPopupMenu()

    //  Create a menu item for all columns managed by the table column
    //  manager, checking to see if the column is shown or hidden.
    for (tableColumn in allColumns.values) {
      val value = tableColumn.headerValue
      val item = JCheckBoxMenuItem(value.toString())
      item.addActionListener(popupActionListener)
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

  private fun withoutColumnModelListener(block: () -> Unit) {
    tcm.removeColumnModelListener(columnModelListener)
    try {
      block()
    } finally {
      tcm.addColumnModelListener(columnModelListener)
    }
  }

  private inner class PopupActionListener : ActionListener {
    override fun actionPerformed(e: ActionEvent) {
      val button = e.source as AbstractButton
      configColumns.getValue(e.actionCommand).visible = button.isSelected
      setupColumns()
    }
  }

  private inner class ColumnModelListener : TableColumnModelListener {
    override fun columnAdded(e: TableColumnModelEvent) {
    }

    override fun columnRemoved(e: TableColumnModelEvent) {
    }

    override fun columnMoved(e: TableColumnModelEvent) {
      if (e.fromIndex == e.toIndex) {
        return
      }
      val columns = tcm.columns.toList()
      val fromIndex = config.indexOfFirst { it.name == columns[e.toIndex].headerValue }
      val columnInfo = config.removeAt(fromIndex)
      val toIndex = when {
        e.toIndex == columns.size - 1 -> columns.size
        else -> config.indexOfFirst { it.name == columns[e.toIndex + 1].headerValue }
      }
      config.add(toIndex, columnInfo)
    }

    override fun columnMarginChanged(e: ChangeEvent) {
      if (initialUpdateDone) {
        val width = table.width
        tcm.columns.asSequence().forEach {
          val columnInfo = configColumns[it.headerValue] ?: return@forEach
          columnInfo.widthRatio = it.width.toDouble() / width
        }
      }
    }

    override fun columnSelectionChanged(e: ListSelectionEvent) {
    }

  }

  class ColumnInfo(val name: String, var visible: Boolean, var widthRatio: Double)

  private class SelectPopupMenu : JPopupMenu() {
    override fun setSelected(sel: Component) {
      val index = getComponentIndex(sel)
      selectionModel.selectedIndex = index
      val me = arrayOfNulls<MenuElement>(2)
      me[0] = this as MenuElement
      me[1] = getSubElements()[index]
      SwingUtilities.invokeLater { MenuSelectionManager.defaultManager().setSelectedPath(me) }
    }
  }
}