package com.peralex.utilities.ui.graphs.waterfallGraph;

import java.awt.Color;

/**
 * This class is used with a Waterfall graph to set a colour for
 * a specific value. This value can then be displayed or not.
 *
 * @author  Jaco Jooste
 */
public class ColourValueSet
{
	/**
	 * Specific Value to be checked for
	 */
	private float fValue = Float.MIN_VALUE;
	
	/**
	 * Display the Specific Value in the given colour
	 */
	private boolean bValueDisplayed = false;
	
	/**
	 * Display the Specific Value in this colour
	 */
	private Color oValueColour = null;
	
	/**
	 * Creates a new instance of cColourValueSet
	 */
	public ColourValueSet(float fValue, Color oValueColour, boolean bValueDisplayed)
	{
		this.fValue = fValue;
		this.oValueColour = oValueColour;
		this.bValueDisplayed = bValueDisplayed;
	}
	
	public ColourValueSet(float fValue, Color oValueColour)
	{
		this.fValue = fValue;
		this.oValueColour = oValueColour;
		this.bValueDisplayed = true;
	}
	
	/**
	 * Setter for property fValue.
	 *
	 * @param fValue
	 */
	public void setValue(float fValue)
	{
		this.fValue = fValue;
	}
	
	/**
	 * Getter for property fValue.
	 *
	 * @return fValue.
	 */
	public float getValue()
	{
		return fValue;
	}
	
	/**
	 * Getter for property bValueDisplayed.
	 *
	 * @return bValueDisplayed.
	 */
	public boolean isValueDisplayed()
	{
		return bValueDisplayed;
	}
	
	/**
	 * Setter for property bValueDisplayed.
	 *
	 * @param bValueDisplayed
	 */
	public void setValueDisplayed(boolean bValueDisplayed)
	{
		this.bValueDisplayed = bValueDisplayed;
	}
	
	/**
	 * Setter for property oValueColour.
	 *
	 * @param oValueColour
	 */
	public void setValueColour(java.awt.Color oValueColour)
	{
		this.oValueColour = oValueColour;
	}
	
	/**
	 * Getter for property oValueColour.
	 *
	 * @return oValueColour.
	 */
	public Color getValueColour()
	{
		return oValueColour;
	}
}
