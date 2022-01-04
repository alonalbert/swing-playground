package com.example

import com.example.SavedFilterComboItem1.*
import java.awt.Component
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.*
import javax.swing.SwingConstants.HORIZONTAL
import javax.swing.event.ListDataListener


object Main {
    private fun createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true)

        val frame = JFrame("HelloWorldSwing")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane.add(FilterComponent())

        frame.pack()
        frame.setSize(1000, 500)
        Toolkit.getDefaultToolkit().screenSize.let {
            val x = ((it.getWidth() - frame.width) / 2).toInt()
            val y = ((it.getHeight() - frame.height) / 2).toInt()
            frame.setLocation(x, y)
        }
        frame.isVisible = true
    }

    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater { createAndShowGUI() }
    }


    private val component: JComponent
        get() {
            return JPanel().apply {
                add(JComboBox<SavedFilterComboItem1>().apply {
                    selectedItem = null
                    preferredSize = Dimension(200, preferredSize.height)
                    renderer = SavedFilterComboRenderer1()
                    val savedFilterComboModel = SavedFilterComboModel1()
                    model = savedFilterComboModel
                    savedFilterComboModel.addSavedFilter(SavedFilter("Foo", "foo"))
                    savedFilterComboModel.addSavedFilter(SavedFilter("Bar", "bar"))
                }
                )
            }
        }
}

private sealed class SavedFilterComboItem1 {
    class SavedFilter(val name: String, val filter: String) : SavedFilterComboItem1() {
        override val component = JLabel(name)
    }

    class Label(val name: String) : SavedFilterComboItem1() {
        override val component = JLabel(name)
    }

    class Separator() : SavedFilterComboItem1() {
        override val component = JSeparator(HORIZONTAL)
    }

    abstract val component: Component
}

private class SavedFilterComboRenderer1 : ListCellRenderer<SavedFilterComboItem1> {
    override fun getListCellRendererComponent(
        list: JList<out SavedFilterComboItem1>,
        value: SavedFilterComboItem1?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        return value?.component ?: JLabel("Unsaved filter")
    }
}

private val CLEAR_FILTER_ITEM = Label("Clear filter")
private val MANAGE_FILTERS_ITEM = Label("Manage filters")
private val SEPARATOR = Separator()

private class SavedFilterComboModel1 : ComboBoxModel<SavedFilterComboItem1> {
    private val savedFilters = sortedMapOf<String, SavedFilter>()
    private var selectedItem: SavedFilter? = null

    override fun getSize(): Int {
        return savedFilters.size + 4
    }

    override fun getElementAt(index: Int): SavedFilterComboItem1 {
        return when (index) {
            0 -> CLEAR_FILTER_ITEM
            1 -> SEPARATOR
            size - 2 -> SEPARATOR
            size - 1 -> MANAGE_FILTERS_ITEM
            else -> savedFilters.values.toList()[index - 2]
        }
    }

    override fun addListDataListener(l: ListDataListener?) {
    }

    override fun removeListDataListener(l: ListDataListener?) {
    }

    override fun setSelectedItem(anItem: Any?) {
        if (anItem is SavedFilter) {
            selectedItem = anItem
        } else if (anItem == CLEAR_FILTER_ITEM) {
            selectedItem = null
        }
    }

    override fun getSelectedItem(): Any? {
        return selectedItem
    }

    fun addSavedFilter(savedFilter: SavedFilter) {
        savedFilters[savedFilter.name] = savedFilter
    }
}



