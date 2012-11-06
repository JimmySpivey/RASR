package org.osehra.eclipse.atfrecorder;

import org.eclipse.jface.preference.IPreferenceStore;

import com.jcraft.eclipse.jcterm.JCTermPlugin;
import com.jcraft.eclipse.jcterm.internal.Util;

public final class RASRPreferences {
	
	//Singleton
    private static class SingletonLoader {
        private static final RASRPreferences INSTANCE = new RASRPreferences();
    }
		
    public static RASRPreferences getInstance() {
        return SingletonLoader.INSTANCE;
    }
    
	private RASRPreferences() {
        if (SingletonLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
        
        plugin.savePluginPreferences();
	}
	
	private JCTermPlugin plugin = JCTermPlugin.getDefault();
	
	public static final String ATF_LOCATION 	= "PREF/ATF-LOC";
	public static final String PACKAGE_NAME		= "PREF/PKG-NAME";
	public static final String TEST_SUITE_NAME	= "PREF/STE-NAME";
	public static final String SHELL_LOCATION	= "LOCATION/SHELL"; //name left over from JCTerm
	
	private String atfLocation;
	private String packageName;
	private String testSuiteName;

	/**
	 * Used to save multiple values into 1 key entry.
	 * 
	 * @param key
	 * @param value
	 */
	public void appendValue(String key, String value) {
		value = new String(Util.toBase64(value.getBytes(), 0, value.length()));
		IPreferenceStore store = plugin.getPreferenceStore();
		String orig = store.getString(key);
		String[] values = orig.split(" ");
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value))
				return;
		}
		orig = value + (orig.length() > 0 ? " " : "") + orig;
		store.setValue(key, orig);
		plugin.savePluginPreferences();
	}

	/**
	 * Retrieves multiple values for a given single key.
	 * 
	 * @param key
	 * @return
	 */
	public String[] getValues(String key) {
		IPreferenceStore store = plugin.getPreferenceStore();
		String value = null;
		String[] values = null;
		value = store.getString(key);
		values = value.split(" ");
		for (int i = 0; i < values.length; i++) {
			values[i] = new String(Util.fromBase64(values[i].getBytes(), 0,
					values[i].length()));
		}
		return values;
	}

	/**
	 * Overwrites the value at the given key.
	 * 
	 * @param key
	 * @param value
	 */
	public void saveValue(String key, String value) {
		value = new String(Util.toBase64(value.getBytes(), 0, value.length()));
		IPreferenceStore store = plugin.getPreferenceStore();
		store.setValue(key, value);
		plugin.savePluginPreferences();
	}

	/**
	 * Retrieves multiple values for a given single key.
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		String value = plugin.getPreferenceStore().getString(key);
		return new String(Util.fromBase64(value.getBytes(), 0, value.length()));
	}
	
}
