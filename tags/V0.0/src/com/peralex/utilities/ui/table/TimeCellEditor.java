package com.peralex.utilities.ui.table;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.tree.TreeCellEditor;

/**
 * A CellEditor for java.util.Date that displays only the time portion of the value.
 * 
 * It also has the useful side-effect that it preserves the year/month/days value in the input.
 * 
 * @author Noel Grandin
 */
public class TimeCellEditor implements TableCellEditor, TreeCellEditor
{
	private static final Border BORDER_DEFAULT = new LineBorder(Color.black);
	private static final Border BORDER_ERROR = new LineBorder(Color.red);
	
	/* Normally I would subclass DefaultCellEditor, but because I need to construct
	 * other stuff before calling the DefaultCellEditor constructor, I have to delegate
	 * the functionality.
	 */
	private final DefaultCellEditor delegate;

	/**
	 * Override the setValue() method so that when the user hits ESCAPE to revert to the original
	 * value, we can clear the error border.
	 */
	private static class MyFormattedTextField extends JFormattedTextField
	{
		@Override
		public void setValue(Object value)
		{
			super.setValue(value);
			if (isEditValid())
			{
				setBorder(BORDER_DEFAULT);
			}
		}
	}
	
	public TimeCellEditor()
	{
		this.delegate = new DefaultCellEditor(new MyFormattedTextField());

		final JFormattedTextField ftf = (JFormattedTextField) delegate.getComponent();

		ftf.setFormatterFactory(new DefaultFormatterFactory(new TimeFormatter()));
		ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

		/*
		 * React when the user presses Enter while the editor is active.
		 * (Tab is handled as specified by JFormattedTextField's focusLostBehavior property).
		 */
		ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
		ftf.getActionMap().put("check", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				// if the text is invalid
				if (!ftf.isEditValid())
				{
					ftf.setBorder(BORDER_ERROR);
				}
				else // The text is valid
				{
					ftf.setBorder(BORDER_DEFAULT); // reset the border
					try {
						ftf.commitEdit(); // use the value
					} catch (ParseException ex)
					{
						ex.printStackTrace();
					}
					ftf.postActionEvent();
				}
			}
		});
	}

	public boolean stopCellEditing()
	{
		JFormattedTextField ftf = (JFormattedTextField) delegate.getComponent();
		if (ftf.isEditValid())
		{
			try
			{
				ftf.commitEdit();
			}
			catch (java.text.ParseException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		// text is invalid
		{
			ftf.setBorder(BORDER_ERROR);
			return false; // don't let the editor go away
		}
		/*
		 * call the superclass method, which will fire an editting stopped event and return true.
		 */
		return delegate.stopCellEditing();
	}

	/** Override to 
	 * (a) reset border on the formatted text field.
	 * (b) set the value directly since the DefaultCellEditor class will call toString() before passing
	 *    the data to the JFormattedTextField.
	 */
	public JFormattedTextField getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		final JFormattedTextField ftf = (JFormattedTextField) delegate.getComponent();
		ftf.setValue(value);
		// reset the border
		ftf.setBorder(BORDER_DEFAULT);
		return ftf;
	}
	
	/** Override to 
	 * (a) reset border on the formatted text field.
	 * (b) set the value directly since the DefaultCellEditor class will call toString() before passing
	 *    the data to the JFormattedTextField.
	 */
	public JFormattedTextField getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
	{
		final JFormattedTextField ftf = (JFormattedTextField) delegate.getComponent();
		ftf.setValue(value);
		// reset the border
		ftf.setBorder(BORDER_DEFAULT);
		return ftf;
	}

	/** Override to ensure that the value remains of type java.util.Date. */
	public java.util.Date getCellEditorValue()
	{
		final JFormattedTextField ftf = (JFormattedTextField) delegate.getComponent();
		java.util.Date date = (java.util.Date) ftf.getValue();
		return date;
	}
	
	public void addCellEditorListener(CellEditorListener l)
	{
		delegate.addCellEditorListener(l);
	}
	
	public void cancelCellEditing()
	{
		delegate.cancelCellEditing();
	}
	
	public boolean isCellEditable(EventObject anEvent)
	{
		return delegate.isCellEditable(anEvent);
	}
	
	public void removeCellEditorListener(CellEditorListener l)
	{
		delegate.removeCellEditorListener(l);
	}
	
	public boolean shouldSelectCell(EventObject anEvent)
	{
		return delegate.shouldSelectCell(anEvent);
	}
}