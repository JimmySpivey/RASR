package org.osehra.eclipse.atfrecorder.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class ScreenStateSourceProvider extends AbstractSourceProvider {
	
	public final static String NAME_SCREEN = "org.osehra.rasr.sourceprovider.screen"; //Note: everytime the read buffer gets new input, the view should be updated.
	public final static String NAME_SELECTED = "org.osehra.rasr.sourceprovider.selected";
	private String currentScreen = ""; //may want to change this to string buffer if there are too many reads (ie: per byte)
	private String selected = "";
	
	@Override
	public void dispose() {

	}

	@Override
	public Map getCurrentState() {
		Map map = new HashMap(1);
		map.put(NAME_SCREEN, currentScreen);
		map.put(NAME_SELECTED, selected);
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { NAME_SCREEN, NAME_SELECTED };
	}
	
	public String getCurrentScreen() {
		return currentScreen;
	}

	public void setCurrentScreen(String currentScreen) {
		this.currentScreen = currentScreen;
		fireSourceChanged(ISources.WORKBENCH, NAME_SCREEN, currentScreen);
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
		fireSourceChanged(ISources.WORKBENCH, NAME_SELECTED, selected);

	}
}
