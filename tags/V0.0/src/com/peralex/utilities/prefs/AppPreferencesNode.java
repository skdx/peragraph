package com.peralex.utilities.prefs;

import java.io.File;

/**
 * Utility class to make using application preferences easier.
 * 
 * @author Noel Grandin
 */
public class AppPreferencesNode implements IPreferencesNode {

	private final Class<?> clazz;
	
	public AppPreferencesNode(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public void put(String prefName, File f) {
		AppPreferences.put(clazz, prefName, f);
	}
	
	public void put(String prefName, String s) {
		AppPreferences.put(clazz, prefName, s);
	}
	
	public void put(String prefName, Enum<?> val) {
		AppPreferences.putEnum(clazz, prefName, val);
	}
	
	public void put(String prefName, int i) {
		AppPreferences.putInt(clazz, prefName, i);
	}
	
	public void put(String prefName, boolean b) {
		AppPreferences.putBoolean(clazz, prefName, b);
	}
	
	public void put(String prefName, java.util.Date date) {
		AppPreferences.putDate(clazz, prefName, date);
	}
	
	public void put(String prefName, Double d) {
		AppPreferences.putNullableDouble(clazz, prefName, d);
	}

	public void put(String prefName, Integer i) {
		AppPreferences.putNullableInteger(clazz, prefName, i);
	}
	
	public Double getNullableDouble(String prefName, Double defaultVal) {
		return AppPreferences.getNullableDouble(clazz.getName(), prefName, defaultVal);
	}
	
	public Integer getNullableInteger(String prefName, Integer defaultVal) {
		return AppPreferences.getNullableInteger(clazz.getName(), prefName, defaultVal);
	}
	
	public int getInt(String prefName) {
		return AppPreferences.getInt(clazz.getName(), prefName);
	}
	
	public int getInt(String prefName, int def) {
		return AppPreferences.getInt(clazz.getName(), prefName, def);
	}
	
	public boolean getBoolean(String prefName) {
		return AppPreferences.getBoolean(clazz.getName(), prefName);
	}
	
	public boolean getBoolean(String prefName, boolean def) {
		return AppPreferences.getBoolean(clazz.getName(), prefName, def);
	}
	
	public java.util.Date getDate(String prefName) {
		return AppPreferences.getDate(clazz.getName(), prefName);
	}
	
	public java.util.Date getDate(String prefName, java.util.Date def) {
		return AppPreferences.getDate(clazz.getName(), prefName, def);
	}
	
	public String get(String prefName) {
		return AppPreferences.get(clazz.getName(), prefName);
	}
	
	public String get(String prefName, String def) {
		return AppPreferences.get(clazz.getName(), prefName, def);
	}
	
	public <T extends Enum<T>> T getEnum(String prefName, Class<T> enumClass, T def) {
		return AppPreferences.getEnum(clazz.getName(), prefName, enumClass, def);
	}
	
	public File getFile(String prefName) {
		return AppPreferences.getFile(clazz.getName(), prefName);
	}
	
	public boolean keyExists(String prefName) {
		return AppPreferences.keyExists(clazz.getName(), prefName);
	}
	
	public void removeKey(String prefName) {
		AppPreferences.removeKey(clazz.getName(), prefName);
	}

}
