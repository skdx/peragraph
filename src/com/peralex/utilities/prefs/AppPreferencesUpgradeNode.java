package com.peralex.utilities.prefs;

import java.io.File;

/**
 * This is used to make upgrading and moving packages around easier.
 * 
 * It will write any prefs to the new location, but when reading, will first attempt to read from
 * the new location, and if nothing is found, will then read from the old location.
 * 
 * @author Noel Grandin
 */
public class AppPreferencesUpgradeNode implements IPreferencesNode {

	/**
	 * create my own preferences node to store the state of upgrades, to make sure
	 * none of the clients of this class accidentally stomp on my state.
	 */
	private static final IPreferencesNode prefsNode = new AppPreferencesNode(AppPreferencesUpgradeNode.class);
	
	/*
	 * Explanation of the state changes, because it can be a little tricky to follow.
	 * 
	 * Start state: UPGRADED flag does not exist
	 *   Either we have no old or new data, or we have old data, but it has not been upgraded.
	 *   We do a read from the old data, and write to the new data if we find something.
	 *   As soon as the first user-write is initiated, 
	 *   we set the UPGRADED to TRUE, 
	 *   and then start reading and writing exclusively to the new data.
	 * 
	 * Start state: UPGRADED flag==TRUE
	 *   We read and write from new data, ignoring old data.
	 */
	
	private final AppPreferencesNode newNode;
	private final String oldClassName;
	private boolean usingNewNode = false;

	/**
	 * 
	 * @param oldClassName if this does not include a full package, 
	 *     we assume that only the class name changed, and not the package.
	 * @param newClass
	 */
	public AppPreferencesUpgradeNode(String oldClassName, Class<?> newClass) {
		this.newNode = new AppPreferencesNode(newClass);
		if (!oldClassName.contains(".")) {
			oldClassName = newClass.getName().substring(0, newClass.getName().lastIndexOf(".")) + "." + oldClassName;
		}
		this.oldClassName = oldClassName;
		this.usingNewNode = prefsNode.getBoolean(newClass.getName(), false);
	}
	
	public void put(String prefName, File f) {
		usingNewNode = true;
		newNode.put(prefName, f);
	}
	
	public void put(String prefName, String s) {
		usingNewNode = true;
		newNode.put(prefName, s);
	}
	
	public void put(String prefName, Enum<?> val) {
		usingNewNode = true;
		newNode.put(prefName, val);
	}
	
	public void put(String prefName, int i) {
		usingNewNode = true;
		newNode.put(prefName, i);
	}
	
	public void put(String prefName, boolean b) {
		usingNewNode = true;
		newNode.put(prefName, b);
	}
	
	public void put(String prefName, java.util.Date date) {
		usingNewNode = true;
		newNode.put(prefName, date);
	}
	
	public void put(String prefName, Double d) {
		usingNewNode = true;
		newNode.put(prefName, d);
	}

	public void put(String prefName, Integer i) {
		usingNewNode = true;
		newNode.put(prefName, i);
	}
	
	public Double getNullableDouble(String prefName, Double defaultVal) {
		if (usingNewNode) {
			return newNode.getNullableDouble(prefName, defaultVal);
		}
		return AppPreferences.getNullableDouble(oldClassName, prefName, defaultVal);
	}
	
	public Integer getNullableInteger(String prefName, Integer defaultVal) {
		if (usingNewNode) {
			return newNode.getNullableInteger(prefName, defaultVal);
		}
		return AppPreferences.getNullableInteger(oldClassName, prefName, defaultVal);
	}
	
	public int getInt(String prefName) {
		if (usingNewNode) {
			return newNode.getInt(prefName);
		}
		return AppPreferences.getInt(oldClassName, prefName);
	}
	
	public int getInt(String prefName, int def) {
		if (usingNewNode) {
			return newNode.getInt(prefName, def);
		}
		return AppPreferences.getInt(oldClassName, prefName, def);
	}
	
	public boolean getBoolean(String prefName) {
		if (usingNewNode) {
			return newNode.getBoolean(prefName);
		}
		return AppPreferences.getBoolean(oldClassName, prefName);
	}
	
	public boolean getBoolean(String prefName, boolean def) {
		if (usingNewNode) {
			return newNode.getBoolean(prefName, def);
		}
		return AppPreferences.getBoolean(oldClassName, prefName, def);
	}
	
	public java.util.Date getDate(String prefName) {
		if (usingNewNode) {
			return newNode.getDate(prefName);
		}
		return AppPreferences.getDate(oldClassName, prefName);
	}
	
	public java.util.Date getDate(String prefName, java.util.Date def) {
		if (usingNewNode) {
			return newNode.getDate(prefName, def);
		}
		return AppPreferences.getDate(oldClassName, prefName, def);
	}
	
	public String get(String prefName) {
		if (usingNewNode) {
			return newNode.get(prefName);
		}
		return AppPreferences.get(oldClassName, prefName);
	}
	
	public String get(String prefName, String def) {
		if (usingNewNode) {
			return newNode.get(prefName, def);
		}
		return AppPreferences.get(oldClassName, prefName, def);
	}
	
	public <T extends Enum<T>> T getEnum(String prefName, Class<T> enumClass, T def) {
		if (usingNewNode) {
			return newNode.getEnum(prefName, enumClass, def);
		}
		return AppPreferences.getEnum(oldClassName, prefName, enumClass, def);
	}
	
	public File getFile(String prefName) {
		if (usingNewNode) {
			return newNode.getFile(prefName);
		}
		return AppPreferences.getFile(oldClassName, prefName);
	}
	
	public boolean keyExists(String prefName) {
		if (usingNewNode) {
			return newNode.keyExists(prefName);
		}
		return AppPreferences.keyExists(oldClassName, prefName);
	}
	
	public void removeKey(String prefName) {
		if (usingNewNode) {
			newNode.removeKey(prefName);
		}
		AppPreferences.removeKey(oldClassName, prefName);
	}

}
