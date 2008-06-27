package com.peralex.utilities.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A lightly modified spinner class that only fires an update if the user has stopped making changes for 300ms.
 * 
 * @author Noel Grandin
 */
public class DelayedSpinner extends JSpinner
{
	
	private boolean bDontFire = false;
	
	public DelayedSpinner()
	{
		initTimer();
	}

	public DelayedSpinner(SpinnerModel model)
	{
		super(model);
		initTimer();
	}

	private void initTimer()
	{
		final Timer timer = new Timer(300, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				firePropertyChange("timedValue", null, getValue());
			}
		});
		timer.setRepeats(false);
		
		addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (bDontFire) return;
				
				timer.stop();
				timer.start();
			}
		});
	}

	/**
	 * add a listener that will fire when the value has stopped changing.
	 */
	public void addDelayedListener(final PropertyChangeListener listener)
	{
		addPropertyChangeListener("timedValue", listener);
	}
	
	public void removeDelayedListener(PropertyChangeListener listener)
	{
		removePropertyChangeListener("timedValue", listener);
	}
	
	/**
	 * set the value without firing the "timedValue" change listener
	 */
	public void setValueWithoutFire(Object value)
	{
		bDontFire = true;
		setValue(value);
		bDontFire = false;
	}
	
	/**
	 * set the model without firing the "timedValue" change listener
	 */
	public void setModelWithoutFire(SpinnerModel model)
	{
		bDontFire = true;
		setModel(model);
		bDontFire = false;
	}
}
