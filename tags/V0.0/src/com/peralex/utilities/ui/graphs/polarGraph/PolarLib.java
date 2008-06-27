package com.peralex.utilities.ui.graphs.polarGraph;

/**
 * Conversion between polar and cartesian co-ordinates.
 * 
 * @author Noel Grandin
 */
public final class PolarLib
{

	/** not meant to be instantiated */
	private PolarLib() {}

	/**
	 * polar to cartesian - get X value
	 */
	public static double cartesianX(double angleInRadians, double radius)
	{
		return Math.cos(angleInRadians) * radius;
	}

	/**
	 * polar to cartesian - get X value
	 */
	public static float cartesianX(float angleInRadians, float radius)
	{
		return (float) Math.cos(angleInRadians) * radius;
	}
	
	/**
	 * polar to cartesian - get X value
	 */
	public static double cartesianY(double angleInRadians, double radius)
	{
		return Math.sin(angleInRadians) * radius;
	}

	/**
	 * polar to cartesian - get X value
	 */
	public static float cartesianY(float angleInRadians, float radius)
	{
		return (float) Math.sin(angleInRadians) * radius;
	}
	
	/**
	 * cartesian to polar - get radius
	 */
	public static double polarR(double x, double y)
	{
		return Math.sqrt(x*x + y*y);
	}

	/**
	 * cartesian to polar - get radius
	 */
	public static float polarR(float x, float y)
	{
		return (float) Math.sqrt(x*x + y*y);
	}
	
	/**
	 * cartesian to polar - get angle in radians
	 */
	public static double polarA(double x, double y)
	{
		final double radius = polarR(x,y);
		if (radius==0) {
			return 0;
		} else {
			if (y<0) {
				return Math.acos(x / radius);
			} else {
				return Math.PI + Math.PI - Math.acos(x / radius);
			}
		}
	}
	
	/**
	 * cartesian to polar - get angle in radians
	 */
	public static float polarA(float x, float y)
	{
		final float radius = polarR(x,y);
		if (radius==0) {
			return 0;
		} else {
			if (y<0) {
				return (float) Math.acos(x / radius);
			} else {
				return (float) (Math.PI + Math.PI - Math.acos(x / radius));
			}
		}
	}
	
	public static void main(String [] args)
	{
		testPolarToCartesian(0, 1, "1, 0");
		testPolarToCartesian(Math.PI, 1, "-1, 0");
		testPolarToCartesian(Math.PI/2, 1, "0, 1");
	}
	
	private static void testPolarToCartesian(double angleInRadians, double radius, String expected)
	{
		System.out.println("polar (" + angleInRadians + "," + radius + ")"
				+ "->"
				+ "(" + cartesianX(angleInRadians, radius) + "," + cartesianY(angleInRadians, radius) + ")"
				+ "   expected " + expected);
	}
}
