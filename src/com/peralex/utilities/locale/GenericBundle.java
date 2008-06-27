package com.peralex.utilities.locale;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.peralex.utilities.ui.errordialog.ErrorDialog;

/**
 * Contains generic code for handling bundle updates.
 * 
 * The way to use this code is to make your own cXXXResourceBundle class which uses cGenericBundle internally. Have a
 * look at cPeralexLibsBundle to see how it is done.
 * 
 * Each program/library should have it's own cXXXResourceBundle class because each cXXXResourceBundle class is tied to a
 * specific set of resource properties files.
 * 
 * @author Noel Grandin
 */
public class GenericBundle
{

	/** when customers localise our software, they are likely to make mistakes, and it's not
	 * very friendly to shut the program down.
	 * Turning this on will (a) show a dialog when a resource is missing (b) swallow the exception
	 * and return the key instead.
	 */
	public static boolean displayDialogOnMissingResource = false;
	
	private final String bundleName;

	/** The resource bundle used for multilingual support */
	private ResourceBundle textResource;

	/**
	 * Locale listener list. 
	 * Because cGenericBundle is normally declared as a class-static field (i.e. global), we can't
	 * maintain a normal reference to the ILocaleListener's. Use a weak-reference to allow the listeners to be GC'ed when
	 * they are no longer in use.
	 */
	private final List<WeakReference<ILocaleListener>> oLocaleListeners = new ArrayList<WeakReference<ILocaleListener>>();

	/**
	 * we return a handle on a ResourceBundle, so in order to catch the MissingResourceException
	 * I have to provide a Facade object.
	 */
	private final class ExceptionSwallowingResourceBundle extends ResourceBundle {
		@Override
		public Enumeration<String> getKeys()
		{
			return textResource.getKeys();
		}
		@Override
		protected Object handleGetObject(String key)
		{
			try {
				return textResource.getObject(key);
			} catch (MissingResourceException ex) {
				if (displayDialogOnMissingResource) {
					ErrorDialog.show(ex);
					return key;
				} else {
					throw ex;
				}
			}
		}
	}
	private final ExceptionSwallowingResourceBundle exceptionSwallower = new ExceptionSwallowingResourceBundle();
	
	/** Creates a new instance of cBundle */
	public GenericBundle(String bundleName)
	{
		this.bundleName = bundleName;
		this.textResource = ResourceBundle.getBundle(bundleName + ".resources.textRes", Locale.getDefault());
	}

	/**
	 * Get the current ResourceBundle.
	 */
	public ResourceBundle getResource()
	{
		return exceptionSwallower;
	}

	/**
	 * Gets a string for the given key from this resource bundle. If the corresponding string can't be found, we return
	 * the key value.
	 */
	public String getString(String key)
	{
		try {
			return textResource.getString(key);
		} catch (MissingResourceException ex) {
			ex.printStackTrace();
			return key;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////
	// Locale Listener methods
	// ////////////////////////////////////////////////////////////////////////////
	public void addLocaleListener(ILocaleListener oLocalListener)
	{
		oLocaleListeners.add(new WeakReference<ILocaleListener>(oLocalListener));
	}

	public void removeLocaleListener(ILocaleListener oLocalListener)
	{
		for (Iterator<WeakReference<ILocaleListener>> iter = oLocaleListeners.iterator(); iter
				.hasNext();)
		{
			WeakReference<ILocaleListener> ref = iter.next();
			if (ref.get() == oLocalListener)
			{
				iter.remove();
				break;
			}
		}
	}
}
