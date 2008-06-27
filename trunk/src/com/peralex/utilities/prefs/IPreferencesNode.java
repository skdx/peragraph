package com.peralex.utilities.prefs;

import java.io.File;

/**
 * Utility class to make using application preferences easier.
 * 
 * @author Noel Grandin
 */
public interface IPreferencesNode {

	void put(String prefName, File f);
	
	void put(String prefName, String s);
	
	void put(String prefName, Enum<?> val);
	
	void put(String prefName, int i);
	
	void put(String prefName, boolean b);
	
	void put(String prefName, java.util.Date date);
	
	void put(String prefName, Double d);

	void put(String prefName, Integer i);
	
	Double getNullableDouble(String prefName, Double defaultVal);
	
	Integer getNullableInteger(String prefName, Integer defaultVal);
	
	int getInt(String prefName);
	
	int getInt(String prefName, int def);
	
	boolean getBoolean(String prefName);
	
	boolean getBoolean(String prefName, boolean def);
	
	java.util.Date getDate(String prefName);
	
	java.util.Date getDate(String prefName, java.util.Date def);
	
	String get(String prefName);
	
	String get(String prefName, String def);
	
	<T extends Enum<T>> T getEnum(String prefName, Class<T> enumClass, T def);
	
	File getFile(String prefName);
	
	boolean keyExists(String prefName);
	
	void removeKey(String prefName);
}
