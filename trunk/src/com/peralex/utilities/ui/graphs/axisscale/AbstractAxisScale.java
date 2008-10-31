package com.peralex.utilities.ui.graphs.axisscale;

import javax.swing.JPanel;

import com.peralex.utilities.ui.graphs.graphBase.GridDrawSurface;

/**
 * API that GridDrawSurface needs from an axis component.
 * 
 * Note - the only reason I extend JPanel is so that I get a reasonable default font - JComponent does
 * not have a default font.
 * It would be better if I could make this an interface, but too many things depend on it
 * being a subclass of JComponent.
 * 
 * @author Noel Grandin
 */
public abstract class AbstractAxisScale extends JPanel
{

	/**
	 * Clear the current collection of labels.
	 */
	public abstract void clear();
	
	/**
	 * Adds the label value and its x position.
	 */
	public abstract void addLabel(int iPosition, double fLabelValue);
	
	/**
	 * Add a label that will be draw at the end of the axis.
	 * This calculates the necessary pixel offset so that the label is visible.
	 */
	public abstract void addLabelAtEnd(double fLabelValue);
	
	/**
	 * format a value the same way a label is formatted.
	 */
	public abstract String formatValueAsLabel(double fLabelValue);
	
	/**
	 * this mode is only meant to be turned on when AbstractAxisScale is used with GraphWrapper.
	 * It normally only applies to the x-axis.
	 */
	public abstract void setOffsetFirstLabel(boolean b);
	
	public abstract void linkToX(GridDrawSurface surface);

	public abstract void linkToY(GridDrawSurface surface);
}
