package com.peralex.utilities.ui.table;


/**
 * Optional interface for custom table row sorters.
 * 
 * This is sometimes useful when the sorting code depends on more data than just the cell value.
 * 
 * WARNING: you can't call the normal getValueAt() from the comparator
 *   because it's not stable while we're sorting.
 *   If you are using SorterBeanTableModel, you can call getRowByModelIndex.
 *   
 * 
 * @author Noel Grandin
 */
public interface ITableColumnSorter
{
	/**
	 * @return same values as java.util.Comparator#compare
	 */
	int compare(SorterTableModel tableModel, int modelRowIndex1, int modelRowIndex2, int column);
}
