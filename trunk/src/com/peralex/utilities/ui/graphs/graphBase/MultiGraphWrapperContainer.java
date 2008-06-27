package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * This code wraps multiple cMultiGraphWrapper classes so that they all display nicely lined up vertically.
 * 
 * @author Noel Grandin
 */
public class MultiGraphWrapperContainer extends JComponent
{
	private final Set<MultiGraphWrapper> wrappers = new HashSet<MultiGraphWrapper>();

	public MultiGraphWrapperContainer()
	{
		super.setLayout(new GridBagLayout());
	}
	
	@Override
	public final void setLayout(LayoutManager mgr)
	{
		throw new IllegalStateException("cannot change this layout manager");
	}
	
	final void add(MultiGraphWrapper wrapper)
	{
		if (wrappers.contains(wrapper)) throw new IllegalStateException("already added!");
		
		wrappers.add(wrapper);
	}
	
	int getMaxGridY()
	{
		final GridBagLayout layout = (GridBagLayout) getLayout();
		int gridY = -1;
		for (int i=0; i<getComponentCount(); i++)
		{
			gridY = Math.max(gridY, layout.getConstraints(getComponent(i)).gridy);
			
		}
		return gridY;
	}
	
	public void addHorizontalSeparator()
	{
    final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = getMaxGridY() + 1;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    
    add(new JSeparator(SwingConstants.HORIZONTAL), gridBagConstraints);
	}
}
