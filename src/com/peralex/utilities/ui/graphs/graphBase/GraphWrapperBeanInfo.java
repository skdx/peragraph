package com.peralex.utilities.ui.graphs.graphBase;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Beaninfo class for cGraphWrapper. Makes GUI editing of this component easier.
 * 
 * @author Noel Grandin
 */
public class GraphWrapperBeanInfo extends SimpleBeanInfo
{

	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			final PropertyDescriptor [] tmpResults = Introspector.getBeanInfo(GraphWrapper.class, Introspector.IGNORE_IMMEDIATE_BEANINFO).getPropertyDescriptors();
			List<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>(Arrays.asList(tmpResults));
			
			// remove the properties we don't want visible in a GUI editor
			for (Iterator<PropertyDescriptor> iter=list.iterator(); iter.hasNext(); )
			{
				final PropertyDescriptor pd = iter.next();
				// remove some properties because they are either deprecated, or we have a getter without a setter
				if (pd.getName().equals("XAxisScaleEnabled")
						|| pd.getName().equals("YAxisScaleEnabled")
						|| pd.getName().equals("axisScalesEnabled")
						|| pd.getName().equals("XAxisExtraPanel")
						|| pd.getName().equals("cursorCoordinatesEnabled")
						|| pd.getName().equals("headerBottomExtraPanel")
						|| pd.getName().equals("headerLeftExtraPanel")
						|| pd.getName().equals("headerRightExtraPanel")
						|| pd.getName().equals("topLeftExtraPanel"))
				{
					iter.remove();
				}
				// remove some because they are utility "composite" methods i.e. they actually set multiple properties
				if (pd.getName().equals("axisScalesVisible")
					|| pd.getName().equals("axisLabelsVisible"))
				{
					iter.remove();
				}
				// remove some because they can only be called once the graph component has been set
				if (pd.getName().equals("XAxisVisible")
					|| pd.getName().equals("YAxisVisible"))
				{
					iter.remove();
				}
		}
			
			return list.toArray(new PropertyDescriptor[list.size()]);
		}
		catch (IntrospectionException exc)
		{
			exc.printStackTrace();
			return null;
		}
	}

}
