package com.peralex.utilities.ui.validatedtextfield;

/**
 * A utility class for TextModel's that will only store text and only plan on implementing
 * a validation method.
 * 
 * Sample usage:
 * <code>
 *   public class MySimpleTextModel extends SimpleTextModel {
 *     public boolean isValid(String s) {
 *       return Pattern.matches("\\d+", s);
 *     }
 *   }
 *   
 * </code>
 * 
 * @author Noel Grandin
 */
public abstract class SimpleTextModel implements ValidatedTextModel
{
	protected String s;

	/** Convert text to object. Will always be called with a valid text value. */
	public Object textToObject(String text)
	{
		return text;
	}

	/** Convert an object to text .*/
	public String objectToText(Object obj)
	{
		return (String) obj;
	}
	
	public Object getValue()
	{
		return s;
	}
	
	/** will always be called with a valid value */
	public void setValue(Object obj)
	{
		this.s = (String) obj;
	}

}
