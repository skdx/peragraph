package com.peralex.utilities.ui.graphs.lineGraph;

import java.util.Arrays;

/**
 * The line graph works with float values - this acts as a wrapper around double values 
 * so that we can pass double arrays into the graph for drawing.
 * 
 * @author Noel Grandin
 */
public class LineDataDouble extends AbstractLineData
{
  /**
   * Stores the Y Values of the Line.
   */  
  private double[] adYValues;
	
  /**
   * Stores the X Values of the Line.
   */
  private double[] adXValues;
  
  /** 
   * Creates a new instance of cLineDataDouble
   */
  public LineDataDouble()
  {
  }
	
  /** 
   * Creates a new instance of cLineDataDouble
   */
  public LineDataDouble(double[] adXValues, double[] adYValues)
  {
  	if (adXValues==null) {
  		throw new IllegalArgumentException("afXValues may not be null");
  	}
  	if (adYValues==null) {
  		throw new IllegalArgumentException("afYValues may not be null");
  	}
		if (adYValues.length!=adXValues.length) {
			throw new IllegalArgumentException("afYValues length must be same as afXValues length, " + adYValues.length + "!=" + adXValues.length);
		}
    this.adXValues = adXValues;
    this.adYValues = adYValues;
  }
  
  /**
   * sometimes, I need higher precision data
   */
  @Override
  public double getXValueDouble(int idx) {
    return adXValues[idx];
  }
  
  @Override
  public double getYValueDouble(int idx)
  {
    return adYValues[idx];
  }
  
  /**
   * This will return the number of points in the line.
   */
	@Override
  public int getNumberOfPoints()
  {
    return adXValues.length;
  }
	
  /**
   * Get the x value at a specific index in the array of points
   */
	@Override
  public final float getXValue(int idx) {
  	return (float) this.adXValues[idx];
  }
  
  /**
   * Get the y value at a specific index in the array of points
   */
	@Override
	public final float getYValue(int idx)
	{
  	return (float) this.adYValues[idx];
	}
	
  /**
   * Do a binary search on the xvalues and return the 
   * index in the array of values that is closed to the key value.
   */
	@Override
  public int binarySearchXValues(double fKey) {
  	return Arrays.binarySearch(this.adXValues, fKey);  	
  }
  

}
