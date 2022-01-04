package com.example;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class BlockComboBoxExample extends JFrame {
    final String SEPARATOR = "SEPARATOR";

    public BlockComboBoxExample() {
        super("Block ComboBox Example");

        String[][] str = {{"A", "B", "C"}, {"1", "2", "3"},
                {"abc", "def", "ghi"}};

        JComboBox combo = new JComboBox(makeVectorData(str));
        combo.setRenderer(new ComboBoxRenderer());
        combo.addActionListener(new BlockComboListener(combo));

        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(combo);
        setSize(300, 100);
        setVisible(true);
    }

    private Vector makeVectorData(String[][] str) {
        boolean needSeparator = false;
        Vector data = new Vector();
        for (int i = 0; i < str.length; i++) {
            if (needSeparator) {
                data.addElement(SEPARATOR);
            }
            for (int j = 0; j < str[i].length; j++) {
                data.addElement(str[i][j]);
                needSeparator = true;
            }
        }
        return data;
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception evt) {
        }

        BlockComboBoxExample frame = new BlockComboBoxExample();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        JSeparator separator;

        public ComboBoxRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(1, 1, 1, 1));
            separator = new JSeparator(JSeparator.HORIZONTAL);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            String str = (value == null) ? "" : value.toString();
            if (SEPARATOR.equals(str)) {
                return separator;
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setFont(list.getFont());
            setText(str);
            return this;
        }
    }

    class BlockComboListener implements ActionListener {
        JComboBox combo;

        Object currentItem;

        BlockComboListener(JComboBox combo) {
            this.combo = combo;
            combo.setSelectedIndex(0);
            currentItem = combo.getSelectedItem();
        }

        public void actionPerformed(ActionEvent e) {
            String tempItem = (String) combo.getSelectedItem();
            if (SEPARATOR.equals(tempItem)) {
                combo.setSelectedItem(currentItem);
            } else {
                currentItem = tempItem;
            }
        }
    }
}
