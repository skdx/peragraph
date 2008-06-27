package com.peralex.utilities.ui.graphs;

import java.awt.Color;

/**
 * Some utility functions for direction stuff.
 * 
 * @author Noel Grandin
 */
public final class DirectionLib
{

	/** not meant to be instantiated */
	private DirectionLib() {}
	
  /**
   * Maps a direction float value in the range 0 to 360 to a color, using a
   *  rainbow spectrum, where zero is RED and 180 is BLUE
   */
  public static int degreesToRGB(float deg)
  {
  	if (deg<0) deg += 360f;
  	if (deg>=360f) deg -= 360f;
  	final float x = deg / 360f;
    return  0xff000000 | Color.HSBtoRGB(x, 0.65f, 0.9f);
  }
  
  /**
   * Maps a direction float value in the range 0 to 360 to a color, using a
   *  rainbow spectrum, where zero is RED and 180 is BLUE
   */
  public static Color degreesToColor(float deg)
  {
  	if (deg<0) deg += 360f;
  	if (deg>=360f) deg -= 360f;
  	final float x = deg / 360f;
    return new Color(Color.HSBtoRGB(x, 0.65f, 0.9f));
  }
  
	/**
	 * Maps a float value in the range 0 to 1 to a color, using a rainbow spectrum.
	 */
  public static Color unitToColor(float x)
	{
		return new Color(Color.HSBtoRGB(x, 0.65f, 0.9f));
	}
}
