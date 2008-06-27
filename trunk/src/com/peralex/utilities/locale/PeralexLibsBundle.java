package com.peralex.utilities.locale;

import java.util.ResourceBundle;

/**
 * Utility class that handles the resource bundle for the Peralex Library.
 * 
 * This class also serves as a template for how to implement this code in other projects.
 * The intention is to reduce the amount of code clutter and other coding in all of the other
 * classes in this project.
 * 
 * @author Noel Grandin
 */
public final class PeralexLibsBundle
{
	private static final GenericBundle bundle = new GenericBundle("com.peralex.utilities");

	private PeralexLibsBundle()
	{
	}

	/**
	 * Get the current ResourceBundle.
	 */
	public static ResourceBundle getResource()
	{
		return bundle.getResource();
	}
	
	/**
	 * Gets a string for the given key from this resource bundle.
	 */
  public static String getString(String key)
  {
  	return bundle.getString(key);
  }
  
	public static void addLocaleListener(ILocaleListener oLocalListener)
	{
		bundle.addLocaleListener(oLocalListener);
	}
	
	public static void removeLocaleListener(ILocaleListener oLocalListener)
	{
		bundle.removeLocaleListener(oLocalListener);
	}
}
