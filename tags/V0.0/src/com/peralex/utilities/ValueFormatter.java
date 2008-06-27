package com.peralex.utilities;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Timestamps are always displayed in the local-time as to not confuse the user.
 * The Millisecond times set will always be in GMT. Thus the display time will
 * be at an offset of the system local time.
 *
 * Note (Noel) : the methods in this class are synchronized because the standard
 *   Java Format classes are NOT thread-safe.
 * 
 * @author Jaco Jooste
 */
public final class ValueFormatter
{
	private static final SimpleTimeZone GMT_TIMEZONE = new SimpleTimeZone(0, "Z");
	
	private static final DateFormat oTimeOfDayFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
	/**
	 * GMT timezone version of formatter
	 */
	private static final DateFormat oTimeOfDayFormatterGMT = new SimpleDateFormat("HH:mm:ss.SSS");
	static {
		oTimeOfDayFormatterGMT.setTimeZone(GMT_TIMEZONE);
	}
	
	private static final DecimalFormat KILO_FORMAT = new DecimalFormat("000");
	private static final String SUFFIX_M = "M";
	private static final String SUFFIX_K = "k";
	private static final String SUFFIX_HZ = "Hz";
	private static final String SPACE = " ";
	
	/** Used for file sizes */
	private static final DecimalFormat FILE_SIZE_FORMAT = new DecimalFormat("0.000");
	private static final int KB = 1024;
	private static final int MB = 1024*1024;
	private static final int GB = 1024*1024*1024;
	private static final String SUFFIX_MB = " MB";
	private static final String SUFFIX_KB = " KB";
	private static final String SUFFIX_GB = " GB";
	private static final String SUFFIX_BYTES = " bytes";
	
	/**
	 * class not meant to be instantiated.
	 */
	private ValueFormatter() {}
	
	public static synchronized String formatFrequency(long lFrequency_Hz)
	{
		final String sFreq;
		
		if (lFrequency_Hz >= 1000000)
		{
			sFreq = 
				lFrequency_Hz/1000000 + SUFFIX_M
				+ KILO_FORMAT.format((lFrequency_Hz%1000000)/1000) + SUFFIX_K 
				+ KILO_FORMAT.format(lFrequency_Hz%1000);
		}
		else if (lFrequency_Hz >= 1000)
		{
			sFreq = 
				((lFrequency_Hz%1000000)/1000) + SUFFIX_K 
				+ KILO_FORMAT.format(lFrequency_Hz%1000);
		}
		else
		{
			sFreq = lFrequency_Hz + SUFFIX_HZ;
		}
		
		return sFreq;
	}
	
	public static synchronized String formatFileSize(long lFileSize_bytes)
	{
		String sFileSize = "";
		if (lFileSize_bytes >= GB)
		{
			sFileSize = FILE_SIZE_FORMAT.format((float)lFileSize_bytes/GB) + SUFFIX_GB;
		}
		else if (lFileSize_bytes >= MB)
		{
			sFileSize = FILE_SIZE_FORMAT.format((float)lFileSize_bytes/MB) + SUFFIX_MB;
		}
		else if (lFileSize_bytes >= KB)
		{
			sFileSize = FILE_SIZE_FORMAT.format((float)lFileSize_bytes/KB) + SUFFIX_KB;
		}
		else
		{
			sFileSize = lFileSize_bytes + SUFFIX_BYTES;
		}
		return sFileSize;
	}
	
	public static synchronized String formatFileSize_MB(long lFileSize_bytes)
	{
		return FILE_SIZE_FORMAT.format((float)lFileSize_bytes/MB) + SUFFIX_MB;
	}
	
	/**
	 * @return a time formatted as oTimeFormatter.format() + " " + "UUU", where U is a microsecond digit
	 */
	public static String formatTime(long lTimeValue_msec, int iTimeValue_usec, DateFormat oTimeFormatter)
	{
		final Date oTimeStampDate = new Date(Math.abs(lTimeValue_msec));
		return formatTime(oTimeStampDate, iTimeValue_usec, oTimeFormatter);
	}
	
	/**
	 * @return a time formatted as oTimeFormatter.format() + " " + "UUU", where U is a microsecond digit
	 */
	public static synchronized String formatTime(Date oTimeStampDate, int iTimeValue_usec, DateFormat oTimeFormatter)
	{
		return oTimeFormatter.format(oTimeStampDate) + SPACE + KILO_FORMAT.format(iTimeValue_usec);
	}
	
	/**
	 * @return a GMT (UTC) time formatted as oTimeFormatter.format() + " " + "UUU", where U is a microsecond digit
	 */
	public static synchronized String formatTimeGMT(long lTimeValue_msec, int iTimeValue_usec, DateFormat oTimeFormatter)
	{
		final TimeZone oCurrentTimeZone = oTimeFormatter.getTimeZone();
		oTimeFormatter.setTimeZone(GMT_TIMEZONE);
		String sTime = formatTime(lTimeValue_msec, iTimeValue_usec, oTimeFormatter);
		oTimeFormatter.setTimeZone(oCurrentTimeZone);
		return sTime;
	}
	
	/**
	 * @return a time formatted as oTimeFormatter.format()
	 */
	public static synchronized String formatTime(long lTimeValue_msec, DateFormat oTimeFormatter)
	{
		Date oTimeStampDate = new Date(Math.abs(lTimeValue_msec));
		return oTimeFormatter.format(oTimeStampDate);
	}
	
	/**
	 * @return a GMT (UTC) time formatted as oTimeFormatter.format()
	 */
	public static synchronized String formatTimeGMT(long lTimeValue_msec, DateFormat oTimeFormatter)
	{
		TimeZone oCurrentTimeZone = oTimeFormatter.getTimeZone();
		oTimeFormatter.setTimeZone(GMT_TIMEZONE);
		String sTime = formatTime(lTimeValue_msec, oTimeFormatter);
		oTimeFormatter.setTimeZone(oCurrentTimeZone);
		return sTime;
	}
	
	/**
	 * @return a time formatted as "HH:mm:ss.SSS UUU", where U is a microsecond digit
	 */
	public static synchronized String formatTime(long lTimeValue_msec, int iTimeValue_usec)
	{
		return formatTime(lTimeValue_msec, iTimeValue_usec, oTimeOfDayFormatter);
	}
	
	/**
	 * @return a GMT (UTC) time formatted as oTimeFormatter.format() + " " + "UUU", where U is a microsecond digit
	 */
	public static synchronized String formatTimeGMT(long lTimeValue_msec, int iTimeValue_usec)
	{
		return formatTime(lTimeValue_msec, iTimeValue_usec, oTimeOfDayFormatterGMT);
	}
	
	/**
	 * @return a time formatted as "HH:mm:ss.SSS"
	 */
	public static synchronized String formatTime(long lTimeValue_msec)
	{
		Date oTimeStampDate = new Date(Math.abs(lTimeValue_msec));
		return oTimeOfDayFormatter.format(oTimeStampDate);
	}
	
	/**
	 * @return a time formatted as "HH:mm:ss.SSS", but in the GMT (aka UTC) timezone
	 */
	public static synchronized String formatTimeGMT(long lTimeValue_msec)
	{
		Date oTimeStampDate = new Date(Math.abs(lTimeValue_msec));
		return oTimeOfDayFormatterGMT.format(oTimeStampDate);
	}

	/**
	 * @return a SimpleDateFormat("ss.SSS")
	 */
	public static SimpleDateFormat createSecondFormatter() {
		return new SimpleDateFormat("ss.SSS");
	}

	/**
	 * @return a SimpleDateFormat("HH:mm:ss.SSS")
	 */
	public static DateFormat createTimeOfDayFormatter() {
		return new SimpleDateFormat("HH:mm:ss.SSS");
	}
}
