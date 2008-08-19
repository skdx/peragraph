package com.peralex.utilities.ui.graphs.lineGraph;

/**
 * Line data class that adds an offset to it's Y values.
 * 
 * Useful when one of the displayed lines is merely an offset version of another line.
 * 
 * @author Noel Grandin
 */
public class OffsetLineData extends GeneratedLineData
{
	private double dOffset;
	
	public OffsetLineData() {
	}
	
	public OffsetLineData(double startX, double endX, int cntX, float[] afYValues) {
		super(startX, endX, cntX, afYValues);
	}

	public OffsetLineData(double startX, double endX, float[] afYValues) {
		super(startX, endX, afYValues);
	}
	
	@Override
	public double getYValueDouble(int idx)
	{
		return dOffset + super.getYValueDouble(idx);
	}
	
	@Override
	public float getYValue(int idx)
	{
		return (float) (dOffset + super.getYValue(idx));
	}
	
	public void setOffset(double offset) {
		this.dOffset = offset;
	}
	
	public double getOffset() {
		return this.dOffset;
	}

}
