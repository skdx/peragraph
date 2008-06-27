package com.peralex.utilities.ui.validatedtextfield;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextField;

/**
 * BeanInfo class for ValidatedTextField. Makes GUI editing of this component easier.
 * 
 * @author Noel Grandin
 */
public class ValidatedTextFieldBeanInfo extends SimpleBeanInfo
{

	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			final PropertyDescriptor[] tmpResults = Introspector.getBeanInfo(ValidatedTextField.class,
					Introspector.IGNORE_IMMEDIATE_BEANINFO).getPropertyDescriptors();
			final List<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>(Arrays.asList(tmpResults));

			final PropertyDescriptor horizontalAlignment = new PropertyDescriptor("horizontalAlignment",
					ValidatedTextField.class);
			horizontalAlignment.setValue("enumerationValues", new Object[] { 
					"LEFT", JTextField.LEFT, "javax.swing.JTextField.LEFT", 
					"CENTER", JTextField.CENTER, "javax.swing.JTextField.CENTER", 
					"RIGHT", JTextField.RIGHT, "javax.swing.JTextField.RIGHT", 
					"LEADING", JTextField.LEADING, "javax.swing.JTextField.LEADING", 
					"TRAILING", JTextField.TRAILING, "javax.swing.JTextField.TRAILING", 
			});
			list.add(horizontalAlignment);

			return list.toArray(new PropertyDescriptor[list.size()]);
		}
		catch (IntrospectionException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

}
