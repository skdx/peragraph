package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import javax.swing.JCheckBoxMenuItem;

import com.peralex.utilities.ui.graphs.axisscale.AbstractAxisScale;
import com.peralex.utilities.ui.graphs.axisscale.AbstractDefaultAxisScale;
import com.peralex.utilities.ui.graphs.axisscale.NumberAxisScale;

/**
 * This is the draw surface for a graph.
 *
 * FIXME x,y grid spacing should be font-relative by default, not hard-coded pixels.
 * 
 * @author Andre
 */
public class GridDrawSurface extends PopupMenuDrawSurface
{
	
	/** 
   * Maintains a handle on the X Axis 
   */
	private AbstractAxisScale oXAxis = new NumberAxisScale(AbstractDefaultAxisScale.X_AXIS);

	/**
   * Maintains a handle on the Y Axis
   */
	private AbstractAxisScale oYAxis = new NumberAxisScale(AbstractDefaultAxisScale.Y_AXIS);
	
	/** 
   * Default unit range values for the Y Axis.
   */
	private float fMinimumY = -20, fMaximumY = 20;
  
	/** 
   * Default unit range values for the X Axis.
   */
	private float fMinimumX = 0, fMaximumX = 10000000;

	/**
	 * This Flag determines whether the Grid must be shown or not.
	 */
	private boolean bGridVisible = true;

  /**
   * Stores the different UnitLabels.
   * http://physics.nist.gov/cuu/Units/prefixes.html
   * http://en.wikipedia.org/wiki/SI_prefix
   */
  private static final String[] sUnitLabels = new String[] { "", "k", "M", "G", "T", "P", "E", "Z", "Y" };
  private static final String sUnknownUnitLabel = "?";
  
  /**
   * These arrays stores the steps at which the vertical and horizontal grids must be drawn.
   */
  private int[] aiVerticalGridSteps, aiHorizontalGridSteps;
  
  /**
   * Stores the scaling factor buy which X and Y values must be multiplied when displayed.
   */
  private long lXScalingFactor = 1, lYScalingFactor = 1;
  
  /**
   * Stores the scaling unit that will be displayed for each axis.
   */
  private String sXScaleUnit = "", sYScaleUnit = "";  
  
	/**
	 * Should the display show K°(engineering) or 1000's°(decimal).
	 */
	private boolean bXDecimalUnitLabels = false, bYDecimalUnitLabels = false;
	
	/**
	 * This stores the approximate number of pixels that will be between the X and Y grids.
	 */
	private int iXGridSpacing = 60, iYGridSpacing = 40;

	/** 
	 * This is the color used for the grid.
	 */
	private Color oGridColor = new Color(80, 80, 80);
	
	/**
	 * This is the CheckBox that gets added to the PopUpMenu.
	 */
	private final JCheckBoxMenuItem oShowGridItem;
	
	
	/**
	 * Creates a new DrawSurface with only a y axis.
	 */
	public GridDrawSurface()
	{
    // Add the Show grid item to the PopUpMenu.
    oShowGridItem = new JCheckBoxMenuItem(textRes.getString("Show_grid"), true);
    oPopupMenu.add(oShowGridItem);
    oShowGridItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        setGridVisible(oShowGridItem.isSelected());
      }
    });

    setFocusable(true);
    _setGridMinMax(fMinimumX, fMaximumX, fMinimumY, fMaximumY);
  }
	  
  /**
   * This will paint the grid.
   */
	@Override
	protected void paint(Graphics g, int iDrawSurfaceID)
  {
		if (GRID_DRAWSURFACE != iDrawSurfaceID)
		{
			return;
		}
		if (!bGridVisible)
		{
			return;
		}
		
		g.setColor(oGridColor);

		// Draw the Horizontal Grid
		if (aiHorizontalGridSteps == null) { calculateHorizontalGridAndAxis(); }    
		for (int element : aiHorizontalGridSteps)
		{
			if (element != 0 && element != getHeight())
			{        
				g.drawLine(0, element, getWidth(), element);
			}
		}

		// Draw the Vertical Grid
		if (aiVerticalGridSteps == null) { calculateVerticalGridAndAxis(); }
		for (int element : aiVerticalGridSteps)
		{
			if (element != 0 && element != getWidth())
			{
				g.drawLine(element, 0, element, getHeight());
			}
		}      
  }
  
  /**
   * This method will calculate the Horizontal Grid lines and the Axis labels.
   */
  private void calculateHorizontalGridAndAxis()
  {
    oYAxis.clear();

    // If no spacing, or the width is too small to be drawn sensibly, don't draw at all.
    if ( !areInputValuesValid( iYGridSpacing, fMinimumY, fMaximumY ))
    {
      return;
    }
    
    aiHorizontalGridSteps = calculateGridAndAxis(getHeight(), iYGridSpacing, fMinimumY, fMaximumY, oYAxis, lYScalingFactor, false);
    
    oYAxis.repaint();
  }
  
  /**
   * This method will recalculate the Vertical Grid lines and the Axis labels.
   */
  private void calculateVerticalGridAndAxis()
  {
    oXAxis.clear();
    
    if (!areInputValuesValid(iXGridSpacing, fMinimumX, fMaximumX))
		{
			return;
		}
    
    aiVerticalGridSteps = calculateGridAndAxis(getWidth(), iXGridSpacing, fMinimumX, fMaximumX, oXAxis, lXScalingFactor, true);
    
    oXAxis.repaint();
  }

  /**
   * @param bProportional If the unit axis is inversely proportional to the pixel axis then
   *                       this must be false (Inversely proportional: units get smaller as pixels get bigger)
   * @return int array containing the grid steps
   */
  private static int [] calculateGridAndAxis(int iAxisLength_px, int iGridSpacing, float fMinimum, float fMaximum, AbstractAxisScale oAxis, long lScalingFactor, boolean bProportional)
  {
    // The step size between the vertical gridlines on the x axis 
    final double dStepSize_Hz = calculateStepSize(iAxisLength_px / iGridSpacing, fMinimum, fMaximum);
    // The first unit value on an axis 
    final float fFirstGridValue = (float)(Math.floor(fMinimum/dStepSize_Hz) * dStepSize_Hz);
    // The number of steps along the x axis 
    final int iNumSteps = (int)((fMaximum - fFirstGridValue)/dStepSize_Hz) + 1;
   
    final int [] aiGridSteps;
    if (iNumSteps>3)
    {
	    aiGridSteps = new int[iNumSteps];
	    for (int i=0; i<iNumSteps; i++)
	    {
	    	final double val = fFirstGridValue + (i * dStepSize_Hz);
	      aiGridSteps[i] = PixelUnitConverter.unitToPixel(bProportional, val, 0, iAxisLength_px, fMinimum, fMaximum);
      	oAxis.addLabel(aiGridSteps[i], (float)(val / lScalingFactor));
	    }
    }
    else if (iNumSteps==3)
    {
    	/* Put a label at the beginning, at the end, and in the middle.
    	 * Create a single grid line in the middle. */
	    aiGridSteps = new int[1];
	    final double val = (fMinimum+fMaximum)/2;
      aiGridSteps[0] = PixelUnitConverter.unitToPixel(bProportional, val, 0, iAxisLength_px, fMinimum, fMaximum);
      oAxis.addLabel(aiGridSteps[0], (float)(val / lScalingFactor)); 
      if (bProportional)
      {
	      oAxis.addLabel(0, fMinimum / lScalingFactor);
	      oAxis.addLabelAtEnd(fMaximum / lScalingFactor);
      }
      else
      {
	      oAxis.addLabel(0, fMaximum / lScalingFactor);
	      oAxis.addLabelAtEnd(fMinimum / lScalingFactor);
      }
    }
    else
    {
    	/* Put a label at the beginning and at the end.
    	 * Create no grid lines. */
	    aiGridSteps = new int[0];
      if (bProportional)
      {
	      oAxis.addLabel(0, fMinimum / lScalingFactor);
	      oAxis.addLabelAtEnd(fMaximum / lScalingFactor);
      }
      else
      {
	      oAxis.addLabel(0, fMaximum / lScalingFactor);
	      oAxis.addLabelAtEnd(fMinimum / lScalingFactor);
      }
    }
    
    return aiGridSteps;
  }
  
  /**
   * If no spacing, or the width is too small to be drawn sensibly, don't draw at all.
   */
  private static boolean areInputValuesValid( int iGridSpacing, double fMinimum, double fMaximum )
  {
    if ( iGridSpacing == 0 || Math.abs( fMinimum - fMaximum ) < 1e-300 )
    {
      return false;
    }
    return true;
  }  
  
  /**
   * format a value the same way an X-axis label is formatted
   */
  public String formatXAxisLabel(float fVal)
  {
  	return getXAxisScale().formatValueAsLabel(fVal / lXScalingFactor);
  }
  
  /** 
   * Calculate the no of spaces along the axis
   *
   * @param approxSpaces - The number of spaces that would look nice for a particular size of drawSurface
   * @param max - The maximum value on the axis
   * @param min - The minimum value on the axis
   * @return - The step size across the axis in the same unit as arguments min & max 
   */
  private static double calculateStepSize(int approxSpaces, double min, double max)
  {
    // Some input values make the calculation unpractical (like zero width.) 
    // So check for zero or negative divisor, and possibly log 0.
    
    if ( approxSpaces <= 0 )
    {
      approxSpaces = 1;
    }
    
    double difference = (max - min);
    if ( difference <= 0 )
    {
      difference = 1;
    }
    
    double ratio = difference/approxSpaces;
    if ( Double.isInfinite( ratio ) || Double.isNaN( ratio ) )
    {
      ratio = 1;
    }
    
    double m = Math.log10( ratio );
    double floorM = Math.floor(m);
    double remainder = m - floorM;
    int f = 0;
		
    if (remainder <= 0.15)  { f = 1; }
    else if (remainder <= 0.5) { f = 2; }
    else if (remainder <= 0.85) { f = 5; }
    else { f = 10; }
		
    double result = f * Math.pow(10.0, floorM);
    
    // In case that all checks failed, return 1 (step) as result.
    if ( result <= 0 )
    {
      result = 1;
    }
    return result;
  }

  /**
   * This methods sets the maximum and minimum on the Axis.
   */
  public void setGridMinMax(float fMinimumX, float fMaximumX, float fMinimumY, float fMaximumY)
  {
    _setGridMinMax(fMinimumX, fMaximumX, fMinimumY, fMaximumY);
    repaint(); 
  }
  
  /**
   * private copy that can't be overridden so I can safely call it from the constructor
   */
  private void _setGridMinMax(float _fMinimumX, float _fMaximumX, float _fMinimumY, float _fMaximumY)
  {
  	if (_fMinimumX > _fMaximumX) throw new IllegalStateException("minX>maxX " + _fMinimumX + " " + _fMaximumX);
  	if (_fMinimumY > _fMaximumY) throw new IllegalStateException("minY>maxY " + _fMinimumY + " " + _fMaximumY);
    this.fMinimumX = _fMinimumX;
    this.fMaximumX = _fMaximumX;
    this.fMinimumY = _fMinimumY;
    this.fMaximumY = _fMaximumY;		

		calculateXAxisScalingFactor();
    calculateVerticalGridAndAxis();
		calculateYAxisScalingFactor();
    calculateHorizontalGridAndAxis();
  }

  /**
   * This methods sets the maximum and minimum on the X Axis.
   */
  public void setGridXMinMax(float _fMinimumX, float _fMaximumX)
  {
  	if (_fMinimumX > _fMaximumX) throw new IllegalStateException("minX>maxX " + _fMinimumX + " " + _fMaximumX);
    this.fMinimumX = _fMinimumX;
    this.fMaximumX = _fMaximumX;

		calculateXAxisScalingFactor();
    calculateVerticalGridAndAxis();
    repaint();    
  }
	
  /**
   * This methods sets the maximum and minimum on the Y Axis.
   */
  public void setGridYMinMax(float _fMinimumY, float _fMaximumY)
  {
  	if (_fMinimumY > _fMaximumY) throw new IllegalStateException("minY>maxY " + _fMinimumY + " " + _fMaximumY);
    this.fMinimumY = _fMinimumY;
    this.fMaximumY = _fMaximumY;		

		calculateYAxisScalingFactor();
    calculateHorizontalGridAndAxis();	
    repaint();    
  }	
  
  /**
   * Get the max X value.
   */
  public float getMaximumX()
  {
    return fMaximumX;
  }
  
  /**
   * Get the min X value.
   */
  public float getMinimumX()
  {
    return fMinimumX;
  }
  
  /**
   * Get the max Y value.
   */
  public float getMaximumY()
  {
    return fMaximumY;
  }
  
  /**
   * Get the min Y value.
   */
  public float getMinimumY()
  {
    return fMinimumY;
  }  

  public void setGridVisible(boolean bGridVisible)
  {
    this.bGridVisible = bGridVisible;		
		if (oShowGridItem.isSelected() != bGridVisible) { oShowGridItem.setSelected(bGridVisible); }
		
    repaint();
  }
  
  public boolean isGridVisible()
  {
  	return bGridVisible;
  }
  
  /**
	 * Compute the corresponding SI prefix for values
	 */
	private static String computeUnitPrefix(boolean bDecimalUnitLabels, float fUnitDivisor)
	{
		if (bDecimalUnitLabels)
		{
			return computeDecimalUnitPrefix(fUnitDivisor);
		}
		
		final double digitCounts = Math.log10(fUnitDivisor);
		int iLabelIndex = (int) (Math.round(digitCounts / 3));

		if (iLabelIndex < 0)
		{
			iLabelIndex = 0;
		}

		if (iLabelIndex < sUnitLabels.length)
		{
			return sUnitLabels[iLabelIndex];
		}
		else
		{
			return sUnknownUnitLabel;
		}
	}

  /**
   * This returns the unit to which a value will be converted, used for displaying purposes.
   *
   * @param dValue contains the the value.
   * @returns the unit to which it is converted.
   */
  private static String computeDecimalUnitPrefix(double dValue)
  {
    if (dValue > 999999)
    {
      return "1 000 000's";
    }
    else if (dValue > 999 && dValue <= 999999)
    {
      return "1000's";
    }
    else if (dValue <= 999 && dValue >= -999)
    {
      return "";
    }
    else if (dValue < -999 && dValue >= -999999)
    {
      return "1000'th";
    }
    if (dValue < -999999)
    {
      return "1 000 000'th";
    }
    else
    {
      return "";
    }
  }
  
  /**
	 * This method will calculate the Scaling Factor for the XAxis.
	 */
  private void calculateXAxisScalingFactor()
  {
    final float fUnitDivisor = (float)(Math.pow(10, (Math.floor(Math.floor((Math.log10(fMaximumX)) / 3)) * 3)));
    sXScaleUnit = computeUnitPrefix(bXDecimalUnitLabels, fUnitDivisor);
    lXScalingFactor = (long)fUnitDivisor;		
		if (lXScalingFactor < 1) { lXScalingFactor = 1; }
		
		fireScalingFactorListeners(lXScalingFactor, sXScaleUnit, lYScalingFactor, sYScaleUnit);
  }
  
  /**
   * This method will calculate the Scaling Factor for the XAxis.
   */
  private void calculateYAxisScalingFactor()
  {
    final float fUnitDivisor = (float)(Math.pow(10, (Math.floor(Math.floor((Math.log10(fMaximumY)) / 3)) * 3)));
    sYScaleUnit = computeUnitPrefix(bYDecimalUnitLabels, fUnitDivisor);
    lYScalingFactor = (long)fUnitDivisor;		
		if (lYScalingFactor < 1) { lYScalingFactor = 1; }
		
		fireScalingFactorListeners(lXScalingFactor, sXScaleUnit, lYScalingFactor, sYScaleUnit);
  } 
  
  /**
   * This will return the X Axis.
   */
  public AbstractAxisScale getXAxisScale()
  {
    return oXAxis;
  }
  
  public void setXAxisScale(AbstractAxisScale scale)
  {
    this.oXAxis = scale;
    repaint();
  }

  /**
   * This will return the Y Axis.
   */
  public AbstractAxisScale getYAxisScale()
  {
    return oYAxis;
  }
  
  public void setYAxisScale(AbstractAxisScale scale)
  {
    this.oYAxis = scale;
    repaint();
  }
  
  /**
   * Event for componentResized.
   */  
	@Override
  public void componentResized(ComponentEvent e)
  {
    super.componentResized(e);
    
    calculateHorizontalGridAndAxis();
    calculateVerticalGridAndAxis();
    repaint();
  }  
	
	/**
	 * @return the X scale unit e.g "k" or "G" or "1 000 000's"
	 */
	public String getXScaleUnit()
	{
		return sXScaleUnit;
	}

	/**
	 * Getter for property lXScalingFactor.
	 */
	public long getXScalingFactor()
	{
		return lXScalingFactor;
	}
	
	/**
	 * @return the Y scale unit e.g "k" or "G" or "1 000 000's"
	 */
	public String getYScaleUnit()
	{
		return sYScaleUnit;
	}

	/**
	 * Getter for property lYScalingFactor.
	 */
	public long getYScalingFactor()
	{
		return lYScalingFactor;
	}	

	/**
	 * the space between the vertical lines of the grid
	 */
	public int getXGridSpacing()
	{
		return iXGridSpacing;
	}
	
	/**
	 * the space between the vertical lines of the grid
	 */
	public void setXGridSpacing(int x)
	{
		this.iXGridSpacing = x;
    calculateHorizontalGridAndAxis();
    calculateVerticalGridAndAxis();
    repaint();
	}
	
	/**
	 * the space between the horizontal lines of the grid
	 */
	public int getYGridSpacing()
	{
		return iYGridSpacing;
	}
	
	/**
	 * the space between the horizontal lines of the grid
	 */
	public void setYGridSpacing(int y)
	{
		this.iYGridSpacing = y;
    calculateHorizontalGridAndAxis();
    calculateVerticalGridAndAxis();
    repaint();
	}
	
	/**
	 * This sets the iXGridSpacing and the iYGridSpacing.
	 * 
	 * @param iXGridSpacing the space between the vertical lines of the grid
	 * @param iYGridSpacing the space between the horizontal lines of the grid
	 */
	public void setGridSpacing(int iXGridSpacing, int iYGridSpacing)
	{
		this.iXGridSpacing = iXGridSpacing;
		this.iYGridSpacing = iYGridSpacing;
		
    calculateHorizontalGridAndAxis();
    calculateVerticalGridAndAxis();
    repaint();
	}
	
	/**
	 * Should the display show K°(engineering) or 1000's°(decimal).
	 */
	public void setXDecimalUnitLabels(boolean bXDecimalUnitLabels)
	{
		this.bXDecimalUnitLabels = bXDecimalUnitLabels;
		calculateXAxisScalingFactor();
    repaint();
	}
	
	public boolean isXDecimalUnitLabels()
	{
		return bXDecimalUnitLabels;
	}
	
	/**
	 * Should the display show K°(engineering) or 1000's°(decimal).
	 */
	public void setYDecimalUnitLabels(boolean bYDecimalUnitLabels)
	{
		this.bYDecimalUnitLabels = bYDecimalUnitLabels;
		calculateYAxisScalingFactor();
    repaint();
	}

	public boolean isYDecimalUnitLabels()
	{
		return bYDecimalUnitLabels;
	}
	
	/**
	 * This will set the color of the grid.
	 */
	public void setGridColor(Color oGridColor)
	{
		this.oGridColor = oGridColor;
		repaint();
	}
	
	/**
	 * This returns the color of the grid.
	 */
	public Color getGridColor()
	{
		return oGridColor;
	}

  /**
   * Utility function for converting X from pixel value to unit value
   */
  public final float pixelToUnitX(int pixelValue)
  {
    return (float)PixelUnitConverter.pixelToUnit(true, pixelValue, 0, getWidth(), getMinimumX(), getMaximumX());            
  }
  
  /**
   * Utility function for converting X from pixel value to unit value
   */
  public final float pixelToUnitY(int pixelValue)
  {
    return (float) PixelUnitConverter.pixelToUnit(false, pixelValue, 0, getHeight(), getMinimumY(), getMaximumY());
  }

  @Override
  protected void localeChanged()
  {
  	super.localeChanged();
    oShowGridItem.setText(textRes.getString("Show_grid"));
  }
}