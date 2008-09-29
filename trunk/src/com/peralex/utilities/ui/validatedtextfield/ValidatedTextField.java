package com.peralex.utilities.ui.validatedtextfield;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.TextAction;

/**
 * A number field component that validates its content.
 * 
 * This component acts mostly like a JFormattedTextField, but without the horrible complexity, and makes
 * it a lot easier to code text fields that need validation.
 * 
 * Notes:
 * Yes, this is perhaps a strange way of solving the problem of having a TextField where I can
 * validate the contents such that getValue() never returns me bad data, and it deals nicely with
 * focus traversal, etc.
 * But it's also the same way that JSpinner.NumberEditor solves this problem, so I don't feel so bad.
 * 
 * @author Noel Grandin
 */
public class ValidatedTextField extends JComponent {

	/**
	 * an example of the null-object pattern, so I don't have to test the model field
	 * for null.
	 */
	private static final ValidatedTextModel ALWAYS_VALID = new ValidatedTextModel() {
		
		public boolean isValid(String text)
		{
			return true;
		}
		public String objectToText(Object obj)
		{
			if (obj==null) {
				return "";
			} else {
				throw new IllegalStateException("cannot set a value on the default model - must configure ValidatedTextField with model first.");
			}
		}
		public Object textToObject(String text)
		{
			return null;
		}
		public Object getValue()
		{
			return null;
		}
		public void setValue(Object obj)
		{
			if (obj!=null) {
				throw new IllegalStateException(""+obj);
			}
		}
	};
	
	private final JTextField textField;

	private String lastValidText = "";
	
	private ValidatedTextModel model = ALWAYS_VALID;
	
	/**
	 * the default background when no error is displayed
	 */
	private Color defaultBackground;
	
	public ValidatedTextField()
	{
		this.textField = new JTextField();
		this.defaultBackground = textField.getBackground();
		
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e)
			{
				valueChanged();
			}
			public void insertUpdate(DocumentEvent e)
			{
				valueChanged();
			}
			public void removeUpdate(DocumentEvent e)
			{
				valueChanged();
			}
			private void valueChanged()
			{
				if (model==null) return;
				String s = textField.getText();
				if (model.isValid(s)) {
					setBackgroundNormal();
				} else {
					setBackgroundError();
				}
			}
		});

		// on losing focus, we do the equivalent of the JFormattedFieldText.COMMIT_OR_REVERT behaviour
		textField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e)
			{
			}
			public void focusLost(FocusEvent e)
			{
				if (e.isTemporary()) return;
				if (model==null) return;
				
				// if the value is not valid, revert it
				final String s = textField.getText();
				if (model.isValid(s)) {
					final Object newValue = model.textToObject(s);
					if (!newValue.equals(model.getValue())) {
						Object oldValue = model.getValue();
						model.setValue(newValue);
						lastValidText = s;
						ValidatedTextField.this.firePropertyChange("value", oldValue, newValue);
						fireActionPerformed();
					}
				} else {
					// revert
					setBackgroundNormal();
					textField.setText(lastValidText);
				}
			}
		});

		// this fires when the contents of the field are "accepted" e.g. when the user presses ENTER
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				final String s = textField.getText();
				if (model.isValid(s)) {
					final Object newValue = model.textToObject(s);
					if (!newValue.equals(model.getValue())) {
						Object oldValue = model.getValue();
						model.setValue(newValue);
						lastValidText = s;
						ValidatedTextField.this.firePropertyChange("value", oldValue, newValue);
						fireActionPerformed();
					}
				} else {
					UIManager.getLookAndFeel().provideErrorFeedback(ValidatedTextField.this);
				}
			}
		});
		
		// implement Cancel semantics like JFormattedTextField i.e. revert the contents when the user presses ESCAPE
		textField.getInputMap().put(KeyStroke.getKeyStroke("pressed ESCAPE"), "reset-field-edit");
		textField.getActionMap().put("reset-field-edit", new CancelAction());		
		
		setLayout(new BorderLayout());
		add(textField, BorderLayout.CENTER);
	}
	
	@Override
	public void setBackground(Color bg)
	{
		this.defaultBackground = bg;
		super.setBackground(bg);
		textField.setBackground(bg);
	}
	
	private void setBackgroundError()
	{
		fireValidChanged(false);
		textField.setBackground(Color.YELLOW);
	}

	private void setBackgroundNormal()
	{
		fireValidChanged(true);
		textField.setBackground(defaultBackground);
	}

	/**
	 * set the value for the text field.
	 * 
	 * @exception IllegalStateException if the value is invalid.
	 */
	public void setValue(Object obj) {
		final String text = model.objectToText(obj);
		if (!model.isValid(text)) {
			throw new IllegalStateException("value is not valid " + obj);
		}
		// make sure the value is of the correct type by converting it back
		// using the model.
		model.setValue(model.textToObject(text));
		lastValidText = text;
		textField.setText(lastValidText);
	}

	public Object getValue() {
		return model.getValue();
	}

	public void setModel(ValidatedTextModel model) {
		if (model==null) {
			throw new IllegalStateException("model may not be null");
		}
		this.model = model;

		lastValidText = model.objectToText(model.getValue());
		textField.setText(lastValidText);
	}
	
	public ValidatedTextModel getModel() {
		return this.model;
	}
	
	/**
	 * @see JTextField#setColumns
	 */
	public void setColumns(int columns) {
		textField.setColumns(columns);
	}
	
	/**
	 * @see JTextField#getColumns
	 */
	public int getColumns() {
		return textField.getColumns();
	}
	
	/**
	 * @see JTextField#setHorizontalAlignment
	 */
	public void setHorizontalAlignment(int alignment) {
		textField.setHorizontalAlignment(alignment);
	}
	
	/**
	 * @see JTextField#getHorizontalAlignment
	 */
	public int getHorizontalAlignment() {
		return textField.getHorizontalAlignment();
	}
	
	@Override
	public void setFont(Font font)
	{
		super.setFont(font);
		textField.setFont(font);
	}

	/**
	 * @see JTextField#setEditable(boolean)
	 */
	public void setEditable(boolean b)
	{
		textField.setEditable(b);
	}
	
	/**
	 * @see JTextField#isEditable()
	 */
	public boolean isEditable()
	{
		return textField.isEditable();
	}

	/**
	 * @see JTextField#setMargin(Insets)
	 */
	public void setMargin(Insets margin)
	{
		textField.setMargin(margin);
	}

	/**
	 * @see JTextField#getMargin()
	 */
	public Insets getMargin()
	{
		return textField.getMargin();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		textField.setEnabled(enabled);
	}
	
	public void addActionListener(ActionListener l) {
    listenerList.add(ActionListener.class, l);
	}
	
	public void removeActionListener(ActionListener l) {
    listenerList.remove(ActionListener.class, l);
	}
	
	private void fireActionPerformed()
	{
		ActionListener [] listeners = listenerList.getListeners(ActionListener.class);
		final ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, lastValidText);
		for (ActionListener l : listeners) {
			l.actionPerformed(evt);
		}
	}

	public void addValidationListener(IValidListener l) {
    listenerList.add(IValidListener.class, l);
	}
	
	public void removeValidationListener(IValidListener l) {
    listenerList.remove(IValidListener.class, l);
	}
	
	private void fireValidChanged(boolean valid)
	{
		IValidListener [] listeners = listenerList.getListeners(IValidListener.class);
		for (IValidListener l : listeners) {
			l.validChanged(valid);
		}
	}
	
	/**
	 * get the text field that this component wraps.
	 */
	public JTextField getTextField() {
		return this.textField;
	}
	
	/**
	 * Reset the value of the field when ESCAPE is pressed.
	 */
	private class CancelAction extends TextAction
	{
		public CancelAction()
		{
			super("reset-field-edit");
		}

		public void actionPerformed(ActionEvent e)
		{
			// revert
			setBackgroundNormal();
			textField.setText(lastValidText);
		}

		@Override
		public boolean isEnabled()
		{
			return true;
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ignoreEx)
		{
		}

		final ValidatedTextField blockControl = new ValidatedTextField();
		blockControl.setColumns(15);
		blockControl.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				System.out.println("value changed " + evt.getOldValue() + "  " + evt.getNewValue());
			}
		});
		blockControl.setModel(new FloatRangeTextModel(10, 100));

		final JFrame frame = new JFrame("ValidTextField Test Frame");
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(blockControl);
		frame.getContentPane().add(new JButton("Just for show"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}