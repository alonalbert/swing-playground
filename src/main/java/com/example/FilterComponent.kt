package com.example

import com.example.SavedFilterComboItem.*
import com.intellij.ui.DocumentAdapter
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.*
import javax.swing.GroupLayout.Alignment.CENTER
import javax.swing.GroupLayout.DEFAULT_SIZE
import javax.swing.GroupLayout.PREFERRED_SIZE
import javax.swing.event.DocumentEvent
import javax.swing.event.ListDataListener

internal class FilterComponent : JPanel() {
  private val savedFiltersCombo = SavedFilterCombo()
  private val filterText = JTextField()
  private val saveButton = JButton("Save")

  init {
    layout = GroupLayout(this).apply {
      autoCreateContainerGaps = true
      autoCreateGaps = true
      setHorizontalGroup(
        createSequentialGroup()
          .addComponent(savedFiltersCombo, DEFAULT_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
          .addComponent(filterText)
          .addComponent(saveButton))
      setVerticalGroup(
        createParallelGroup(CENTER)
          .addComponent(savedFiltersCombo, DEFAULT_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
          .addComponent(filterText, DEFAULT_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
          .addComponent(saveButton))
    }

    filterText.document.addDocumentListener(object : DocumentAdapter() {
      override fun textChanged(e: DocumentEvent) {
        saveButton.isEnabled = filterText.text.isNotEmpty()
      }
    })
    saveButton.isEnabled = filterText.text.isNotEmpty()

    saveButton.addActionListener {
      JOptionPane().createDialog(this, "")
      val name = JOptionPane.showInputDialog(
        this,
        "Name",
        "Save as new filter",
        JOptionPane.PLAIN_MESSAGE,
        null,
        null,
        ""
      ) as? String
      if (name != null) {
        savedFiltersCombo.addSavedFilter(name, filterText.text)
        savedFiltersCombo.setSelectedSavedFilter(name)
      }
    }

    savedFiltersCombo.addItemListener {
      filterText.text = (it.item as SavedFilter).filter
    }
  }
}

private class SavedFilterCombo : JComboBox<SavedFilterComboItem>() {
  private val savedFilterComboModel = SavedFilterComboModel()

  init {
    selectedItem = null
    // Do not use "renderer" field syntax because there is also a protected field by that name that hides the method
    setRenderer(SavedFilterComboRenderer())
    model = savedFilterComboModel
  }

  fun addSavedFilter(name: String, filter: String) {
    savedFilterComboModel.addSavedFilter(SavedFilter(name, filter))
  }

  fun setSelectedSavedFilter(name: String) {
    savedFilterComboModel.setSelectedSavedFilter(name)
  }
}

private sealed class SavedFilterComboItem {
  class SavedFilter(val name: String, val filter: String) : SavedFilterComboItem() {
    override val component = JLabel(name)
  }

  class Button(val name: String) : SavedFilterComboItem() {
    override val component = JLabel(name)
  }

  class Separator() : SavedFilterComboItem() {
    override val component = JSeparator(SwingConstants.HORIZONTAL)
  }

  abstract val component: Component
}

private class SavedFilterComboRenderer : ListCellRenderer<SavedFilterComboItem> {
  override fun getListCellRendererComponent(
    list: JList<out SavedFilterComboItem>,
    value: SavedFilterComboItem?,
    index: Int,
    isSelected: Boolean,
    cellHasFocus: Boolean
  ): Component {
    return value?.component ?: JLabel("Unsaved filter")
  }
}

private val CLEAR_FILTER_ITEM = Button("Clear filter")
private val NO_SAVED_FILTERS_ITEM = Button("Saved filters will appear here")
private val MANAGE_FILTERS_ITEM = Button("Manage filters")
private val SEPARATOR = Separator()

private class SavedFilterComboModel : ComboBoxModel<SavedFilterComboItem> {
  private val savedFilters = sortedMapOf<String, SavedFilter>()
  private var selectedItem: SavedFilter? = null

  override fun getSize() = if (savedFilters.isEmpty()) 5 else savedFilters.size + 4

  override fun getElementAt(index: Int): SavedFilterComboItem {
    return when {
      index == 0 -> CLEAR_FILTER_ITEM
      index == 1 -> SEPARATOR
      index == size - 2 -> SEPARATOR
      index == size - 1 -> MANAGE_FILTERS_ITEM
      savedFilters.isEmpty() -> NO_SAVED_FILTERS_ITEM
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

  fun setSelectedSavedFilter(name: String) {
    selectedItem = savedFilters[name]
  }
}
