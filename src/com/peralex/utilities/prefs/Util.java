package com.peralex.utilities.prefs;

import java.util.prefs.Preferences;

/**
 * Kind of a wrapper interface over the normal Preferences API because
 * the standard API works at the package level, while I need to save stuff at the
 * class level.
 * 
 * @author Noel Grandin
 */
final class Util {

	/** not meant to be instantiated */
	private Util() {}
	
	private static Preferences userNodeForClass(Class<?> clazz) {
		final Preferences prefs = Preferences.userNodeForPackage(clazz);
		return prefs.node(clazz.getSimpleName());
	}
	
	private static Preferences userNodeForClass(String className) {
        final int pkgEndIndex = className.lastIndexOf('.');
        String packageName = className.substring(0, pkgEndIndex);
        String simpleClassName = className.substring(pkgEndIndex+1);
        String packagePath = "/" + packageName.replace('.', '/');
        
        final Preferences prefs = Preferences.userRoot().node(packagePath);
		return prefs.node(simpleClassName);
	}
	
	static void remove(String className, String prefName) {
		final Preferences prefs = userNodeForClass(className);
		prefs.remove(prefName);
	}
	
	static void putString(Class<?> clazz, String prefName, String value) {
		final Preferences prefs = userNodeForClass(clazz);
		prefs.put(prefName, value);
	}
	
	static void putInt(Class<?> clazz, String prefName, int i) {
		final Preferences prefs = userNodeForClass(clazz);
	    prefs.putInt(prefName, i);
	}
	
	static void putBoolean(Class<?> clazz, String prefName, boolean b) {
		final Preferences prefs = userNodeForClass(clazz);
	    prefs.putBoolean(prefName, b);
	}
	
	static String getString(String className, String prefName, String defaultValue) {
		final Preferences prefs = userNodeForClass(className);
	    return prefs.get(prefName, defaultValue);
	}

	/**
	 * note that this method is not guaranteed to return an accurate answer if
	 * the key in question is nullable.
	 */
	static boolean keyExists(String className, String prefName) {
		final Preferences prefs = userNodeForClass(className);
		return prefs.get(prefName, null) != null;
	}
	
	static int getInt(String className, String prefName, int defaultValue) {
		final Preferences prefs = userNodeForClass(className);
	    return prefs.getInt(prefName, defaultValue);
	}
	
	static boolean getBoolean(String className, String prefName, boolean defaultValue) {
		final Preferences prefs = userNodeForClass(className);
	    return prefs.getBoolean(prefName, defaultValue);
	}
}