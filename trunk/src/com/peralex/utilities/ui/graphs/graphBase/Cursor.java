package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;

/**
 * FIXME This class should probably not be exposed. It should probably be an internal implementation class, 
 *  with a bunch of setter and getter methods on cCursorDrawSurface, which would allow to trigger repaints
 *  properly when values change.
 * 
 * @author Andre
 */
public class Cursor
{
  
  /**
   * This stores the ID of this cursor.
   */
  private final String sCursorID;
  
  /**
   * This stores the KeyEvent.VK_* keycode of this cursor.
   * -1 is "no key pressed"
   * -2 means "not positionable"
   */
  private int iKeyBinding;
  
  /**
   * This stores the Color of this cursor.
   */
  private final Color oCursorColor;
  
  /**
   * Stores the current values.
   */
  private double dXValue, dYValue;
  
  /**
   * Stores the current Coordinates of the cursor.
   * FIXME - we either shouldn't store these here, or we should make them package-private.
   *   The values are not calculated until repaint time, which means that making them visible to client code can
   *   result in hard to find bugs.
   */
  private int iXCoordinate, iYCoordinate;  
  
  /**
   * This stores the resolutions at which this cursor will snap.
   */
  private float fXResolution = 1,  fYResolution = 1;
  
  /**
   * Stores the labels for the cursors.
   */
  private String sXLabel = "", sYLabel = "";
  
  /**
   * Stores the current Coordinates of the Horizontal cursor's labels.
   * FIXME - we either shouldn't store these here, or we should make them package-private.
   *   The values are not calculated until repaint time, which means that making them visible to client code can
   *   result in hard to find bugs.
   */
  private int iHorizontalCursorLabelXCoordinate, iHorizontalCursorLabelYCoordinate;
  
  /**
   * Stores the current Coordinates of the Vertical cursor's labels.
   * FIXME - we either shouldn't store these here, or we should make them package-private.
   *   The values are not calculated until repaint time, which means that making them visible to client code can
   *   result in hard to find bugs.
   */
  private int iVerticalCursorLabelXCoordinate, iVerticalCursorLabelYCoordinate;  
  
	/**
	 * Stores a reference to the CursorDrawSurface.
	 */
	private final CursorDrawSurface oCursorDrawSurface;
	
  /**
   * This indicates whether the Horizontal Cursor is Enabled.
   */
  private boolean bHorizontalCursorEnabled = true;
  
  /**
   * This indicates whether the Vertical Cursor is Enabled.
   */
  private boolean bVerticalCursorEnabled = true;	

	
  /**
   * Create a new Cursor
   * @param iKeyBinding the KeyEvent.VK_* code of the keybinding to position the cursor, -1 is "no key pressed", -2 is "not positionable"
   */
  public Cursor(CursorDrawSurface oCursorDrawSurface, String sCursorID, int iKeyBinding, Color oCursorColor, float fXResolution, float fYResolution, double dXValue, double dYValue, boolean bHorizontalCursorEnabled, boolean bVerticalCursorEnabled)
  {
		this.oCursorDrawSurface = oCursorDrawSurface;
    this.sCursorID = sCursorID;
    this.iKeyBinding = iKeyBinding;
    this.oCursorColor = oCursorColor;
    this.fXResolution = fXResolution;
    this.fYResolution = fYResolution;
		this.bHorizontalCursorEnabled = bHorizontalCursorEnabled;
		this.bVerticalCursorEnabled = bVerticalCursorEnabled;
    
    setValue(dXValue, dYValue);
  }	
  
  /**
   * Sets the new value of the cursor.
   */
  public final void setValue(double dXValue, double dYValue)
  {
		this.dXValue = dXValue;
		this.dYValue = dYValue;
    
    snapToResolution(this.dXValue, this.dYValue);
		oCursorDrawSurface.invalidateCursors();
	}
  
  public final void setXValue(double dXValue)
  {
		this.dXValue = dXValue;
    
    snapToResolution(this.dXValue, this.dYValue);
		oCursorDrawSurface.invalidateCursors();
  }
  
  public final void setYValue(double dYValue)
  {
		this.dYValue = dYValue;
    
    snapToResolution(this.dXValue, this.dYValue);
		oCursorDrawSurface.invalidateCursors();
  }
  
  /**
   * This will move the Horizontal cursor up by one resolution value
   */
  public void moveUp()
  {
    setValue(dXValue, dYValue + fYResolution);
  }
  
  /**
   * This will move the Horizontal cursor down by one resolution value
   */
  public void moveDown()
  {
    setValue(dXValue, dYValue - fYResolution);
  }
  
  /**
   * This will move the Vertical cursor left by one resolution value
   */
  public void moveLeft()
  {
    setValue(dXValue - fXResolution, dYValue);
  }
  
  /**
   * This will move the Vertical cursor right by one resolution value
   */
  public void moveRight()
  {
    setValue(dXValue + fXResolution, dYValue);
  }
  
  /**
   * Snap the cursor to the nearest resolution.
   */
  private void snapToResolution(double _fXValue, double _fYValue)
  {
    this.dXValue = snap(_fXValue, fXResolution);
    this.dYValue = snap(_fYValue, fYResolution);
  }
  
  private static double snap(double val, float resolution)
  {
  	// weirdly, this code seems to work better than using BigDecimal or doing
  	// the usual Math.round(f/resolution)*resolution thing.
		double dRem = val % resolution;
		if (dRem != 0)
		{
			val = val - dRem;
		}
		return val;
  }
  
  public double getXValue()
  {
    return dXValue;
  }
  
  public double getYValue()
  {
    return dYValue;
  }

  /**
   * 
   * @param sXLabel label next to the vertical part of the cursor
   * @param sYLabel label next to the horizontal part of the cursor
   */
  public void setLabels(String sXLabel, String sYLabel)
  {
    this.sXLabel = sXLabel;
		this.sYLabel = sYLabel;		
		oCursorDrawSurface.invalidateCursors();
  }
	
  /**
   * draws a label next to the vertical part of the cursor
   */
  public void setXLabel(String sXLabel)
  {
    this.sXLabel = sXLabel;
		oCursorDrawSurface.invalidateCursors();
  }

  /**
   * a label next to the vertical part of the cursor
   */
  public String getXLabel()
  {
    return sXLabel;
  }

  /**
   * draws a label next to the horizontal part of the cursor
   */
  public void setYLabel(String sYLabel)
	{		
    this.sYLabel = sYLabel;
		oCursorDrawSurface.invalidateCursors();
  }

  /**
   * a label next to the horizontal part of the cursor
   */
  public String getYLabel()
  {
    return sYLabel;
  }  
  
  public Color getColor()
  {
    return oCursorColor;
  }

  /**
   * @return the KeyEvent.VK_* code of the keybinding to position the cursor, -1 is "no key pressed", -2 is "not positionable"
   */
  public int getKeyBinding()
  {
    return iKeyBinding;
  }
  
  /**
   * the KeyEvent.VK_* keycode of this cursor. -1 is "no key pressed", -2 means "not positionable"
   */
  public void setKeyBinding(int binding)
  {
    this.iKeyBinding = binding;
  }
  
  public String getCursorID()
  {
    return sCursorID;
  }
 
  public void setXResolution(float fXResolution)
  {
    this.fXResolution = fXResolution;
    snapToResolution(this.dXValue, this.dYValue);
  }
	
  public float getXResolution()
  {
    return fXResolution;
  }
  
  public void setYResolution(float fYResolution)
  {
    this.fYResolution = fYResolution;
    snapToResolution(this.dXValue, this.dYValue);
  }
	
  public float getYResolution()
  {
    return fYResolution;
  }
  
  void setXCoordinate(int iXCoordinate)
  {
    this.iXCoordinate = iXCoordinate;
  }
  
  int getXCoordinate()
  {
    return iXCoordinate;
  }
  
  void setYCoordinate(int iYCoordinate)
  {
    this.iYCoordinate = iYCoordinate;
  }
  
  public int getYCoordinate()
  {
    return iYCoordinate;
  }
  
  public void setHorizontalCursorLabelCoordinates(int iHorizontalCursorLabelXCoordinate, int iHorizontalCursorLabelYCoordinate)
  {
    this.iHorizontalCursorLabelXCoordinate = iHorizontalCursorLabelXCoordinate;
    this.iHorizontalCursorLabelYCoordinate = iHorizontalCursorLabelYCoordinate;
  }
  
  int getHorizontalCursorLabelXCoordinate()
  {
    return iHorizontalCursorLabelXCoordinate;
  }  
  
  int getHorizontalCursorLabelYCoordinate()
  {
    return iHorizontalCursorLabelYCoordinate;
  }
    
  public void setVerticalCursorLabelCoordinates(int iVerticalCursorLabelXCoordinate, int iVerticalCursorLabelYCoordinate)
  {
    this.iVerticalCursorLabelXCoordinate = iVerticalCursorLabelXCoordinate;
    this.iVerticalCursorLabelYCoordinate = iVerticalCursorLabelYCoordinate;
  }
  
  int getVerticalCursorLabelXCoordinate()
  {
    return iVerticalCursorLabelXCoordinate;
  }  
  
  int getVerticalCursorLabelYCoordinate()
  {
    return iVerticalCursorLabelYCoordinate;
  }
	
  /**
   * turns horizontal AND vertical on/off.
   */
  public void setCursorEnabled(boolean enabled) {
		this.bHorizontalCursorEnabled = enabled;
		this.bVerticalCursorEnabled = enabled;
		oCursorDrawSurface.repaint();
  }
  
	public void setHorizontalCursorEnabled(boolean bHorizontalCursorEnabled)
	{
		this.bHorizontalCursorEnabled = bHorizontalCursorEnabled;
		oCursorDrawSurface.repaint();
	}	
	
	public boolean isHorizontalCursorEnabled()
	{
		return bHorizontalCursorEnabled;
	}
		
	public void setVerticalCursorEnabled(boolean bVerticalCursorEnabled)
	{
		this.bVerticalCursorEnabled = bVerticalCursorEnabled;
		oCursorDrawSurface.repaint();
	}	
	
	public boolean isVerticalCursorEnabled()
	{
		return bVerticalCursorEnabled;
	}	
}
