package com.peralex.utilities.ui.table;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * http://java.sun.com/docs/books/tutorial/uiswing/components/examples/TableSorter.java
 * 
 * SorterTableModel is a decorator for TableModels; adding sorting functionality to a supplied TableModel. SorterTableModel does
 * not store or copy the data in its TableModel; instead it maintains a map from the row indexes of the view to the row
 * indexes of the model. As requests are made of the sorter (like getValueAt(row, col)) they are passed to the
 * underlying model after the row numbers have been translated via the internal mapping array. This way, the SorterTableModel
 * appears to hold another copy of the table with the rows in a different order. 
 * <p/> 
 * SorterTableModel registers itself as a
 * listener to the underlying model, just as the JTable itself would. Events received from the model are examined,
 * sometimes manipulated (typically widened), and then passed on to the SorterTableModel's listeners (typically the JTable).
 * If a change to the model has invalidated the order of SorterTableModel's rows, a note of this is made and the sorter will
 * resort the rows the next time a value is requested. 
 * <p/> 
 * When the tableHeader property is set, either by using the
 * setTableHeader() method or the two argument constructor, the table header may be used as a complete UI for
 * SorterTableModel. The default renderer of the tableHeader is decorated with a renderer that indicates the sorting status
 * of each column. In addition, a mouse listener is installed with the following behavior:
 * <ul>
 * <li> Mouse-click: Clears the sorting status of all other columns and advances the sorting status of that column
 * through three values: {NOT_SORTED, ASCENDING, DESCENDING} (then back to NOT_SORTED again).
 * <li> SHIFT-mouse-click: Clears the sorting status of all other columns and cycles the sorting status of the column
 * through the same three values, in the opposite order: {NOT_SORTED, DESCENDING, ASCENDING}.
 * <li> CONTROL-mouse-click and CONTROL-SHIFT-mouse-click: as above except that the changes to the column do not cancel
 * the statuses of columns that are already sorting - giving a way to initiate a compound sort.
 * </ul>
 * <p/>
 * This is a long overdue rewrite of a class of the same name that first appeared in the swing table demos in 1997.
 * 
 * Notes
 * (1) If you directly reference your own TableModel, you must use the modelIndex() method on this class
 * to translate between view and model row indexes.
 * 
 * FIXME this could do with a smarter strategy for dealing with updates. 
 *    Invalidating the entire model<->view cache on any update does not scale very well.
 * 
 * @author Philip Milne
 * @author Brendon McLean
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @version 2.0 02/27/04
 */
public class SorterTableModel extends AbstractTableModel
{
	public static enum SortDirection {
		DESCENDING, NOT_SORTED, ASCENDING;
		
		public SortDirection next() {
			switch (this) {
			case DESCENDING: return NOT_SORTED;
			case NOT_SORTED: return ASCENDING;
			case ASCENDING: return DESCENDING;
			default: throw new IllegalStateException(""+this);
			}
		}
		
		public SortDirection prev() {
			switch (this) {
			case DESCENDING: return ASCENDING;
			case NOT_SORTED: return DESCENDING;
			case ASCENDING: return NOT_SORTED;
			default: throw new IllegalStateException(""+ this);
			}
		}
	}
	
	/** These are the unicode character constants for an up-arrow and a down-arrow */
	private static final String UP_CHAR = " \u25b2";
	private static final String DOWN_CHAR = " \u25bc";

	private static final Directive EMPTY_DIRECTIVE = new Directive(-1, SortDirection.NOT_SORTED);

	@SuppressWarnings("unchecked")
	public static final Comparator<Comparable> COMPARABLE_COMPARATOR = new Comparator<Comparable>()
	{
		public int compare(Comparable o1, Comparable o2)
		{
			return o1.compareTo(o2);
		}
	};

	@SuppressWarnings("unchecked")
	public static final Comparator LEXICAL_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			return o1.toString().compareTo(o2.toString());
		}
	};

	/**
	 * Create a SorterTableModel and apply it to a table.
	 * 
	 * @return the SorterTableModel created for the table.
	 */
  public static SorterTableModel configure(JTable oTable, TableModel tableModel)
	{
		final JTableHeader tableHeader = oTable.getTableHeader();
		tableHeader.setResizingAllowed(true);
		tableHeader.setReorderingAllowed(false);

		SorterTableModel sorter = new SorterTableModel(tableModel);
		sorter.setTableHeader(tableHeader);
		oTable.setModel(sorter);
		
		return sorter;
	}
  
	private TableModel tableModel;

	private boolean[] sortabilityList;

	/** synchronized by mappingLock */
	private Row[] viewToModel;

	/** synchronized by mappingLock */
	private int[] modelToView;

	private final Object mappingLock = new Object();
	
	private JTableHeader tableHeader;

	private MouseListener mouseListener;

	private TableModelListener tableModelListener;

	/**
	 * allows us to override the comparators by column type
	 */
	@SuppressWarnings("unchecked")
	private final Map<Class, Comparator> columnComparatorsByType = new HashMap<Class, Comparator>();

	/**
	 * allows us to override the comparators by column index
	 */
	private final Map<Integer, Object> columnComparatorsByIndex = new HashMap<Integer, Object>();
	
	private final List<Directive> sortingColumns = new ArrayList<Directive>();

	public SorterTableModel()
	{
		this.mouseListener = new MouseHandler();
		this.tableModelListener = new TableModelHandler();
	}

	public SorterTableModel(TableModel tableModel)
	{
		this();
		setTableModel(tableModel);
	}

	public SorterTableModel(TableModel tableModel, JTableHeader tableHeader)
	{
		this();
		setTableHeader(tableHeader);
		setTableModel(tableModel);
	}

	private void clearSortingState()
	{
		synchronized (mappingLock) {
			viewToModel = null;
			modelToView = null;
		}
	}

	/**
	 * get the underlying {@link TableModel}
	 */
	public TableModel getTableModel()
	{
		return tableModel;
	}

	/**
	 * Set the underlying {@link TableModel}
	 */
	public void setTableModel(TableModel tableModel)
	{
		if (this.tableModel != null)
		{
			this.tableModel.removeTableModelListener(tableModelListener);
		}

		this.tableModel = tableModel;
		if (this.tableModel != null)
		{
			this.tableModel.addTableModelListener(tableModelListener);
		}

		clearSortingState();
		fireTableStructureChanged();
	}

	/**
	 * get the {@link JTableHeader} we're modifying
	 */
	public JTableHeader getTableHeader()
	{
		return tableHeader;
	}

	public void setTableHeader(JTableHeader tableHeader)
	{
		if (this.tableHeader != null)
		{
			this.tableHeader.removeMouseListener(mouseListener);
			TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
			if (defaultRenderer instanceof SortableHeaderRenderer)
			{
				this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
			}
		}
		this.tableHeader = tableHeader;
		if (this.tableHeader != null)
		{
			this.tableHeader.addMouseListener(mouseListener);
			this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
		}
	}

	/**
	 * are there any sorted columns?
	 */
	public boolean isSorting()
	{
		return !sortingColumns.isEmpty();
	}

	/**
	 * Set which columns can be sorted.
	 */
	public void setSortabilityList(boolean[] sortabilityList)
	{
		if (tableModel.getColumnCount() != sortabilityList.length)
		{
			throw new IllegalArgumentException("Sortability List has a different length from table model.");
		}
		this.sortabilityList = sortabilityList;
	}

	private Directive getDirective(int column)
	{
		for (Directive directive : sortingColumns)
		{
			if (directive.column == column)
			{
				return directive;
			}
		}
		return EMPTY_DIRECTIVE;
	}

	public SortDirection getSortDirection(int column)
	{
		return getDirective(column).direction;
	}

	private void sortStatusChanged()
	{
		clearSortingState();
		fireTableDataChanged();
		if (tableHeader != null)
		{
			tableHeader.repaint();
		}
	}

	public void setSortDirection(int column, SortDirection status)
	{
		Directive directive = getDirective(column);
		if (directive != EMPTY_DIRECTIVE)
		{
			sortingColumns.remove(directive);
		}
		if (status != SortDirection.NOT_SORTED)
		{
			sortingColumns.add(new Directive(column, status));
		}
		sortStatusChanged();
	}

	/**
	 * Remove any sorting columns
	 */
	public void cancelSorting()
	{
		sortingColumns.clear();
		sortStatusChanged();
	}

	/**
	 * set a comparator for a column type
	 */
	public <T> void setColumnComparator(Class<T> columnType, Comparator<T> comparator)
	{
		if (comparator == null)
		{
			columnComparatorsByType.remove(columnType);
		}
		else
		{
			columnComparatorsByType.put(columnType, comparator);
		}
	}

	/**
	 * set a comparator for a specific column
	 */
	public void setColumnComparator(int columnIdx, Comparator<?> comparator)
	{
		if (comparator == null)
		{
			columnComparatorsByIndex.remove(columnIdx);
		}
		else
		{
			columnComparatorsByIndex.put(columnIdx, comparator);
		}
	}
	
	/**
	 * set a comparator for a specific column
	 */
	public void setColumnComparator(int columnIdx, ITableColumnSorter comparator)
	{
		if (comparator == null)
		{
			columnComparatorsByIndex.remove(columnIdx);
		}
		else
		{
			columnComparatorsByIndex.put(columnIdx, comparator);
		}
	}
	
	/**
	 * this method can return either a Comparator or a ITableRowComparator
	 */
	private Object getComparator(int column)
	{
		// first check for column-specific overrides
		Object comparator = columnComparatorsByIndex.get(column);
		if (comparator != null)
		{
			return comparator;
		}
		
		// then check for type-specific overrides
		Class<?> columnType = tableModel.getColumnClass(column);
		if (columnType==null) {
			throw new IllegalStateException("tableModel returned null for column class, column=" + column + " model=" + tableModel.getClass());
		}
		comparator = columnComparatorsByType.get(columnType);
		if (comparator != null)
		{
			return comparator;
		}
		if (Comparable.class.isAssignableFrom(columnType))
		{
			return COMPARABLE_COMPARATOR;
		}
		return LEXICAL_COMPARATOR;
	}

	private boolean buildingViewToModel = false;
	
	private Row[] getViewToModel()
	{
		synchronized (mappingLock) {
			/* If we call back into ourselves while we are building this thing, 
			 * that can cause serious weirdness and hard-to-find bugs, so make it illegal.
			 */
			if (buildingViewToModel) throw new IllegalStateException("recursive calls illegal!");
			
			if (viewToModel == null)
			{
				
				try {
					buildingViewToModel = true;
					int tableModelRowCount = tableModel.getRowCount();
					viewToModel = new Row[tableModelRowCount];
					for (int row = 0; row < tableModelRowCount; row++)
					{
						viewToModel[row] = new Row(row);
					}
		
					if (isSorting())
					{
						Arrays.sort(viewToModel);
						// if we're sorting, we going to need the modelToView array.
						modelToView = buildModelToView(viewToModel);
					}
				} finally {
					buildingViewToModel = false;
				}
			}
			return viewToModel;
		}
	}

	private static int [] buildModelToView(Row [] viewToModel)
	{
		final int n = viewToModel.length;
		final int [] modelToView = new int[n];
		for (int i = 0; i < n; i++)
		{
			modelToView[viewToModel[i].modelIndex] = i;
		}
		return modelToView;
	}
	
	/**
	 * translate view index to model index
	 */
	public int modelIndex(int viewIndex)
	{
		try
		{
			return getViewToModel()[viewIndex].modelIndex;
		}
		catch (RuntimeException e)
		{
			System.out.println("In " + tableModel.getClass().getName());
			throw e;
		}
	}
	
	/**
	 * translate model index to view index
	 */
	public int viewIndex(int modelIndex)
	{
		return getModelToView()[modelIndex];
	}

	private int[] getModelToView()
	{
		synchronized (mappingLock) {
			if (modelToView == null)
			{
				// force the index to be built
				getViewToModel();
				// if we're not sorting, that may not have been enough
				if (modelToView == null)
				{
					modelToView = buildModelToView(viewToModel);
				}
			}
			return modelToView;
		}
	}

	// TableModel interface methods

	public int getRowCount()
	{
		return (tableModel == null) ? 0 : tableModel.getRowCount();
	}

	public int getColumnCount()
	{
		return (tableModel == null) ? 0 : tableModel.getColumnCount();
	}

	@Override
	public String getColumnName(int column)
	{
		return tableModel.getColumnName(column);
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		return tableModel.getColumnClass(column);
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return tableModel.isCellEditable(modelIndex(row), column);
	}

	/**
	 * Note: the row index is the "view" row index
	 */
	public Object getValueAt(int row, int column)
	{
		return tableModel.getValueAt(modelIndex(row), column);
	}

	@Override
	public void setValueAt(Object aValue, int row, int column)
	{
		tableModel.setValueAt(aValue, modelIndex(row), column);
	}

	// Helper classes

	private class Row implements Comparable<Object>
	{
		private final int modelIndex;

		public Row(int index)
		{
			this.modelIndex = index;
		}

		@SuppressWarnings("unchecked")
		public int compareTo(Object o)
		{
			final int row1 = modelIndex;
			final int row2 = ((Row) o).modelIndex;

			for (Directive directive : sortingColumns)
			{
				final int column = directive.column;
				final Object o1 = tableModel.getValueAt(row1, column);
				final Object o2 = tableModel.getValueAt(row2, column);

				final int comparison;
				// Define null less than everything, except null.
				if (o1 == null && o2 == null)
				{
					comparison = 0;
				}
				else if (o1 == null)
				{
					comparison = -1;
				}
				else if (o2 == null)
				{
					comparison = 1;
				}
				else
				{
					Object comp = getComparator(column);
					if (comp instanceof ITableColumnSorter) {
						ITableColumnSorter comp2 = (ITableColumnSorter) comp;
						comparison = comp2.compare(SorterTableModel.this, row1, row2, column);
					} else {
						comparison = ((Comparator) comp).compare(o1, o2);
					}
				}
				if (comparison != 0)
				{
					return directive.direction == SortDirection.DESCENDING ? -comparison : comparison;
				}
			}
			return 0;
		}
	}

	private class TableModelHandler implements TableModelListener
	{
		public void tableChanged(TableModelEvent e)
		{
			// If we're not sorting by anything, just pass the event along.
			if (!isSorting())
			{
				clearSortingState();
				fireTableChanged(e);
				return;
			}

			/* If the table structure has changed, cancel the sorting; the
			 * sorting columns may have been either moved or deleted from
			 * the model. */
			if (e.getFirstRow() == TableModelEvent.HEADER_ROW)
			{
				cancelSorting();
				fireTableChanged(e);
				return;
			}

			/* We can map a cell event through to the view without widening
			 * when the following conditions apply:
			 * 
			 * a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and,
			 * b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
			 * c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and,
			 * d) a reverse lookup will not trigger a sort (modelToView != null)
			 *
			 * Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
			 * 
			 * The last check, for (modelToView != null) is to see if modelToView
			 * is already allocated. If we don't do this check, sorting can become
			 * a performance bottleneck for applications where cells
			 * change rapidly in different parts of the table. If cells
			 * change alternately in the sorting column and then outside of
			 * it this class can end up re-sorting on alternate cell updates -
			 * which can be a performance problem for large tables. The last
			 * clause avoids this problem.
			 */
			final int column = e.getColumn();
			synchronized (mappingLock) {
				if (e.getFirstRow() == e.getLastRow() && column != TableModelEvent.ALL_COLUMNS
						&& getSortDirection(column) == SortDirection.NOT_SORTED && modelToView != null)
				{
					// note: we don't trigger a sort here because we already have the modelToView
					final int viewIndex = getModelToView()[e.getFirstRow()];
					fireTableChanged(new TableModelEvent(SorterTableModel.this, viewIndex, viewIndex, column, e.getType()));
					return;
				}
			}

			// Something has happened to the data that may have invalidated the row order.
			clearSortingState();
			fireTableDataChanged();
		}
	}

	private class MouseHandler extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			final JTableHeader h = (JTableHeader) e.getSource();
			final TableColumnModel columnModel = h.getColumnModel();
			final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			final int column = columnModel.getColumn(viewColumn).getModelIndex();
			if (column != -1)
			{
				if (sortabilityList != null && !sortabilityList[column])
				{
					return;
				}

				SortDirection status = getSortDirection(column);
				if (!e.isControlDown())
				{
					cancelSorting();
				}
				/* Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or
				 * {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed. */
				status = e.isShiftDown() ? status.prev() : status.next();
				setSortDirection(column, status);
			}
		}
	}

	private class SortableHeaderRenderer implements TableCellRenderer
	{
		private final TableCellRenderer tableCellRenderer;

		public SortableHeaderRenderer(TableCellRenderer tableCellRenderer)
		{
			this.tableCellRenderer = tableCellRenderer;
		}

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column)
		{
      final Component c = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (c instanceof JLabel)
			{
				final JLabel l = (JLabel) c;

				String direction = "";

				final Directive directive = getDirective(column);
				if (directive.direction == SortDirection.DESCENDING)
				{
					direction = DOWN_CHAR;
				}
				else if (directive.direction == SortDirection.ASCENDING)
				{
					direction = UP_CHAR;
				}
				
				l.setText(table.getColumnName(column) + direction);
			}
			return c;
		}
	}

	private static class Directive
	{
		public final int column;

		public final SortDirection direction;

		public Directive(int column, SortDirection direction)
		{
			this.column = column;
			this.direction = direction;
		}
	}
}