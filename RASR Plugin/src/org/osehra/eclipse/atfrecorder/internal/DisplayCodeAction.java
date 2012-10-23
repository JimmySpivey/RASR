package org.osehra.eclipse.atfrecorder.internal;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Menu;
import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;
import org.osehra.eclipse.atfrecorder.codegen.ATFCodeGenerator;
import org.osehra.python.codegen.LineNotFoundException;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;

public class DisplayCodeAction extends Action {

	// private JCTermView term;
	private IAction displayCode;
	private ATFCodeGenerator atfCodeGen;

	public DisplayCodeAction(final ATFRecorderAWT atfRecorderAwt) {
		super();
		
		atfCodeGen = new ATFCodeGenerator();

		displayCode = new Action() {
			public void run() {
				//TODO: implement
			}
		};
		displayCode.setText("Display Test");

		setText("Display Test");
		setToolTipText("Display Test");
		setImageDescriptor(JCTermPlugin
				.getImageDescriptor(IUIConstants.IMG_TEXT_X_PYTHON));
	}

	public void run() {
		displayCode.run();
	}

	public void dispose() {
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
}
