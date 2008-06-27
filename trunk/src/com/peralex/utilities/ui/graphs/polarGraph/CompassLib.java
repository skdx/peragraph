package com.peralex.utilities.ui.graphs.polarGraph;

/**
 * Conversion between compass co-ordinates (in degrees) and cartesian co-ordinates.
 * Where 0 degrees is at the top, and the degrees increase clockwise.
 * 
 * @author Noel Grandin
 */
public final class CompassLib
{

	/** not meant to be instantiated */
	private CompassLib() {}

	/**
	 * convert compass co-ordinate degrees to polar co-ordinate degrees
	 */
	public static double compass2polar(double deg)
	{
		deg = 360d - deg + 90;
		if (deg>360) {
			deg -=360;
		}
		if (deg<0) {
			deg += 360;
		}
		return deg;
	}

	/**
	 * compass to cartesian - get X value
	 */
	public static double cartesianX(double ang_deg, double radius)
	{
		return Math.sin(Math.toRadians(ang_deg)) * radius;
	}

	
	/**
	 * compass to cartesian - get Y value
	 * WARNING - this y value increases upwards, which is the opposite of the Java2D co-ordinate system.
	 */
	public static double cartesianY(double ang_deg, double radius)
	{
		return Math.cos(Math.toRadians(ang_deg)) * radius;
	}

	/**
	 * cartesian to compass - get radius
	 */
	public static double compassR(double x, double y)
	{
		return Math.sqrt(x*x + y*y);
	}

	/**
	 * cartesian to compass - get angle in degrees
	 */
	public static double compassA_deg(double x, double y)
	{
		double degrees = Math.toDegrees(Math.atan2(y, x)) + 90;
		if (degrees<0) {
			degrees += 360d;
		}
		return degrees;
	}
	
}
