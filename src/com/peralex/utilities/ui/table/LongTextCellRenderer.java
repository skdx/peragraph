package com.peralex.utilities.ui.table;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Useful renderer for displaying long sequences of text that need to wrap. 
 * Will make the JTable have rows of different heights.
 * 
 * @author Heinz Kabutz
 * @author Noel Grandin
 */
public class LongTextCellRenderer extends JTextArea implements TableCellRenderer
{
	private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

	public LongTextCellRenderer(final JTable table)
	{
		setLineWrap(true);
		setWrapStyleWord(true);

		// if the table width changes, we need to recalculate row height
		table.addComponentListener(new ComponentAdapter() {
			private int width = -1;
			@Override
			public void componentResized(ComponentEvent e)
			{
				// I need to only fire on width changes otherwise I trigger a loop of events
				if (table.getWidth()==width) return;
				this.width = table.getWidth();
				// force the table to recalculate row heights, without recreating columns
				table.tableChanged(new TableModelEvent(table.getModel(), 1, Integer.MAX_VALUE, 
						TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
			}
		});
	}

	/**
	 * @deprecated use the LongTextCellRenderer(JTable) constructor
	 */
	@Deprecated
	public LongTextCellRenderer()
	{
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
	{
		// set the colours, etc. using the standard for that platform
		adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setForeground(adaptee.getForeground());
		setBackground(adaptee.getBackground());
		setBorder(adaptee.getBorder());
		setFont(adaptee.getFont());
		setText(adaptee.getText());

		setText((String) value);

		final int currentHeight = table.getRowHeight(row);
		// set our width up so we calculate height correctly
		setSize(table.getColumnModel().getColumn(column).getWidth(), currentHeight);
		final int heightNeeded = (int) getPreferredSize().getHeight();
		// increase the row height if it is too small
		if (heightNeeded > currentHeight)
		{
			table.setRowHeight(row, heightNeeded);
		}

		return this;
	}
}