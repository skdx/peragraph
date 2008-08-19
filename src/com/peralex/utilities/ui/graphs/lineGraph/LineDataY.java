package com.peralex.utilities.ui.graphs.lineGraph;

/**
 * Acts as a base class for LineData and GeneratedLineData
 * 
 * @author Noel Grandin
 */
abstract class LineDataY extends AbstractLineData {

  /**
   * Stores the Y Values of the Line.
   */  
  private float[] afYValues;
	
  /** 
   * Creates a new instance of cLineData
   */
  protected LineDataY()
  {
  }
	
  /** 
   * Creates a new instance of cLineData
   */
  protected LineDataY(float[] afYValues)
  {
  	if (afYValues==null) {
  		throw new IllegalArgumentException("afYValues may not be null");
  	}
    this.afYValues = afYValues;
  }
  
  /**
   * sometimes, I need higher precision data
   */
  @Override
  public double getXValueDouble(int idx) {
    return getXValue(idx);
  }
  
  /**
   * sometimes, I need higher precision data
   */
  @Override
  public double getYValueDouble(int idx)
  {
    return getYValue(idx);
  }
  
	@Override
	public float getYValue(int idx)
	{
		return afYValues[idx];
	}
	
  /**
   * Setter for property afYValues.
   */
  public final void setYValues(float[] afYValues)
  {
    this.afYValues = afYValues;
  }
  
  /**
   * Getter for property afYValues.
   */
  public final float[] getYValues()
  {
    return this.afYValues;
  }

}
