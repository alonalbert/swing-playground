package com.example

import javax.swing.JFrame
import java.awt.FlowLayout
import javax.swing.JTextArea
import javax.swing.JScrollPane
import kotlin.jvm.JvmStatic
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.swing.SwingUtilities

object JScrollPaneExample {
  private const val serialVersionUID = 1L
  private fun createAndShowGUI() {

    // Create and set up the window.
    val frame = JFrame("Scroll Pane Example")

    // Display the window.
    frame.setSize(500, 500)
    frame.isVisible = true
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    // set flow layout for the frame
    frame.contentPane.layout = FlowLayout()
    val mouseAdapter = object : MouseAdapter() {
      override fun mouseClicked(e: MouseEvent) {
        println("${e.component::class.simpleName} mouseClicked")
      }

      override fun mousePressed(e: MouseEvent) {
        println("${e.component::class.simpleName} mousePressed")
      }

      override fun mouseReleased(e: MouseEvent) {
        println("${e.component::class.simpleName} mouseReleased")
      }

      override fun mouseEntered(e: MouseEvent) {
        println("${e.component::class.simpleName} mouseEntered")
      }

      override fun mouseExited(e: MouseEvent) {
        println("${e.component::class.simpleName} mouseExited")
      }

      override fun mouseWheelMoved(e: MouseWheelEvent) {
        println("${e.component::class.simpleName} mouseWheelMoved")
      }

      override fun mouseDragged(e: MouseEvent) {
        println("${e.component::class.simpleName} mouseDragged")
      }

      override fun mouseMoved(e: MouseEvent) {
        println("${e.component::class.simpleName} mouseMoved")
      }
    }
    val textArea = JTextArea(20, 20).apply {
      text = (1..100).joinToString("\n") { "Line $it" }
    }
    val scrollableTextArea = JScrollPane(textArea).apply {
      horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
      verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
      addMouseWheelListener(mouseAdapter)
      verticalScrollBar.addMouseListener(mouseAdapter)
      verticalScrollBar.addMouseMotionListener(mouseAdapter)
    }

    frame.contentPane.add(scrollableTextArea)
  }

  @JvmStatic
  fun main(args: Array<String>) {
    SwingUtilities.invokeLater { createAndShowGUI() }
  }
}
