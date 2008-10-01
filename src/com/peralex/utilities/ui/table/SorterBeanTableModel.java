package com.peralex.utilities.ui.table;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

/**
 * Combine a BeanTableModel with SorterTableModel capabilities.
 * 
 * @author Noel Grandin
 */
public class SorterBeanTableModel<T> extends SorterTableModel
{

	private final BeanTableModel<T> beanModel;
	
	/**
	 * 
	 * @param beanClass class for row objects, all fields in this class are added as columns
	 */
	public SorterBeanTableModel(JTable oTable, Class<T> beanClass)
	{
		beanModel = new BeanTableModel<T>(beanClass);
		
		final JTableHeader tableHeader = oTable.getTableHeader();
		tableHeader.setResizingAllowed(true);
		tableHeader.setReorderingAllowed(false);

		setTableModel(beanModel);
		setTableHeader(tableHeader);
		oTable.setModel(this);
	}
	
	/**
	 * 
	 * @param beanClass this is here so we can check the field names and fail early if there is a mistake.
	 * @param columnBeanFields
	 */
	public SorterBeanTableModel(JTable oTable, Class<T> beanClass, String... columnBeanFields)
	{
		beanModel = new BeanTableModel<T>(beanClass, columnBeanFields);
		
		final JTableHeader tableHeader = oTable.getTableHeader();
		tableHeader.setResizingAllowed(true);
		tableHeader.setReorderingAllowed(false);

		setTableModel(beanModel);
		setTableHeader(tableHeader);
		oTable.setModel(this);
	}
	
	protected void setColumnName(String beanFieldName, String columnName)
	{
		beanModel.setColumnName(beanFieldName, columnName);
	}
	
	/**
	 * returns a non-modifiable List view of the bean data
	 */
	public List<T> values()
	{
		return beanModel.values();
	}

	public void addRow(T newRow)
	{
		beanModel.addRow(newRow);
	}

	/**
	 * Note: the row index is the "view" row index
	 */
	public void insertRow(int rowIndex, T newRow)
	{
		beanModel.insertRow(modelIndex(rowIndex), newRow);
	}

	/**
	 * Note: the row index is the "view" row index
	 */
	public T removeRow(int rowIndex)
	{
		return beanModel.removeRow(modelIndex(rowIndex));
	}

	/**
	 * Remove a group of rows. More efficient and more safe than coding it yourself.
	 * 
	 * @param rowIndices array of row indices - this array must be sorted, and must not contain duplicates
	 */
	public void removeRows(int ...rowIndices)
	{
		if (rowIndices==null) return;
		
		int [] modelIndices = new int[rowIndices.length];
		for (int i=0; i<rowIndices.length; i++) {
			modelIndices[i] = modelIndex(rowIndices[i]);
		}
		Arrays.sort(modelIndices);
		beanModel.removeRows(modelIndices);
	}
	
	public T removeRowByModelIndex(int modelIndex)
	{
		return beanModel.removeRow(modelIndex);
	}
	
	/**
	 * Note: the row index is the "view" row index
	 */
	public void updateRow(int rowIndex, T updatedRow)
	{
		beanModel.updateRow(modelIndex(rowIndex), updatedRow);
	}
	
	/**
	 * Note: the row index is the "view" row index
	 */
	public T getRow(int rowIndex)
	{
		return beanModel.getRow(modelIndex(rowIndex));
	}

	/**
	 * Note: the row index is the "model" row index
	 * This method is a special-case method for use inside ITableColumnSorters.
	 * @see ITableColumnSorter
	 */
	public T getRowByModelIndex(int modelIndex)
	{
		return beanModel.getRow(modelIndex);
	}
	
	/**
	 * clears existing data and copies new data from parameter
	 */
	public void setData(Collection<T> data)
	{
		beanModel.setData(data);
	}

	/**
	 * clear existing rows
	 */
	public void removeAll()
	{
		beanModel.removeAll();
	}
	
	public void setColumnEditable(int columnIndex, boolean editable)
	{
		beanModel.setColumnEditable(columnIndex, editable);
	}
	
	public void setColumnEditable(String beanFieldName, boolean editable)
	{
		beanModel.setColumnEditable(beanFieldName, editable);
	}
	
	/**
	 * Get the view index of a row.
	 * Useful for setting selected rows.
	 * @return -1 if we found nothing
	 */
	public int indexOf(T row)
	{
		int x = beanModel.indexOf(row);
		if (x==-1) {
			return x;
		}
		return viewIndex(x);
	}

}
