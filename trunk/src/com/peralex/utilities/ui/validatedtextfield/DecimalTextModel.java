package com.peralex.utilities.ui.validatedtextfield;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * 
 * @author Noel Grandin
 */
public abstract class DecimalTextModel extends FormattedTextModel<NumberFormat>
{
	protected DecimalTextModel(DecimalFormat format)
	{
		super(format);
	}

	protected DecimalTextModel()
	{
		super(NumberFormat.getInstance());
	}

	/** helper for textToObject */
	protected final Number parse(String text)
	{
		try
		{
			return format.parse(text);
		}
		catch (ParseException ex)
		{
			// this should never happen
			throw new RuntimeException(ex);
		}
	}
}