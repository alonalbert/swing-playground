package table.columns

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JTable
import javax.swing.event.ChangeEvent
import javax.swing.event.ListSelectionEvent
import javax.swing.event.TableColumnModelEvent
import javax.swing.event.TableColumnModelListener

class DynamicColumnTable(private val table: JTable, private val config: MutableList<ColumnInfo>) {
  val component get() = table
  private val tcm get() = table.columnModel
  private val allColumns = tcm.columns.asSequence().associateBy { it.headerValue }
  private val configColumns = config.associateBy { it.name }
  private val columnModelListener = ColumnModelListener()
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

  private fun withoutColumnModelListener(block: () -> Unit) {
    tcm.removeColumnModelListener(columnModelListener)
    try {
      block()
    } finally {
      tcm.addColumnModelListener(columnModelListener)
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
}