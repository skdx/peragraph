package com.peralex.utilities.ui.validatedtextfield;

/**
 * 
 * @author Noel Grandin
 */
public class IntRangeTextModel extends IntTextModel
{
	private final int minValue, maxValue;

	public IntRangeTextModel(int initialValue, int minValue, int maxValue)
	{
		super(initialValue);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public IntRangeTextModel(int minValue, int maxValue)
	{
		this(minValue, minValue, maxValue);
	}
	
	@Override
	public boolean isValid(String text)
	{
		if (!super.isValid(text))
			return false;
		final int i = textToObject(text);
		return i >= minValue && i <= maxValue;
	}
}