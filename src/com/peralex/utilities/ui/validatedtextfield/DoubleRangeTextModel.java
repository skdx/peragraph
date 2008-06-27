package com.peralex.utilities.ui.validatedtextfield;

/**
 * 
 * @author Noel Grandin
 */
public class DoubleRangeTextModel extends DoubleTextModel
{
	private final double minValue, maxValue;

	public DoubleRangeTextModel(double minValue, double maxValue)
	{
		super(minValue);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public DoubleRangeTextModel(double initialValue, double minValue, double maxValue)
	{
		super(initialValue);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public boolean isValid(String text)
	{
		if (!super.isValid(text))
			return false;
		final double f = textToObject(text);
		return f >= minValue && f <= maxValue;
	}
}