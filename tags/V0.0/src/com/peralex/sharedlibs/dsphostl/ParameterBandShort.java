package com.peralex.sharedlibs.dsphostl;

/**
 * A utility class which is designed to hold two integer values which represent a legal band. Any values which fall
 * outside this band are considered not valid.
 * 
 * @author Roy Emmerich
 * @version v1.1 July 2002
 */
public class ParameterBandShort
{
	/** Minimum acceptable value of the band */
	protected final short min;

	/** Maximum acceptable value of the band */
	protected final short max;

	/**
	 * No-args constructor. Min and max values of <code>ParameterBandInt</code> initialised to 0.
	 */
	public ParameterBandShort()
	{
		this.min = 0;
		this.max = 0;
	}

	/**
	 * Creates a new instance of <code>ParameterBandInt</code> with specific values.
	 * 
	 * @param min Minimum acceptable value of the band.
	 * @param max Maximum acceptable value of the band.
	 */
	public ParameterBandShort(short min, short max)
	{
		this.min = min;
		this.max = max;
	}

	/**
	 * Check if the band contains a particular value.
	 * 
	 * @param value Value to check against the band
	 * @return True if the value is in the band
	 */
	public boolean contains(short value)
	{
		return (value >= min && value <= max);
	}

	/**
	 * Snap the given value into the band and return it. If the value is outside the band then it will be snapped to the
	 * nearest legal value. In other words the minimum value if it is less than the minimum value and the maximum value if
	 * it is greater than the maximum value.
	 * 
	 * @param value Value to snap into the band.
	 * @return Value after snapping into the band.
	 */
	public short snapToGrid(short value)
	{
		if (value < min)
		{
			return min;
		}
		else if (value > max)
		{
			return max;
		}
		else
		{
			return value;
		}
	}

	/**
	 * Return the centre value from the parameter band.
	 * 
	 * @return The valid centre value from the band.
	 */
	public short getCentre()
	{
		return (short) ((min + max) / 2);
	}

	public short getMin()
	{
		return min;
	}

	public short getMax()
	{
		return max;
	}

	/**
	 * This prints a string representing the values contained in this class.
	 * 
	 * @return The string containing the values.
	 */
	@Override
	public String toString()
	{
		return "Minimum:" + getMin() + " Maximum:" + getMax() + "";
	}
}
