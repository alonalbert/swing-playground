package com.example

import java.awt.*
import javax.swing.*


fun main(args: Array<String>) {
    SwingUtilities.invokeLater { createAndShowGUI() }
}

private fun createAndShowGUI() {
    JFrame.setDefaultLookAndFeelDecorated(true)

    val frame = JFrame("HelloWorldSwing")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.contentPane.add(component)

    frame.pack()
    frame.setSize(1000, 500)
    Toolkit.getDefaultToolkit().screenSize.let {
        val x = ((it.getWidth() - frame.width) / 2).toInt()
        val y = ((it.getHeight() - frame.height) / 2).toInt()
        frame.setLocation(x, y)
    }
    frame.isVisible = true
}


private val component: JComponent
    get() {
        return JPanel().apply {
            layout = GridLayout(0, 2).apply {
                hgap = 40
            }
            add(Button("Item 1"))
            add(Button("Item 2"))
            add(Button("Item 3"))
            add(Button("Item 4"))
        }
    }





