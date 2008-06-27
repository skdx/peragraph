package com.peralex.utilities.ui.validatedtextfield;

/**
 * 
 * @author Noel Grandin
 */
public class DoubleTextModel extends DecimalTextModel
{
	private double value;
	
	public DoubleTextModel(double initialValue)
	{
		this.value = initialValue;
	}
	
	public Double textToObject(String text)
	{
		return parse(text).doubleValue();
	}
	
	public Object getValue()
	{
		return value;
	}
	
	public void setValue(Object obj)
	{
		this.value = ((Number) obj).doubleValue();
	}
}