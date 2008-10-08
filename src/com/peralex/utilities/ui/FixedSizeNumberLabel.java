package com.peralex.utilities.ui;

import java.text.DecimalFormat;

/**
 * A label that sizes itself to the number format required, and does not change size.
 * 
 * Mostly used in graph co-ordinate panel displays.
 * 
 * @author Noel Grandin
 */
public class FixedSizeNumberLabel extends FixedSizeLabel
{
	
	private DecimalFormat oFormat;
	private String pattern;
	private String oSuffix = "";
	
	public FixedSizeNumberLabel()
	{
	}
	
	public DecimalFormat getFormat()
	{
		return oFormat;
	}
	
	/**
	 * uses the same pattern strings as DecimalFormat.
	 * But any extra whitespace is treated as significant for sizing the label.
	 */
	public void setFormat(String pattern)
	{
		this.oFormat = new DecimalFormat(pattern);
		this.pattern = pattern;
		updateSamplePattern();
	}
	
	private void updateSamplePattern()
	{
		setSamplePattern('-' + pattern.replace(' ', '0') + oSuffix);
	}
	
	public void setValue(double d)
	{
		if (oFormat!=null)
		{
			setText(oFormat.format(d) + oSuffix);
		}
		else
		{
			setText("" + d + oSuffix);
		}
	}
	
	public void setValue(Number l)
	{
		if (oFormat!=null)
		{
			setText(oFormat.format(l) + oSuffix);
		}
		else
		{
			setText("" + l + oSuffix);
		}
	}
	
	public String getSuffix()
	{
		return oSuffix;
	}

	public void setSuffix(String suffix)
	{
		final String oldValue = oSuffix;
		oSuffix = suffix;
    if (oSuffix == null || oldValue == null || !oSuffix.equals(oldValue))
    {
  		updateSamplePattern();
    }
	}
}
