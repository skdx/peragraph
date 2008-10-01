package com.peralex.utilities.ui.table;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A collection of few useful functions for JTable
 * 
 * @author David Lee
 * @author Noel Grandin
 */
public class TableLib
{
	/** not meant to be instantiated */
	private TableLib()
	{
	}

	/**
	 * Set the visible no of rows for a JTable (i.e. the number of rows before it starts scrolling). This is very useful
	 * to prevent tables in JScrollPanes from demanding excessive amounts of vertical space.
	 * 
	 * Note: Kudos to Santhosh Kumar for this method.
	 */
	public static void setVisibleRowCount(JTable table, int rows)
	{
		int height = 0;
		for (int row = 0; row < rows; row++)
		{
			height += table.getRowHeight(row);
		}

		table.setPreferredScrollableViewportSize(new Dimension(table
				.getPreferredScrollableViewportSize().width, height));
	}

	/**
	 * @see #resizeColumnToFitContent(javax.swing.JTable, int[], int)
	 */
	public static void resizeColumnToFitContent(final JTable table, final int padding)
	{
		resizeColumnToFitContent(table, null, padding);
	}

	public static void disableAllTypesOfSelection(JTable oTable)
	{
		oTable.setRowSelectionAllowed(false);
		oTable.setColumnSelectionAllowed(false);
		oTable.setCellSelectionEnabled(false);
	}

	public static void setRowHeight(JTable table, double ratioRelativeToCurrentFont)
	{
		final int newHeight = (int) (table.getFontMetrics(table.getFont()).getHeight() * ratioRelativeToCurrentFont);
		table.setRowHeight(newHeight);
	}

	public static void configurePreferredScrollableViewportSize(JTable table, int visibleRowCount)
	{
		table.setPreferredScrollableViewportSize(getPreferredScrollableViewportSize(table,
				visibleRowCount));
	}

	public static Dimension getPreferredScrollableViewportSize(JTable table, int visibleRowCount)
	{
		final int width = table.getPreferredSize().width;
		final int height = table.getFontMetrics(table.getFont()).getHeight();

		final Dimension minimalSize = new Dimension(width, height * visibleRowCount);

		return minimalSize;
	}

	/**
	 * lock the width of a column by setting the min, max, and preferred sizes
	 */
	public static void lockColumnWidth(JTable oUnitsTable, int columnIdx, int width_px)
	{
		oUnitsTable.getColumnModel().getColumn(columnIdx).setPreferredWidth(width_px);
		oUnitsTable.getColumnModel().getColumn(columnIdx).setMinWidth(width_px);
		oUnitsTable.getColumnModel().getColumn(columnIdx).setMaxWidth(width_px);
	}

	/**
	 * This will resize the columns to fit their content. If the width is potentially too wide, you can limit it by
	 * specified the preferred max width for that column.
	 * 
	 * Remember to call setAutoResizeMode( JTable.AUTO_RESIZE_OFF ) on the JTable object, otherwise the adjusted width
	 * will be overriden.
	 * 
	 * @param table
	 * @param preferredMaxWidth If this is null, it will use the width calculated from the content.
	 * @param padding extra space for each column, if desired
	 */
	public static void resizeColumnToFitContent(JTable table, int[] preferredMaxWidth, int padding)
	{
		final TableColumnModel columns = table.getColumnModel();

		final int[] maxColumnWidth = TableWidthFinder.computeMaxColumnWidth(table);
		for (int i = 0; i < maxColumnWidth.length; i++)
		{
			final int preferredWidth = preferredMaxWidth == null ? Integer.MAX_VALUE
					: preferredMaxWidth[i];
			final int maxContentWidth = maxColumnWidth[i] + padding;
			columns.getColumn(i).setPreferredWidth(Math.min(maxContentWidth, preferredWidth));
		}
	}

	private static class TableWidthFinder
	{
		public static int[] computeMaxColumnWidth(JTable table)
		{
			final int[] headerWidth = getHeaderWidth(table);
			return updateMaxWidthTable(table, headerWidth);
		}

		private static int[] getHeaderWidth(JTable table)
		{
			TableCellRenderer defaultHeaderRenderer = table.getTableHeader().getDefaultRenderer();

			final TableColumnModel columns = table.getColumnModel();
			final int columnCount = columns.getColumnCount();
			final int[] headerWidth = new int[columnCount];

			for (int columnId = 0; columnId < columnCount; columnId++)
			{
				TableColumn column = columns.getColumn(columnId);
				final Object value = column.getHeaderValue();
				TableCellRenderer renderer = column.getHeaderRenderer();
				if (renderer == null)
				{
					renderer = defaultHeaderRenderer;
				}

				Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1,
						columnId);

				headerWidth[columnId] = (int) c.getPreferredSize().getWidth();
			}

			return headerWidth;
		}

		private static int[] updateMaxWidthTable(JTable table, int[] colWidth)
		{
			TableModel data = table.getModel();
			for (int row = 0; row < data.getRowCount(); row++)
			{
				updateMaxWidthTable(table, data, row, colWidth);
			}

			return colWidth;
		}

		private static void updateMaxWidthTable(JTable table, TableModel data, int row, int[] colWidth)
		{
			for (int col = 0; col < data.getColumnCount(); col++)
			{
				final TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
				final Component c = table.prepareRenderer(cellRenderer, row, col);
				colWidth[col] = Math.max(colWidth[col], c.getPreferredSize().width);
			}
		}
	}
}
