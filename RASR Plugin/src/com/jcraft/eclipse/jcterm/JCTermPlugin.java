/*******************************************************************************
 * Copyright (c) 2007 JCraft, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     JCraft, Inc. - initial API and implementation
 *******************************************************************************/

package com.jcraft.eclipse.jcterm;

import java.net.URL;
import java.util.Hashtable;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.jcraft.eclipse.jcterm.internal.Util;

/**
 * The activator class controls the plug-in life cycle
 */
public class JCTermPlugin extends AbstractUIPlugin {

	public static final String ID = "com.jcraft.eclipse.jcterm";

	// The shared instance
	private static JCTermPlugin plugin;

	private static Hashtable imageDescriptors = new Hashtable(20);

	/**
	 * The constructor
	 */
	public JCTermPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializeImages();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static JCTermPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the image descriptor for the given image ID. Returns null if
	 * there is no such image.
	 */
	public static ImageDescriptor getImageDescriptor(String id) {
		return (ImageDescriptor) imageDescriptors.get(id);
	}

	/**
	 * Creates an image and places it in the image registry.
	 */
	protected void createImageDescriptor(String id) {
		URL url = FileLocator.find(JCTermPlugin.getDefault().getBundle(),
				new Path(IUIConstants.ICON_PATH + id), null);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		imageDescriptors.put(id, desc);
	}

	/**
	 * Initializes the table of images used in this plugin.
	 */
	private void initializeImages() {
		createImageDescriptor(IUIConstants.IMG_TERMINAL32);
		createImageDescriptor(IUIConstants.IMG_TERMINAL16);
		createImageDescriptor(IUIConstants.IMG_SAVEADD16);
		createImageDescriptor(IUIConstants.IMG_OLD_EDIT_FIND16);
	}

	/**
	 * Used to save multiple values into 1 key entry.
	 * 
	 * @param key
	 * @param value
	 */
	public void appendValue(String key, String value) { // TODO: refactor and
														// call saveValue()
		value = new String(Util.toBase64(value.getBytes(), 0, value.length()));
		IPreferenceStore store = getPreferenceStore();
		String orig = store.getString(key);
		String[] values = orig.split(" ");
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value))
				return;
		}
		orig = value + (orig.length() > 0 ? " " : "") + orig;
		store.setValue(key, orig);
		savePluginPreferences();
	}

	/**
	 * Retrieves multiple values for a given single key.
	 * 
	 * @param key
	 * @return
	 */
	public String[] getValues(String key) { // TODO: refactor, call getPrefStore
		IPreferenceStore store = getPreferenceStore();
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
		IPreferenceStore store = getPreferenceStore();
		store.setValue(key, value);
		savePluginPreferences();
	}

	/**
	 * Retrieves multiple values for a given single key.
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		String value = getPreferenceStore().getString(key);
		return new String(Util.fromBase64(value.getBytes(), 0, value.length()));
	}
}
