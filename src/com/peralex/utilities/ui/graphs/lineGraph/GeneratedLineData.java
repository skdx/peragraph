package com.peralex.utilities.ui.graphs.lineGraph;

/**
 * cLineData that generates the X values on the fly, saving some memory
 * 
 * @author Noel Grandin
 */
public class GeneratedLineData extends LineDataY {

	private double startX;
	private double rangeX; // difference between start and end
	private int cntX;

	public GeneratedLineData() {
		this(0, 0, 0, new float[0]);
	}
	
	public GeneratedLineData(double startX, double endX, int cntX, float[] afYValues) {
		super(afYValues);
		if (afYValues.length!=cntX) {
			throw new IllegalArgumentException("afYValues length must be same as afXValues length, " + afYValues.length + "!=" + cntX);
		}
		this.startX = startX;
		this.cntX = cntX;
		this.rangeX = endX - startX;
	}

	public GeneratedLineData(double startX, double endX, float[] afYValues) {
		super(afYValues);
		this.startX = startX;
		this.cntX = afYValues.length;
		this.rangeX = endX - startX;
	}
	
	@Override
	public int getNumberOfPoints() {
		return this.cntX;
	}
	
	@Override
	public float getXValue(int idx) {
		return (float) (this.startX + (idx * rangeX / cntX)); 
	}
	
  /**
   * override and return a higher-resolution value
   */
	@Override
  public double getXValueDouble(int idx) {
    return this.startX + (idx * rangeX / cntX); 
  }
  
  /**
   * Do a binary search on the x-values and return the 
   * index in the array of values that is closed to the key value.
   */
	@Override
  public int binarySearchXValues(double fKey) {
  	if (fKey<startX) {
  		return -1;
  	}
  	if (fKey>(startX+rangeX)) {
  		return cntX;
  	}
  	return (int) ((fKey - startX) / rangeX * cntX);  	
  }
	
	/**
	 * this takes a start, an end, and a count
	 */
	public void setXValues(double startX, double endX, int cntX) {
		this.startX = startX;
		this.cntX = cntX;
		this.rangeX = endX - startX;
	}

	/**
	 * this takes a start, a resolution and a count
	 */
	public void setXFrequencyResolution(double startX, double fResolution, int cntX) {
		final double endX = startX + (fResolution*cntX);
		this.startX = startX;
		this.cntX = cntX;
		this.rangeX = endX - startX;
	}
	
}
