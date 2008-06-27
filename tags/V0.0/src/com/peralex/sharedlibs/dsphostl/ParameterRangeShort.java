package com.peralex.sharedlibs.dsphostl;

/**
 * A utility class which is designed to hold three integer values which represent a legal range. Any values which fall
 * outside this range or which aren't multiples of the increment are considered invalid.
 * 
 * @author Roy Emmerich
 * @version v1.1 July 2002
 */
public class ParameterRangeShort extends ParameterBandShort
{
	/** The increment of the range */
	protected final short increment;

	/**
	 * No-args constructor. Min, max & increment values of <code>ParameterBandInt</code> will be initialised to 0.
	 */
	public ParameterRangeShort()
	{
		this.increment = 0;
	}

	/**
	 * Creates a new instance of code>ParameterBandInt</code> with specific values.
	 * 
	 * @param min Minimum acceptable value in the range.
	 * @param max Maximum acceptable value in the range.
	 * @param increment Acceptable increment of values in the range.
	 */
	public ParameterRangeShort(short min, short max, short increment)
	{
		super(min, max);
		this.increment = increment;
	}

	/**
	 * Check if range contains a particular value.
	 * 
	 * @param value Value to check against the range.
	 * @return True if the value is in the range.
	 */
	@Override
	public boolean contains(short value)
	{
		return (value >= min && value <= max && (value - min) % increment == 0);
	}

	/**
	 * Snap the given value to the grid and return it.
	 * 
	 * @param value Value to snap to the range grid.
	 * @return Value after snapping it to the range grid.
	 */
	@Override
	public short snapToGrid(short value)
	{
		// clip to range
		if (value < min)
		{
			value = min;
		}
		if (value > max)
		{
			value = max;
		}
		// snap to grid
		return (short) ((value - min + increment / 2) / increment * increment + min);
	}

	/**
	 * Return the centre valid value from the parameter range.
	 * 
	 * @return The valid centre value from the range.
	 */
	@Override
	public short getCentre()
	{
		short centre = (short) (min / 2 + max / 2);
		if (!contains(centre))
		{
			centre = snapToGrid(centre);
		}
		return centre;
	}

	public short getIncrement()
	{
		return increment;
	}

	/**
	 * This prints a string representing the values contained in this class.
	 * 
	 * @return The string containing the values.
	 */
	@Override
	public String toString()
	{
		return "Increment:" + getIncrement() + " " + super.toString();
	}

}
