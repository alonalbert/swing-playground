package hex

import java.awt.Color
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.ScrollPaneConstants
import javax.swing.border.CompoundBorder
import javax.swing.border.MatteBorder
import kotlin.random.Random


private val FONT = Font(Font.MONOSPACED, Font.PLAIN, 14)

internal class BinaryDataViewer(bytes: ByteArray) : JPanel(FlowLayout(FlowLayout.LEFT)) {
  private val addressView = TextArea(bytes.toAddressRows())
  private val hexView = TextArea(bytes.toHexRows())
  private val asciiView = TextArea(bytes.toAsciiRows()).apply {
    border = CompoundBorder(MatteBorder(0, 1, 0, 1, foreground), border)
  }

  init {
    val panel = JPanel()
    val scrollPane = JScrollPane(panel)
    panel.background = addressView.background

    add(scrollPane)


    panel.add(addressView)
    panel.add(hexView)
    panel.add(asciiView)
//    layout.setHorizontalGroup(
//      layout.createSequentialGroup()
//        .addComponent(addressView)
//        .addComponent(hexView)
//        .addComponent(asciiView)
//    )
//    layout.setVerticalGroup(
//      layout.createParallelGroup()
//        .addComponent(addressView)
//        .addComponent(hexView)
//        .addComponent(asciiView)
//    )

    background = Color.DARK_GRAY
  }

  private class TextArea(text: String) : JTextArea(text) {
    init {
      font = FONT
      border = MatteBorder(8, 8, 8, 8, background)
    }
  }
}

fun main() {
  println(Random(0).nextBytes(300).toAsciiRows())
}

private fun ByteArray.toAddressRows(): String {
  val rows = ((size - 1) / 16) + 1
  return (0 until rows).joinToString("\n") { "%08x".format(it * 16) }
}

private fun ByteArray.toHexRows(): String {
  return asSequence().chunked(16) { row ->
    row.chunked(8) { block ->
      block.joinToString(" ") { "%02x".format(it) }
    }.joinToString("  ") { it }
  }.joinToString("\n") { it }
}

private fun ByteArray.toAsciiRows(): String {
  return asSequence().chunked(16) { row ->
    row.joinToString("") {
      val char = it.toInt().toChar()
      when {
        char.isPrintable() -> char.toString()
        else -> "."
      }
    }
  }.joinToString("\n") { it }
}

private fun Char.isPrintable() = !Character.isISOControl(this) && this.code < 127
