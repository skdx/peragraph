package com.peralex.utilities.ui.validatedtextfield;

/**
 * 
 * @author Noel Grandin
 */
public class FloatTextModel extends DecimalTextModel
{
	private float value;
	
	public FloatTextModel(float initialValue)
	{
		this.value = initialValue;
	}

	public Float textToObject(String text)
	{
		return parse(text).floatValue();
	}
	
	public Object getValue()
	{
		return value;
	}
	
	public void setValue(Object obj)
	{
		this.value = ((Number) obj).floatValue();
	}
}