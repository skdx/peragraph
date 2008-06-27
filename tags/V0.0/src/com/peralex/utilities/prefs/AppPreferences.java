package com.peralex.utilities.prefs;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

/**
 * Application preferences.
 * 
 * @author Noel Grandin
 */
final class AppPreferences {

	private static final Logger logger = Logger.getLogger(AppPreferences.class);

	/**
	 * so that I can distinguish between "null" and "no value stored"
	 */
	private static final String NULL_DATE = "NULL";
	
	/** The date formatting we use for saving and restoring dates.
	 * Use a ThreadLocal because formatters are not thread-safe.
	 */
	private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	/**
	 * not meant to be instantiated.
	 */
	private AppPreferences() {
	}
	
	static void put(Class<?> clazz, String prefName, File f) {
		put(clazz, prefName, f==null ? null : f.getAbsolutePath());
	}
	
	static void put(Class<?> clazz, String prefName, String s) {
		if (s==null) {
			Util.remove(clazz.getName(), prefName);
		} else {
			Util.putString(clazz, prefName, s);
		}
	}
	
	static void putEnum(Class<?> clazz, String prefName, Enum<?> val) {
		put(clazz, prefName, val.name());
	}
	
	static void putInt(Class<?> clazz, String prefName, int i) {
		Util.putInt(clazz, prefName, i);
	}
	
	static void putBoolean(Class<?> clazz, String prefName, boolean b) {
		Util.putBoolean(clazz, prefName, b);
	}
	
	static void putDate(Class<?> clazz, String prefName, java.util.Date date) {
		if (date==null) {
			Util.putString(clazz, prefName, NULL_DATE);
		} else {
			Util.putString(clazz, prefName, DATE_FORMAT.get().format(date));
		}
	}
	
	static void putNullableDouble(Class<?> clazz, String prefName, Double d) {
		if (d==null) {
			Util.remove(clazz.getName(), prefName);
		} else {
			Util.putString(clazz, prefName, d.toString());
		}
	}

	static void putNullableInteger(Class<?>clazz, String prefName, Integer i) {
		if (i==null) {
			Util.remove(clazz.getName(), prefName);
		} else {
			Util.putString(clazz, prefName, i.toString());
		}
	}
	
	static Double getNullableDouble(String className, String prefName, Double defaultVal) {
		final String s = Util.getString(className, prefName, null);
		if (s==null) {
			return defaultVal;
		}
		try {
			return new Double(s);
		} catch (NumberFormatException ex) {
			logger.error("", ex);
			return defaultVal;
		}
	}
	
	static Integer getNullableInteger(String className, String prefName, Integer defaultVal) {
		final String s = Util.getString(className, prefName, null);
		if (s==null) {
			return defaultVal;
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			logger.error("", ex);
			return defaultVal;
		}
	}
	
	static int getInt(String className, String prefName) {
		return getInt(className, prefName, 0);
	}
	
	static int getInt(String className, String prefName, int def) {
		return Util.getInt(className, prefName, def);
	}
	
	static boolean getBoolean(String className, String prefName) {
		return getBoolean(className, prefName, false);
	}
	
	static boolean getBoolean(String className, String prefName, boolean def) {
		return Util.getBoolean(className, prefName, def);
	}
	
	static java.util.Date getDate(String className, String prefName) {
		return getDate(className, prefName, null);
	}
	
	static java.util.Date getDate(String className, String prefName, java.util.Date def) {
		final String s = Util.getString(className, prefName, null);
		if (s==null) {
			return def;
		}
		if (NULL_DATE.equals(s)) {
			return null;
		}
		try {
			return DATE_FORMAT.get().parse(s);
		} catch (ParseException ex) {
			logger.error("", ex);
			return def;
		}
	}
	
	static String get(String className, String prefName) {
		return get(className, prefName, (String) null);
	}
	
	static String get(String className, String prefName, String def) {
		return Util.getString(className, prefName, def);
	}
	
	static <T extends Enum<T>> T getEnum(String className, String prefName, Class<T> enumClass, T def) {
	    final String s = Util.getString(className, prefName, null);
	    if (s==null) {
	    	return def;
	    }
    	/* Dubious code, but will have to do 
    	 * until Sun fixes this bug
    	 * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5034509
    	 */
	    try {
	    	return Enum.valueOf(enumClass, s);
	    } catch (IllegalArgumentException ex) {
	    	return def;
	    }
	}
	
	static File getFile(String className, String prefName) {
		String s = get(className, prefName, (String) null);
		return s==null ? null : new File(s);
	}
	
	/**
	 * note that this method is not guaranteed to return an accurate answer if
	 * the key in question is nullable.
	 */
	static boolean keyExists(String className, String prefName) {
		return Util.keyExists(className, prefName);
	}

	static void removeKey(String className, String prefName)
	{
		Util.remove(className, prefName);
	}
}
