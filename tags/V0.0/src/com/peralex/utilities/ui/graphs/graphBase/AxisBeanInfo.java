package com.peralex.utilities.ui.graphs.graphBase;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import javax.swing.JComponent;

/**
 * Beaninfo class for cAxis. Makes GUI editing of this component easier.
 * 
 * @author Noel Grandin
 */
public class AxisBeanInfo extends SimpleBeanInfo
{

	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			// use the introspector to get a list of JComponent's properties
			final PropertyDescriptor [] superclassResults = Introspector.getBeanInfo(JComponent.class).getPropertyDescriptors();
			final PropertyDescriptor[] result = new PropertyDescriptor[superclassResults.length+5];
			System.arraycopy(superclassResults, 0, result, 0, superclassResults.length);
			int idx = superclassResults.length;
			
			PropertyDescriptor orientationDescriptor = new PropertyDescriptor("orientation", AxisScale.class, "getOrientation", "setOrientation");
			orientationDescriptor.setValue("enumerationValues", new Object[] { 
					"X_AXIS", Integer.valueOf(AxisScale.X_AXIS), "com.peralex.utilities.ui.graphs.graphBase.cAxis.X_AXIS",
					"Y_AXIS", Integer.valueOf(AxisScale.Y_AXIS), "com.peralex.utilities.ui.graphs.graphBase.cAxis.Y_AXIS",
			});
			result[idx++] = orientationDescriptor;
			
			result[idx++] = new PropertyDescriptor("labelColor", AxisScale.class, "getLabelColor", "setLabelColor");
			result[idx++] = new PropertyDescriptor("labelFont", AxisScale.class, "getLabelFont", "setLabelFont");
			result[idx++] = new PropertyDescriptor("offsetFirstLabel", AxisScale.class, "isOffsetFirstLabel", "setOffsetFirstLabel");
			result[idx++] = new PropertyDescriptor("drawLabels", AxisScale.class, "isDrawLabels", "setDrawLabels");
			
			return result;
		}
		catch (IntrospectionException exc)
		{
			exc.printStackTrace();
			return null;
		}
	}

}
