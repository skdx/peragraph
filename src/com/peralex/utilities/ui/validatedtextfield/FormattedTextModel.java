package com.peralex.utilities.ui.validatedtextfield;

import java.text.ParsePosition;

/**
 * 
 * @author Noel Grandin
 */
public abstract class FormattedTextModel<TFormat extends java.text.Format> implements ValidatedTextModel
{
	protected final TFormat format;

	protected FormattedTextModel(TFormat format)
	{
		this.format = format;
	}

	public boolean isValid(String text)
	{
		final ParsePosition pos = new ParsePosition(0);
		Object obj = format.parseObject(text, pos);
		// the text is only valid is we successfully parsed ALL of it. Don't want trailing bits of
		// not-valid text.
		return obj != null && pos.getErrorIndex() == -1 && pos.getIndex() == text.length();
	}

	public String objectToText(Object obj)
	{
		return format.format(obj);
	}
}