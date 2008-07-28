package com.peralex.utilities.ui.graphs.axisscale;

import java.text.DecimalFormat;

/**
 * An axis-scale that provides the usual numbers.
 * 
 * @author Noel Grandin
 */
public class NumberAxisScale extends AbstractDefaultAxisScale
{

	/**
	 * DecimalFormat used for drawing the labels.
	 */
	private DecimalFormat oNumberFormat;
	
	public NumberAxisScale(int iAxisType)
	{
		super(iAxisType);
	}
	
	public NumberAxisScale()
	{
		this(Y_AXIS);
	}
	
	/**
	 * format a value the same way a label is formatted.
	 */
	@Override
	public String formatValueAsLabel(double fVal)
	{
		return oNumberFormat.format(fVal);
	}

	@Override
	protected String getSampleForPreferredSizing()
	{
		return "-" + oNumberFormat.toPattern();
	}
	
	/**
	 * 
	 * @param iAxisType Y_AXIS or X_AXIS
	 */
	@Override
	public void setOrientation(int iAxisType)
	{
		super.setOrientation(iAxisType);
		
		// override and reset the number format when the axis orientation changes
		oNumberFormat = defaultNumberFormat(iAxisType);
	}
	
	/**
	 * set the number format. Setting it to null will reset to the default.
	 */
	public void setFormat(DecimalFormat df)
	{
		if (df==null)
		{
			oNumberFormat = defaultNumberFormat(getOrientation());
		}
		else
		{
			this.oNumberFormat = df;
		}
	}
	
	private static DecimalFormat defaultNumberFormat(int iAxisType)
	{
		if (iAxisType==X_AXIS)
		{
			return new DecimalFormat("###0.###");
		}
		else
		{
			// most of the time, the Y-axis is decibels, and we only need a range of 0.0-140.0
			return new DecimalFormat("##0.#");
		}
	}

}
