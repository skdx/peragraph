package com.peralex.utilities.ui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * A TableCellRenderer that will breaks large chunks of text into multiple lines.
 * 
 * @author Noel Grandin
 */
public class TextAreaRenderer extends JTextArea implements TableCellRenderer
{
  private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  
	/** Used to check current position of scrollbar against old max value */
	private int iAtEndValue;

	private final JScrollPane scrollPane;

	public TextAreaRenderer(JScrollPane scrollPane)
	{
		this.scrollPane = scrollPane;
		setLineWrap(true);
		setWrapStyleWord(true);
		setBorder(NO_FOCUS_BORDER);
	}

	public Component getTableCellRendererComponent(final JTable table, final Object obj, final boolean isSelected,
			final boolean hasFocus, final int row, final int column)
	{
		if (isSelected)
		{
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		}
		else
		{
			super.setForeground(table.getForeground());
			super.setBackground(table.getBackground());
		}

		setFont(table.getFont());

		if (hasFocus)
		{
			Border border = null;
			if (isSelected)
			{
				border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
			}
			if (border == null)
			{
				border = UIManager.getBorder("Table.focusCellHighlightBorder");
			}
			setBorder(border);

			if (!isSelected && table.isCellEditable(row, column))
			{
				Color col;
				col = UIManager.getColor("Table.focusCellForeground");
				if (col != null)
				{
					super.setForeground(col);
				}
				col = UIManager.getColor("Table.focusCellBackground");
				if (col != null)
				{
					super.setBackground(col);
				}
			}
		}
		else
		{
			setBorder(NO_FOCUS_BORDER);
		}
		
		setText((String) obj);

		// adjust the height of the row
		final int currentHeight = table.getRowHeight(row);
		setSize(table.getColumnModel().getColumn(column).getWidth(), currentHeight);
		final int heightNeeded = (int) getPreferredSize().getHeight();
		if (heightNeeded > currentHeight)
		{
			table.setRowHeight(row, heightNeeded);
		}

		// If the scrollbar is positioned at the bottom of the viewport, keep it positioned at the bottom,
		// even if the viewport height changes.
		final JScrollBar oScrollBar = scrollPane.getVerticalScrollBar();
		if (oScrollBar.getValue() == iAtEndValue)
		{
			oScrollBar.setValue(oScrollBar.getMaximum());
		}
		iAtEndValue = oScrollBar.getMaximum() - oScrollBar.getVisibleAmount();
		return this;
	}
}
