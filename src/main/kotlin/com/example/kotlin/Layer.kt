package com.example.kotlin

import com.example.kotlin.Icons.CLOSE
import com.example.kotlin.Icons.CLOSE_HOVERED
import com.example.kotlin.Icons.EMPTY_ICON
import com.example.kotlin.Icons.FAVORITE_FILLED
import com.example.kotlin.Item.SeparatorItem
import com.example.kotlin.Item.TextItem
import com.example.kotlin.ListLayerUI.HoveredIcon.Type.DELETE
import com.example.kotlin.ListLayerUI.HoveredIcon.Type.FAVORITE
import java.awt.AWTEvent.MOUSE_EVENT_MASK
import java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.LINE_END
import java.awt.BorderLayout.LINE_START
import java.awt.Component
import java.awt.Graphics
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON1
import java.awt.event.MouseEvent.MOUSE_RELEASED
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JLayer
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSeparator
import javax.swing.ListCellRenderer
import javax.swing.ListModel
import javax.swing.SwingUtilities
import javax.swing.plaf.LayerUI
import javax.swing.plaf.basic.BasicComboPopup

fun main() {
  SwingUtilities.invokeLater { createAndShowGUI() }
}

private fun createAndShowGUI() {
  JFrame.setDefaultLookAndFeelDecorated(true)

  val frame = JFrame("HelloWorldSwing")
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
  frame.contentPane.add(getComponent())

  frame.pack()
  frame.setSize(1000, 500)
  Toolkit.getDefaultToolkit().screenSize.let {
    val x = ((it.getWidth() - frame.width) / 2).toInt()
    val y = ((it.getHeight() - frame.height) / 2).toInt()
    frame.setLocation(x, y)
  }
  frame.isVisible = true
}

private fun getComponent(): JPanel {
  val comboBox = JComboBox(
    arrayOf(
      TextItem("Item 1", isFavorite = false),
      TextItem("Item 2", isFavorite = true),
      SeparatorItem,
      TextItem("Item 3", isFavorite = false),
      TextItem("Item 4", isFavorite = true),
    )
  ).apply {
    setRenderer(ComboBoxRenderer())
  }

  val popup = comboBox.accessibleContext.getAccessibleChild(0) as (BasicComboPopup)

  @Suppress("UNCHECKED_CAST")
  val list = popup.list as JList<Item>
  val scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane::class.java, list) as JScrollPane
  val layer = JLayer(list)
  val ui = ListLayerUI(list, layer)
  layer.setUI(ui)
  scrollPane.setViewportView(layer)
  val panel = JPanel().apply {
    add(comboBox)
  }

  return panel
}

private sealed class Item {
  class TextItem(val text: String, var isFavorite: Boolean) : Item() {
    override val component: JComponent
      get() = itemComponent.apply {
        setText(text)
//        setFavorite(isFavorite)
      }

    companion object {
      private val itemComponent = TextItemComponent()

      fun getFavoriteIconBounds(offset: Point = Point(0, 0)) = itemComponent.getFavoriteIconBounds() + offset

      fun getDeleteIconBounds(offset: Point = Point(0, 0)) = itemComponent.getDeleteIconBounds() + offset
    }

    private class TextItemComponent : JPanel(BorderLayout()) {
      private val favoriteIcon = JLabel().apply {
        icon = EMPTY_ICON
      }
      private val label = JLabel()
      private val deleteIcon = JLabel().apply {
        icon = EMPTY_ICON
      }

      init {
        add(favoriteIcon, LINE_START)
        add(label, CENTER)
        add(deleteIcon, LINE_END)
      }

      fun setText(text: String) {
        label.text = text
      }

      fun getFavoriteIconBounds(): Rectangle {
        return favoriteIcon.bounds
      }

      fun getDeleteIconBounds(): Rectangle {
        return deleteIcon.bounds
      }
    }
  }

  object SeparatorItem : Item() {
    override val component: JComponent get() = JSeparator()
  }

  abstract val component: JComponent
}

private class ComboBoxRenderer : ListCellRenderer<Item> {
  override fun getListCellRendererComponent(
    list: JList<out Item>,
    value: Item,
    index: Int,
    isSelected: Boolean,
    cellHasFocus: Boolean
  ): Component {
    value.component.background = if (isSelected) list.selectionBackground else list.background
    return value.component
  }
}

private class ListLayerUI(
  private val list: JList<Item>,
  private val layer: JLayer<JList<Item>>,
) : LayerUI<JList<Item>>() {
  private var hoveredIcon: Rectangle? = null

  override fun paint(graphics: Graphics, component: JComponent) {
    super.paint(graphics, component)
    println("paint")
    @Suppress("UNCHECKED_CAST")
    list.model.iterator().forEachIndexed { index, item ->
      if (item !is TextItem) {
        return@forEachIndexed
      }
      val cellBounds = list.getCellBounds(index, index)
      val (x, y) = cellBounds.location

      val favoriteIcon = if (item.isFavorite) FAVORITE_FILLED else EMPTY_ICON
      val (favoriteX, favoriteY) = TextItem.getFavoriteIconBounds().location
      favoriteIcon.paintIcon(component, graphics, x + favoriteX, y + favoriteY)

      val mousePosition = MouseInfo.getPointerInfo().location - list.locationOnScreen
      val deleteIconBounds = TextItem.getDeleteIconBounds()
      val deleteIcon = when {
        !cellBounds.contains(mousePosition) -> EMPTY_ICON
        hoveredIcon == deleteIconBounds  -> CLOSE_HOVERED
        else -> CLOSE
      }
      val (closeX, closeY) = deleteIconBounds.location
      deleteIcon.paintIcon(component, graphics, x + closeX, y + closeY)
    }
  }

  override fun installUI(component: JComponent) {
    super.installUI(component)
    layer.layerEventMask = MOUSE_EVENT_MASK or MOUSE_MOTION_EVENT_MASK
  }

  override fun uninstallUI(component: JComponent) {
    super.uninstallUI(component)
    layer.layerEventMask = 0
  }

  override fun processMouseEvent(event: MouseEvent, layer: JLayer<out JList<Item>>) {
    if (event.id == MOUSE_RELEASED && event.button == BUTTON1) {
      val index = list.selectedIndex
      val cellLocation = list.getCellBounds(index, index).location
      val favoriteIconBounds = TextItem.getFavoriteIconBounds(cellLocation)
      val deleteIconBounds = TextItem.getDeleteIconBounds(cellLocation)
      val mousePoint = event.point
      when {
        favoriteIconBounds.contains(mousePoint) -> favoriteClicked(index, favoriteIconBounds).also { event.consume() }
        deleteIconBounds.contains(mousePoint) -> deleteIconClicked(index).also { event.consume() }
      }
    }
  }

  private fun deleteIconClicked(index: Int) {
    val model = list.model as DefaultComboBoxModel<Item>
    model.removeElementAt(index)
    list.selectedIndex = if (index >= model.size) index - 1 else index
  }

  private fun favoriteClicked(index: Int, bounds: Rectangle) {
    val model = list.model as DefaultComboBoxModel<Item>
    val textItem = model.getElementAt(index) as TextItem
    textItem.isFavorite = !textItem.isFavorite
    layer.paintImmediately(bounds)
  }

  override fun processMouseMotionEvent(event: MouseEvent, layer: JLayer<out JList<Item>>) {
    @Suppress("UNCHECKED_CAST")
    val list = event.component as JList<Item>
    val index = list.selectedIndex
    if (list.model.getElementAt(index) !is TextItem) {
      return
    }

    val cellLocation = list.getCellBounds(index, index).location
    val favoriteIconBounds = TextItem.getFavoriteIconBounds(cellLocation)
    val deleteIconBounds = TextItem.getDeleteIconBounds(cellLocation)
    val mousePoint = event.point
    val hovered = when {
      favoriteIconBounds.contains(mousePoint) -> favoriteIconBounds
      deleteIconBounds.contains(mousePoint) -> deleteIconBounds
      else -> null
    }
    if (hovered != hoveredIcon) {
      hovered?: hoveredIcon?.let {
        layer.paintImmediately(it)
      }
      hoveredIcon = hovered
    }
  }

  private data class HoveredIcon(val type: Type, val index: Int) {
    enum class Type {
      FAVORITE,
      DELETE,
    }
  }
}

private operator fun Rectangle.plus(point: Point): Rectangle {
  return Rectangle(x + point.x, y + point.y, width, height)
}

private operator fun Point.minus(point: Point): Point = Point(x - point.x, y - point.y)

private operator fun Point.component1(): Int = x

private operator fun Point.component2(): Int = y

private fun <T> ListModel<T>.iterator() = (0 until size).map { getElementAt(it) }