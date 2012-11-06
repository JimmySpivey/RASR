package org.osehra.eclipse.atfrecorder.internal;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;
import com.jcraft.eclipse.jcterm.Messages;
import com.jcraft.eclipse.jcterm.internal.IHelpContextIds;

public class SaveTestDialog extends TrayDialog {

	// widgets
	protected Image keyLockImage;

	protected String name;
	private String message;
	private String packageName;
	private String testSuite;
	private String testName;
	
	protected SaveTestDialog(Shell shell) {
		super(shell);
		
		setShellStyle(getShellStyle() | SWT.RESIZE);

		this.message = NLS
				.bind(Messages.SaveTestDialog_message,
						new String[] { ""
								+ (name != null && name.length() > 0 ? ": " + name : "") }); //NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String getTestName() {
		return testName;
	}

	public String getTestSuite() {
		return testSuite;
	}

	/**
	 * @see Window#configureShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(message);

		// set F1 help
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(newShell, IHelpContextIds.SAVE_TEST_DIALOG);

	}

//	/**
//	 * @see Window#create
//	 */
//	public void create() {
//		super.create();
//		if (texts.length > 0) {
//			texts[0].setFocus();
//		}
//	}

	/**
	 * @see Dialog#createDialogArea
	 */
	protected Control createDialogArea(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		top.setLayout(layout);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite imageComposite = new Composite(top, SWT.NONE);
		layout = new GridLayout();
		imageComposite.setLayout(layout);
		imageComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		Composite main = new Composite(top, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label imageLabel = new Label(imageComposite, SWT.NONE);
		keyLockImage = JCTermPlugin.getImageDescriptor(
				IUIConstants.IMG_SAVEADD16).createImage(); //TODO: change to a 32x32 image
		imageLabel.setImage(keyLockImage);
		GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		imageLabel.setLayoutData(data);

		createFields(main);

		Dialog.applyDialogFont(parent);

		return main;
	}

	/**
	 * Creates the widgets that represent the entry area.
	 * 
	 * @param parent
	 *            the parent of the widgets
	 */
	protected void createFields(Composite parent) {
		Label testSuiteLabel = new Label(parent, SWT.WRAP);
		testSuiteLabel.setText("Test Suite");
		Combo packageCombo = new Combo(parent, SWT.DROP_DOWN);
		
		//populateTestSuiteComobo
		
		String atfLoc = JCTermPlugin.getDefault().getValue("PREF/ATF-LOC");
		String sep = System.getProperty("file.separator");
		
		File packagesDir = new File(atfLoc+sep+ "FunctionalTest"+sep+"RAS"+sep+"VistA-FOIA"+sep+"Packages");
		if (!packagesDir.exists()) {
			//TODO: open error dialog
		}
		File[] packages = packagesDir.listFiles();
		for (int i = 0; i < packages.length; i++) {
			if (!packages[i].isDirectory())
				continue;
			
			packageCombo.add(packages[i].getName());
		} //TODO: pre-select the last chosen package.
		
		//testSuiteName combo
		//TODO: file iteration for each python file ending in _suite
		//idea: get rid of the 2 file system _main and _suite. just put the main() method in the _suite file.
		
		//TODO: pre-select the last test suite
		
		//testName text field
	}

	
	
	/**
	 * Notifies that the ok button of this dialog has been pressed.
	 * <p>
	 * The default implementation of this framework method sets this dialog's
	 * return code to <code>Window.OK</code> and closes the dialog. Subclasses
	 * may override.
	 * </p>
	 */
	protected void okPressed() {
		
		
		
		super.okPressed();
	}

	/**
	 * Notifies that the cancel button of this dialog has been pressed.
	 * <p>
	 * The default implementation of this framework method sets this dialog's
	 * return code to <code>Window.CANCEL</code> and closes the dialog.
	 * Subclasses may override.
	 * </p>
	 */
	protected void cancelPressed() {
		packageName = null;
		testSuite = null;
		testName = null;
		super.cancelPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 */
	public boolean close() {
		if (keyLockImage != null) {
			keyLockImage.dispose();
		}
		return super.close();
	}

	public String getPackageName() {
		return packageName;
	}

}
