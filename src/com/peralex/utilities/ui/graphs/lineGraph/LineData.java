package com.peralex.utilities.ui.graphs.lineGraph;

import java.util.Arrays;

/**
 * 
 * @author Andre
 */
public class LineData extends LineDataY
{

	/**
	 * Stores the X Values of the Line.
	 */
	private float[] afXValues;

	/**
	 * Creates a new instance of cLineData
	 */
	public LineData()
	{
	}

	/**
	 * Creates a new instance of cLineData
	 */
	public LineData(float[] afXValues, float[] afYValues)
	{
		super(afYValues);
		if (afXValues == null)
		{
			throw new IllegalArgumentException("afXValues may not be null");
		}
		if (afYValues.length!=afXValues.length) {
			throw new IllegalArgumentException("afYValues length must be same as afXValues length, " + afYValues.length + "!=" + afXValues.length);
		}
		this.afXValues = afXValues;
	}

	/**
	 * This will return the number of points in the line.
	 * 
	 * @return iNumberOfPoints
	 */
	@Override
	public int getNumberOfPoints()
	{
		return afXValues.length;
	}

	/**
	 * Setter for property afXValues and afYValues.
	 */
	public void setValues(float[] afXValues, float[] afYValues)
	{
		if (afYValues.length!=afXValues.length) {
			throw new IllegalArgumentException("afYValues length must be same as afXValues length, " + afYValues.length + "!=" + afXValues.length);
		}
		this.afXValues = afXValues;
		super.setYValues(afYValues);
	}

	/**
	 * Setter for property afXValues.
	 */
	public void setXValues(float[] afXValues)
	{
		this.afXValues = afXValues;
	}

	/**
	 * Getter for property afXValues.
	 * 
	 * @return afXValues.
	 */
	public float[] getXValues()
	{
		return this.afXValues;
	}

	/**
	 * Get the x value at a specific index in the array of points
	 */
	@Override
	public final float getXValue(int idx)
	{
		return this.afXValues[idx];
	}

	/**
	 * Do a binary search on the x-values and return the index in the array of values that is closed to the key value.
	 */
	@Override
	public int binarySearchXValues(double fKey)
	{
		return Arrays.binarySearch(this.afXValues, (float) fKey);
	}

}
