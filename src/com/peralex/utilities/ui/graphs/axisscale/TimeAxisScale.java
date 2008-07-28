package com.peralex.utilities.ui.graphs.axisscale;

import java.text.SimpleDateFormat;

/**
 * An axis-scale that draw time-stamps.
 * 
 * @author Noel Grandin
 */
public class TimeAxisScale extends AbstractDefaultAxisScale
{
	/**
	 * SimpleDateFormat used for drawing the labels.
	 */
	private SimpleDateFormat oFormat = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");

	public TimeAxisScale(int iAxisType)
	{
		super(iAxisType);
	}

	public TimeAxisScale()
	{
		this(Y_AXIS);
	}

	/**
	 * format a value the same way a label is formatted.
	 */
	@Override
	public String formatValueAsLabel(double fVal)
	{
		return oFormat.format(fVal);
	}

	@Override
	protected String getSampleForPreferredSizing()
	{
		return "-" + oFormat.toPattern();
	}

	/**
	 * set the date/time format. Setting it to null will reset to the default.
	 */
	public void setFormat(SimpleDateFormat df)
	{
		if (df == null)
		{
			oFormat = defaultFormat();
		}
		else
		{
			this.oFormat = df;
		}
	}

	private static SimpleDateFormat defaultFormat()
	{
		return new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
	}

}
