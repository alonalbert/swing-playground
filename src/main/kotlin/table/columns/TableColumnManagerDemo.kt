package table.columns

import table.columns.DynamicColumnTable.ColumnInfo
import java.awt.BorderLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.JFrame.DISPOSE_ON_CLOSE
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.border.EmptyBorder
import javax.swing.table.DefaultTableModel

private val config = mutableListOf(
    ColumnInfo("A", true, 0.5),
    ColumnInfo("B", true, 0.5),
    ColumnInfo("C", false, 0.2),
    ColumnInfo("E", true, 0.2),
)

class TableColumnManagerDemo : JPanel() {
  init {
    setLayout(BorderLayout(10, 10))
    setBorder(EmptyBorder(10, 10, 10, 10))
    val center = createCenterPanel()
    add(center, BorderLayout.CENTER)
  }

  private fun createCenterPanel(): JComponent {
    val table = JTable(DefaultTableModel(2, 5))
    val dynamicColumnTable = DynamicColumnTable(table, config)
    return JScrollPane(dynamicColumnTable.component)
  }
}

fun main() {
  SwingUtilities.invokeLater { createAndShowGUI() }
}

fun createAndShowGUI() {
  JFrame("Demo").apply {
    setDefaultCloseOperation(EXIT_ON_CLOSE)
    val button = JButton("Click Here").apply {
      margin = Insets(20, 20, 20, 20)
    }
    button.addActionListener {
      JFrame("Demo").apply {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        add(TableColumnManagerDemo())
        pack()
        setLocationRelativeTo(null)
        isVisible = true
      }
    }
    add(button)
    pack()
    setSize(400, 400)
    setLocationRelativeTo(null)
    isVisible = true
  }
}
