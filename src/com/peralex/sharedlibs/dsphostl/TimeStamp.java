package com.peralex.sharedlibs.dsphostl;

import com.peralex.utilities.ValueFormatter;

/**
 * Class used for representing times on the host in microsecond resolution.
 * 
 * @author Robert Crida
 * @version 1.01 October 2001
 */
public final class TimeStamp implements Comparable<TimeStamp>, Cloneable
{

	/** the millisecond component of the time */
	private final long time_msec;

	/** the microsecond component of the time (<1000) */
	private final int time_usec;

	/**
	 * factory method to create a TimeStamp from a pure microseconds value
	 */
	public static TimeStamp from_usec(long lTime_us)
	{
		return new TimeStamp(lTime_us / 1000, (int) (lTime_us % 1000));
	}

	/**
	 * Creates new TimeStamp
	 * 
	 * @param time_msec millisecond time
	 * @param time_usec microsecond component of time (<1000)
	 */
	public TimeStamp(long time_msec, int time_usec)
	{
		this.time_msec = time_msec;
		this.time_usec = time_usec;
		if (time_usec < 0)
			throw new IllegalArgumentException("usec component may not be < 0 : " + time_usec);
		if (time_usec > 1000)
			throw new IllegalArgumentException("usec component may not be > 1000 : " + time_usec);
	}

	/**
	 * Creates new TimeStamp
	 * 
	 * @param time_msec millisecond time
	 */
	public TimeStamp(long time_msec)
	{
		this(time_msec, 0);
	}

	/**
	 * creates a new TimeStamp representing time now.
	 */
	public TimeStamp()
	{
		this(System.currentTimeMillis(), 0);
	}

	/**
	 * Access to the millisecond component of the timestamp.
	 * 
	 * @return the millisecond component of the timestamp.
	 */
	public long getTime_msec()
	{
		return time_msec;
	}

	/**
	 * Access to the microsecond component of the timestamp.
	 * 
	 * @return the microsecond component of the timestamp (<1000).
	 */
	public int getTime_usec()
	{
		return time_usec;
	}

	/**
	 * Calculates the total number of microseconds in the time, useful after performing a subtraction.
	 * 
	 * @return total number of microseconds in the time.
	 */
	public long getPeriod_usec()
	{
		return time_msec * 1000 + time_usec;
	}

	/**
	 * Determine the current time and return it in a new timestamp.
	 * 
	 * @return the current time in a new timestamp
	 */
	public static TimeStamp getCurrentTime()
	{
		return new TimeStamp(System.currentTimeMillis());
	}

	/**
	 * Test this timestamp is equal to another.
	 * 
	 * @param oth other timestamp
	 * @return true if the timestamps are equal
	 */
	public boolean equals(TimeStamp oth)
	{
		return oth != null && time_msec == oth.time_msec && time_usec == oth.time_usec;
	}

	/**
	 * Test this timestamp is equal or after another
	 * 
	 * @param oth other timestamp
	 * @return true if the timestamps are equal or after.
	 */
	public boolean equalsOrAfter(TimeStamp oth)
	{
		return equals(oth) || after(oth);
	}

	/**
	 * Test if this object is equal to another. If this object is an instance of TimeStamp it will check if this.time ==
	 * oth.time.
	 * 
	 * @param oth other object
	 * @return true if the timestamps are equal
	 */
	@Override
	public boolean equals(Object oth)
	{
		return oth instanceof TimeStamp ? equals((TimeStamp) oth) : super.equals(oth);
	}

	/**
	 * Need to implement hashCode since we implement equals(Object)
	 */
	@Override
	public int hashCode()
	{
		return ((int)(this.time_msec & 0xffffffff)) ^ this.time_usec;
	}

	/**
	 * Test if this timestamp is before another one.
	 * 
	 * @param oth other timestamp
	 * @return true if this timestamp is before the other one
	 */
	public boolean before(TimeStamp oth)
	{
		return time_msec < oth.time_msec || (time_msec == oth.time_msec && time_usec < oth.time_usec);
	}

	/**
	 * Test if this timestamp is the same or before another one.
	 * 
	 * @param oth other timestamp
	 * @return true if this timestamp is the same or before the other one
	 */
	public boolean equalsOrBefore(TimeStamp oth)
	{
		return equals(oth) || before(oth);
	}

	/**
	 * Test if this timestamp is after another one.
	 * 
	 * @param oth other timestamp
	 * @return true if this timestamp is after the other one
	 */
	public boolean after(TimeStamp oth)
	{
		return time_msec > oth.time_msec || (time_msec == oth.time_msec && time_usec > oth.time_usec);
	}

	/**
	 * Add this time to another one and return the result
	 * 
	 * @param oth other timestamp
	 * @return the sum of this and oth
	 */
	public TimeStamp add(TimeStamp oth)
	{
		long res_time_msec = this.time_msec + oth.time_msec;
		int res_time_usec = this.time_usec + oth.time_usec;
		if (res_time_usec > 1000)
		{
			res_time_msec++;
			res_time_usec -= 1000;
		}
		return new TimeStamp(res_time_msec, res_time_usec);
	}

	/**
	 * Subtract the other timestamp from this one and return the result
	 * 
	 * @param oth other timestamp
	 * @return the difference between this and oth
	 */
	public TimeStamp subtract(TimeStamp oth)
	{
		long res_time_msec = this.time_msec - oth.time_msec;
		int res_time_usec;
		if (this.time_usec >= oth.time_usec)
		{
			res_time_usec = this.time_usec - oth.time_usec;
		}
		else
		{
			res_time_msec--;
			res_time_usec = this.time_usec + 1000 - oth.time_usec;
		}
		return new TimeStamp(res_time_msec, res_time_usec);
	}

	/**
	 * Find the absolute value of this time (useful for periods)
	 * 
	 * @return the absolute value of this time
	 */
	public TimeStamp abs()
	{
		if (time_msec >= 0)
		{
			return this;
		}
		else
		{
			return new TimeStamp(-1 - time_msec, 1000 - time_usec);
		}
	}

	/**
	 * Get the time of day from this TimeStamp as a String.
	 * 
	 * @return The time of day as a String.
	 */
	public String getTimeOfDay()
	{
		return ValueFormatter.formatTimeGMT(time_msec) + time_usec;
	}

	/**
	 * Get the time of day from this TimeStamp as a String.
	 * 
	 * @return The time of day as a String.
	 */
	@Override
	public String toString()
	{
		return ValueFormatter.formatTimeGMT(time_msec, time_usec);
	}

	/**
	 * Compare this Time Stamp to another Time Stamp. Used for sorting.
	 * 
	 * @param oTS Another TimeStamp. If not instance of TimeStamp this Time Stamp will return that it's bigger in value.
	 */
	public int compareTo(TimeStamp oTS)
	{
		int iResult = 1;
		if (oTS != null && before(oTS))
		{
			iResult = -1;
		}
		else if (oTS != null && equals(oTS))
		{
			iResult = 0;
		}
		return iResult;
	}

	@Override
	public TimeStamp clone()
	{
		return new TimeStamp(time_msec, time_usec);
	}
}
