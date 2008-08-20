package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;

/**
 *
 * @author Andre
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
	private double fValue;
	
	/**
	 * Stores the width of the RangeCursor.
	 */
	private double fWidth;
	
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
	public RangeCursor(String sRangeCursorID, Color oColor, float fResolution, double fWidth, double fValue)
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
			this.fValue = Math.floor(fValue / fResolution) * fResolution;
		}
	}
	
	public String getRangeCursorID()
	{
		return sRangeCursorID;
	}
	
	/**
	 * Sets the new value of the RangeCursor.
	 */
	public void setValue(double fValue)
	{
		this.fValue = fValue;
		
		snapToResolution();
		oRangeCursorDrawSurface.invalidateRangeCursors();
	}
	
	/**
	 * Returns the current Value.
	 */
	public double getValue()
	{
		return fValue;
	}
	
	public void setWidth(double fWidth)
	{
		this.fWidth = fWidth;
		oRangeCursorDrawSurface.invalidateRangeCursors();
	}
	
	public double getWidth()
	{
		return fWidth;
	}
	
	/**
	 * This sets the label of this RangeCursor.
	 */
	public void setLabel(String sLabel)
	{
		this.sLabel = sLabel;
		oRangeCursorDrawSurface.invalidateRangeCursors();
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
		oRangeCursorDrawSurface.repaint();
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
		snapToResolution();
		oRangeCursorDrawSurface.invalidateRangeCursors();
	}
	
	public float getResolution()
	{
		return fResolution;
	}
	
	/**
	 * package-protected because setting it from outside this package is useless - RangeCursorDrawSurface
	 * will reset it anyway.
	 */
	void setCoordinate(int iCoordinate)
	{
		this.iCoordinate = iCoordinate;
	}
	
	public int getCoordinate()
	{
		return iCoordinate;
	}

	/**
	 * package-protected because setting it from outside this package is useless - RangeCursorDrawSurface
	 * will reset it anyway.
	 */
	void setPixelWidth(int iPixelWidth)
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
	
	/**
	 * package-protected because setting it from outside this package is useless - RangeCursorDrawSurface
	 * will reset it anyway.
	 */
	void setCursorLabelCoordinates(int iCursorLabelXCoordinate, int iCursorLabelYCoordinate)
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
