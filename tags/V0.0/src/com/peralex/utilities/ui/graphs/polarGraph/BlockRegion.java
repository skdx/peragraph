package com.peralex.utilities.ui.graphs.polarGraph;

/**
 * 
 * @author David Lee
 */
public class BlockRegion
{
	public float start_deg;

	public float end_deg;

	public BlockRegion(float start_degrees, float end_degrees)
	{
		this.start_deg = normalise(start_degrees);
		this.end_deg = normalise(end_degrees);
	}

	public float getExtent_deg()
	{
		return end_deg - start_deg;
	}

	public void setEnd_deg(float deg)
	{
		this.end_deg = deg;
	}
	
	public float getEnd_deg()
	{
		return end_deg;
	}

	public void setStart_deg(float deg)
	{
		this.start_deg = deg;
	}
	
	public float getStart_deg()
	{
		return start_deg;
	}

	public static float normalise(float value)
	{
		if (value > 360)
		{
			return value-360;
		}
		if (value < 0)
		{
			value += 360;
		}

		return value;
	}

	@Override
	public String toString()
	{
		return String.format("(%d, %d)", start_deg, end_deg);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final BlockRegion region = (BlockRegion) o;
		return end_deg == region.end_deg && start_deg == region.start_deg;
	}

	@Override
	public int hashCode()
	{
		return Float.floatToIntBits(start_deg) ^ Float.floatToIntBits(end_deg);
	}
}
