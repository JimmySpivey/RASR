package org.osehra.eclipse.atfrecorder.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.osehra.eclipse.atfrecorder.RASRPreferences;
import org.osehra.eclipse.atfrecorder.dialogs.ManageConnectionDialog;

public class ManageConnectionsAction extends Action implements IMenuCreator {

	private IAction manageConnAction;
	private RASRPreferences preferences = RASRPreferences.getInstance();

	
	public ManageConnectionsAction() {
		manageConnAction = new Action() {
			public void run() {


				ManageConnectionDialog dialog = new ManageConnectionDialog(null);
				dialog.open();
				
				//List<String> origValues = Arrays.asList(preferences.getValues(RASRPreferences.SHELL_LOCATION));
				List<String> origValues = new ArrayList<String>();
				for (String value : preferences.getValues(RASRPreferences.SHELL_LOCATION))
					origValues.add(value);
				
				for (String deleteMe : dialog.getResults())
					origValues.remove(deleteMe);
				
				preferences.saveValue(RASRPreferences.SHELL_LOCATION, "");
				for (String value: origValues)
					preferences.appendValue(RASRPreferences.SHELL_LOCATION, value);
				
			}
		};
		
		manageConnAction.setText("Manage Connections");
		
		setText("Manage Connections");
		setToolTipText("Select this to remove a connection");
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Menu getMenu(Control arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Menu getMenu(Menu arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void run() {
		manageConnAction.run();
	}

	
}
