package hex

import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main() {
  SwingUtilities.invokeLater { createAndShowGUI() }
}

fun createAndShowGUI() {
  JFrame("Demo").apply {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    add(BinaryDataViewer("01234567890123456789012345678901234567890".toByteArray()))
    pack()
//    setSize(400, 400)
    setLocationRelativeTo(null)
    isVisible = true
  }
}
