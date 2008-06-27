package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;

/**
 * FIXME This class should probably not be exposed. It should probably be an internal implementation class, 
 *  with a bunch of setter and getter methods on cRangeCursorDrawSurface, which would allow to trigger repaints
 *  properly when values change.
 *
 * @author  Andre
 */
public class RangeCursor
{
	
	/**
	 * This stores the ID of this RangeCursor.
	 */
	private final String sRangeCursorID;
	
	/**
	 * This stores the Color of this RangeCursor.
	 */
	private Color oColor;
	
	/**
	 * Stores the current value of the RangeCursor.
	 */
	private float fValue;
	
	/**
	 * Stores the width of the RangeCursor.
	 */
	private float fWidth;
	
	/**
	 * Stores the current Coordinate of the RangeCursor.
	 */
	private int iCoordinate;
	
	/**
	 * Stores the current PixelWidth of the RangeCursor.
	 */
	private int iPixelWidth;
	
	/**
	 * This stores the resolutions at which this RangeCursor will snap.
	 */
	private float fResolution = 1;
	
	/**
	 * Stores the labels for the RangeCursor.
	 */
	private String sLabel = "";
	
	/**
	 * Stores a reference to the RangeCursorDrawSurface.
	 */
	private RangeCursorDrawSurface oRangeCursorDrawSurface;
	
	/**
	 * Stores the current Coordinates of the cursor's labels.
	 */
	private int iCursorLabelXCoordinate, iCursorLabelYCoordinate;
	
	/**
	 * This indicates whether the Cursor is Enabled.
	 */
	private boolean bRangeCursorEnabled = true;
	
	
	/**
	 * Create a new RangeCursor
	 */
	public RangeCursor(String sRangeCursorID, Color oColor, float fResolution, float fWidth, float fValue)
	{
		this.sRangeCursorID = sRangeCursorID;
		this.fWidth = fWidth;
		this.fResolution = fResolution;
		this.oColor = oColor;
		
		this.fValue = fValue;
		snapToResolution();
	}
	
	/**
	 * Snap the RangeCursor to the nearest resolution.
	 */
	private void snapToResolution()
	{
		if (fResolution!=1.0) {
			// note: cannot use Math.round here because round() returns a long, and a long may not
			// have sufficient range for fValue.
			this.fValue = (float) (Math.floor(fValue / fResolution) * fResolution);
		}
	}
	
	/**
	 * Getter for property iRangeCursorID.
	 */
	public String getRangeCursorID()
	{
		return sRangeCursorID;
	}
	
	/**
	 * Sets the new value of the RangeCursor.
	 */
	public void setValue(float fValue)
	{
		this.fValue = fValue;
		
		snapToResolution();
		oRangeCursorDrawSurface.calculateRangeCursors();
	}
	
	/**
	 * Returns the current Value.
	 */
	public float getValue()
	{
		return fValue;
	}
	
	public void setWidth(float fWidth)
	{
		this.fWidth = fWidth;
	}
	
	public float getWidth()
	{
		return fWidth;
	}
	
	/**
	 * This sets the label of this RangeCursor.
	 */
	public void setLabel(String sLabel)
	{
		this.sLabel = sLabel;
		oRangeCursorDrawSurface.calculateRangeCursors();
	}
	
	/**
	 * Returns the current Value of the label.
	 */
	public String getLabel()
	{
		return sLabel;
	}
	
	/**
	 * This sets the color of the RangeCursor.
	 */
	public void setColor(Color oColor)
	{
		this.oColor = oColor;
	}
	
	/**
	 * This returns the color of the RangeCursor.
	 */
	public Color getColor()
	{
		return oColor;
	}
	
	public void setResolution(float fResolution)
	{
		this.fResolution = fResolution;
	}
	
	public float getResolution()
	{
		return fResolution;
	}
	
	public void setCoordinate(int iCoordinate)
	{
		this.iCoordinate = iCoordinate;
	}
	
	public int getCoordinate()
	{
		return iCoordinate;
	}
	
	public void setPixelWidth(int iPixelWidth)
	{
		if (iPixelWidth == 0)
		{
			this.iPixelWidth = 1;
		}
		else
		{
			this.iPixelWidth = iPixelWidth;
		}
	}
	
	public int getPixelWidth()
	{
		return iPixelWidth;
	}
	
	public void setCursorLabelCoordinates(int iCursorLabelXCoordinate, int iCursorLabelYCoordinate)
	{
		this.iCursorLabelXCoordinate = iCursorLabelXCoordinate;
		this.iCursorLabelYCoordinate = iCursorLabelYCoordinate;
	}
	
	public int getCursorLabelXCoordinate()
	{
		return iCursorLabelXCoordinate;
	}
	
	public int getCursorLabelYCoordinate()
	{
		return iCursorLabelYCoordinate;
	}
	
	void setRangeCursorDrawSurface(RangeCursorDrawSurface rangeCursorDrawSurface)
	{
		oRangeCursorDrawSurface = rangeCursorDrawSurface;
	}
		
	public void setRangeCursorEnabled(boolean bRangeCursorEnabled)
	{
		this.bRangeCursorEnabled = bRangeCursorEnabled;
		oRangeCursorDrawSurface.repaint();
	}	
	
	public boolean isRangeCursorEnabled()
	{
		return bRangeCursorEnabled;
	}
	
}
