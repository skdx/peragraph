package com.peralex.utilities.ui.graphs.lineGraph;

/**
 * @author Noel Grandin
 */
public abstract class AbstractLineData {

  /** 
   * Creates a new instance of cLineData
   */
  protected AbstractLineData()
  {
  }
	
  /**
   * This will return the number of points in the line.
   *
   * @return iNumberOfPoints
   */
  public abstract int getNumberOfPoints();
	
  /**
   * Get the x value at a specific index in the array of points
   */
  public abstract float getXValue(int idx);
  
  /**
   * Get the y value at a specific index in the array of points
   */
  public abstract float getYValue(int idx);
  
  /**
   * sometimes, I need higher precision data
   */
  public abstract double getXValueDouble(int idx);
  
  /**
   * sometimes, I need higher precision data
   */
  public abstract double getYValueDouble(int idx);
  
  /**
   * Do a binary search on the x-values and return the 
   * index in the array of values that is closed to the key value.
   * 
   * @return same values as Arrays.binarySearch (which means that the values can be negative!)
   */
  public abstract int binarySearchXValues(double fKey);

}
