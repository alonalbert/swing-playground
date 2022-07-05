package com.example.kotlin

import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

class EmptyIcon(private val width: Int, private val height: Int) : Icon {
  override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {}

  override fun getIconWidth() = width

  override fun getIconHeight() = height
}