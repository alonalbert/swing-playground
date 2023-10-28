package table.columns

import java.awt.BorderLayout
import javax.swing.*
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.border.EmptyBorder
import javax.swing.table.DefaultTableModel

class TableColumnManagerDemo : JPanel() {
  init {
    setLayout(BorderLayout(10, 10))
    setBorder(EmptyBorder(10, 10, 10, 10))
    val center = createCenterPanel()
    add(center, BorderLayout.CENTER)
  }

  private fun createCenterPanel(): JComponent {
    val table = JTable(DefaultTableModel(15, 15))
    table.preferredScrollableViewportSize = table.getPreferredSize()
    TableColumnManager(table)
    return JScrollPane(table)
  }

  companion object {
  }

}

fun main() {
  SwingUtilities.invokeLater { createAndShowGUI() }
}

fun createAndShowGUI() {
  val frame = JFrame("Table Column Manager")
  frame.setDefaultCloseOperation(EXIT_ON_CLOSE)
  frame.add(TableColumnManagerDemo())
  frame.pack()
  frame.setLocationRelativeTo(null)
  frame.isVisible = true
}
