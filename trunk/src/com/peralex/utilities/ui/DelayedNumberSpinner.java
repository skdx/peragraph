package com.peralex.utilities.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.peralex.sharedlibs.dsphostl.ParameterRangeShort;

/**
 * A TimedSpinner that provides the kind of basic editing we need for numbers.
 * It performs validation on the text entry.
 * 
 * @author Noel Grandin
 */
public class DelayedNumberSpinner extends DelayedSpinner
{

	public DelayedNumberSpinner()
	{
		initEditor();
	}

	public DelayedNumberSpinner(SpinnerNumberModel model)
	{
		super(model);
		initEditor();
	}

	/**
	 * reset the model with new ranges
	 */
	public void setRange(ParameterRangeShort oParamRange)
	{
    setModel(new SpinnerNumberModel(oParamRange.getMin(),
        oParamRange.getMin(),
        oParamRange.getMax(),
        oParamRange.getIncrement()));
	}
	
	private void initEditor()
	{
		((JSpinner.NumberEditor) getEditor()).getFormat().setGroupingSize(0);
		
		// a key listener to ensure that no invalid text can be entered
		((JSpinner.NumberEditor)getEditor()).getTextField().addKeyListener(new java.awt.event.KeyAdapter()
		{
			private KeyEvent keyEvent;
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt)
			{
				keyEvent = evt;
				oKeyReleasedTimer.stop();
				oKeyReleasedTimer.start();
			}
			// this ensures that the key listener give the user enough time to enter text
			private final Timer oKeyReleasedTimer = new Timer(500, new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					oKeyReleasedTimer.stop();
					spinnerKeyReleased(keyEvent);
				}
			});
		});
	}

	/**
	 * Override setModel() and redo the editor initialisation
	 * because changing the model also changes the editor.
	 */
	@Override
	public void setModel(SpinnerModel model)
	{
		super.setModel(model);
		initEditor();
	}

	private void spinnerKeyReleased(KeyEvent evt)
	{
		final JFormattedTextField textField = ((JFormattedTextField) evt.getSource());
		final SpinnerNumberModel model = (SpinnerNumberModel) getModel();

		if (textField.getText().equals("-"))
		{
			return;
		}
		else if (textField.getText().equals(""))
		{
			return;
		}

		validateNumber(textField, model);
	}

	@SuppressWarnings("unchecked")
	private static void validateNumber(final JFormattedTextField textField, final SpinnerNumberModel model)
	{
		final Comparable textValue;

		try
		{
			if (model.getValue() instanceof Integer)
			{
				textValue = Integer.valueOf(textField.getText());
			}
			else if (model.getValue() instanceof Long)
			{
				textValue = Long.valueOf(textField.getText());
			}
			else if (model.getValue() instanceof Float)
			{
				textValue = Float.valueOf(textField.getText());
			}
			else if (model.getValue() instanceof Double)
			{
				textValue = Double.valueOf(textField.getText());
			}
			else
			{
				throw new IllegalStateException("unknown class " + model.getValue().getClass());
			}
		}
		catch (NumberFormatException nfe)
		{
			// if not a valid value, reset the editor to the model's value
			textField.setText(model.getValue().toString());
			model.setValue(model.getValue());
			return;
		}

		// if value less than minimum
		if (textValue.compareTo(model.getMaximum())==1)
		{
			textField.setText(model.getValue().toString());
			model.setValue(model.getMaximum());
			return;
		}
		// if value greater than maximum
		if (textValue.compareTo(model.getMinimum())==-1)
		{
			textField.setText(model.getValue().toString());
			model.setValue(model.getMinimum());
			return;
		}
	}
	
  public static void main(String[] args)
  {
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
		catch (Exception ignoreEx) {}
		
    final DelayedNumberSpinner blockControl = new DelayedNumberSpinner(new SpinnerNumberModel(10, -100, 100, 1));
    
    final JFrame frame = new JFrame("cDelayedNumberSpinner Test Frame");
    frame.getContentPane().add(blockControl);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setSize(400, 300);
    frame.setVisible(true);
  }
}
