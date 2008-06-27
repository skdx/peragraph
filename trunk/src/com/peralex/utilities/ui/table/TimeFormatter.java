package com.peralex.utilities.ui.table;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.text.DefaultFormatter;

/**
 * A custom formatter class that is "lenient" in it's parsing of time values.
 * It assumes that all input values are 24 hour time value in the rough format
 * "HH:mm:ss.SSS" but it does not care if the string is missing seconds or microseconds.
 * 
 * It also has the useful side-effect that it preserves the year/month/days value in the input.
 * 
 * @author Noel Grandin
 */
public class TimeFormatter extends DefaultFormatter
{
	private final DateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
	// used to preserve the year/month/day of the input
	private java.util.Date oldValue;

	@Override
	public Object stringToValue(String text) throws ParseException
	{
		if (text==null) throw new ParseException("time value may not be null", 0);
		
		final String [] components = text.split(":");
		if (components==null || components.length==0) throw new ParseException("time value may not be empty", 0);
		if (components.length>3) throw new ParseException("too many colons in time value", 0);

		final int hours;
		try {
			hours = Integer.parseInt(components[0]);
		} catch (NumberFormatException ex)
		{
			throw new ParseException("hours value is not a valid number " + components[0], 0);
		}
		if (hours<0 || hours>23) throw new ParseException("hours value is not within range 0-23 " + components[0], 0);
		if (components.length==1)
		{
			return buildTime(hours, 0, 0, 0);
		}
		
		if (components[1].length()!=2) throw new ParseException("minutes value must be 2 characters " + components[1], 0);
		final int minutes;
		try {
			minutes = Integer.parseInt(components[1]);
		} catch (NumberFormatException ex)
		{
			throw new ParseException("minutes value is not a valid number " + components[1], 0);
		}
		if (minutes<0 || minutes>59) throw new ParseException("minutes value is not within range 0-59 " + components[1], 0);
		if (components.length==2)
		{
			return buildTime(hours, minutes, 0, 0);
		}
		
		return secondStageParse(hours, minutes, components[2]);
	}
	
	/**
	 * parse the seconds/milliseconds
	 */
	private java.util.Date secondStageParse(int hours, int minutes, String text) throws ParseException
	{
		final String [] components = text.split("\\.");
		if (components.length>2) throw new ParseException("too many period characters in time value", 0);
		
		if (components[0].length()!=2) throw new ParseException("seconds value must be 2 characters " + components[0], 0);
		final int seconds;
		try {
			seconds = Integer.parseInt(components[0]);
		} catch (NumberFormatException ex)
		{
			throw new ParseException("seconds value is not a valid number " + components[0], 0);
		}
		if (seconds<0 || seconds>59) throw new ParseException("seconds value is not within range 0-59 " + components[0], 0);
		if (components.length==1)
		{
			return buildTime(hours, minutes, seconds, 0);
		}

		// milliseconds are after the decimal point, so expand them with extra zeros to make validation easier
		String s = components[1];
		if (s.length()==2) s += "0";
		else if (s.length()==1) s += "00";
		
		final int milliseconds;
		try {
			milliseconds = Integer.parseInt(s);
		} catch (NumberFormatException ex)
		{
			throw new ParseException("milliseconds value is not a valid number " + components[1], 0);
		}
		if (seconds<0 || seconds>999) throw new ParseException("milliseconds value is not within range 0-999 " + components[1], 0);
		return buildTime(hours, minutes, seconds, milliseconds);
	}

	private java.util.Date buildTime(int hours, int minutes, int seconds, int milliseconds)
	{
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(oldValue);
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, seconds);
		cal.set(Calendar.MILLISECOND, milliseconds);
		return cal.getTime();
	}
	
	
	@Override
	public String valueToString(Object value)
	{
		oldValue = (java.util.Date) value;
		if (value==null) return "";
		return format.format(value);
	}
}
