package org.osehra.eclipse.atfrecorder.internal;

import java.io.FileNotFoundException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.osehra.eclipse.atfrecorder.RASRPreferences;
import org.osehra.eclipse.atfrecorder.SaveTestMenuPopulator;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;
import com.jcraft.eclipse.jcterm.Messages;
import com.jcraft.eclipse.jcterm.internal.IHelpContextIds;

public class SaveTestDialog extends TrayDialog implements SelectionListener {

	//dependencies
	private RASRPreferences preferences = RASRPreferences.getInstance();
	private SaveTestMenuPopulator populator = new SaveTestMenuPopulator();
	
	// widgets
	protected Image keyLockImage;
	protected Combo packageCombo;
	protected Combo suiteNameCombo;
	protected Text testNameText;
	
	protected String name;
	private String message;
	private String packageName;
	private String testSuite;
	private String testName;
	
	private Boolean hadError;
	
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
		layout.numColumns = 2;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label imageLabel = new Label(imageComposite, SWT.NONE);
		keyLockImage = JCTermPlugin.getImageDescriptor(
				IUIConstants.IMG_SAVEADD32).createImage();
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
		Label packageNameLabel = new Label(parent, SWT.WRAP);
		packageNameLabel.setText("Package");
		packageCombo = new Combo(parent, SWT.DROP_DOWN);
		packageCombo.addSelectionListener(this);
		
		String lastSelectedPkg = preferences.getValue(RASRPreferences.PACKAGE_NAME);
		
		//Populate the package pull down menu
		try {
			int i = 0;
			for (String packageName : populator.getPackageNames()) {
				packageCombo.add(packageName);
				
				//preselect the last chosen package
				if (packageName.equals(lastSelectedPkg)) {
					packageCombo.select(i);
				}
				
				i++;
			}
		} catch (FileNotFoundException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"ATF Location not found",
					e.getMessage());
			return;
		}
		
		//testSuiteName combo
		Label suiteNameLabel = new Label(parent, SWT.WRAP);
		suiteNameLabel.setText("Suite");

		suiteNameCombo = new Combo(parent, SWT.DROP_DOWN);
		
		//if any package is selected, initialize the suite combo
		if (packageCombo.getSelectionIndex() != -1)
			populateSuite(lastSelectedPkg);
				
		//testName text field
		Label testNameLabel = new Label(parent, SWT.WRAP);
		testNameLabel.setText("Test Name");
		
		testNameText = new Text(parent, SWT.BORDER);
		
		hadError = false;
	}

	private void populateSuite(String lastSelectedPkg) {
		String lastSelectedSte = preferences.getValue(RASRPreferences.TEST_SUITE_NAME);
		try {
			int i = 0;
			for (String suite : populator.getSuiteNames(lastSelectedPkg)) {
				suiteNameCombo.add(suite);
				
				if (suite.equals(lastSelectedSte))
					suiteNameCombo.select(i);
				i++;
			}
		} catch (FileNotFoundException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"ATF Location not found",
					e.getMessage());
			return;
		}
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
		if (hadError) {
			super.okPressed();
			return;
		}
		
		int selectedPkg= packageCombo.getSelectionIndex();
		
		if (selectedPkg == -1) {
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
					"No package selected", 
					"A package must be selected.");
			
			super.okPressed();
			return;
		}
		
		int selectedSte= suiteNameCombo.getSelectionIndex();
		if (selectedSte == -1) {
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
					"No suite selected", 
					"A suite must be selected.");
			
			super.okPressed();
			return;
		}
		
		packageName = packageCombo.getItem(selectedPkg);
		testSuite = suiteNameCombo.getItem(selectedSte);
		testName = testNameText.getText();
		//TODO: can put validations here for testSuiteName to prevent the dialog window from closing on the user (???)
		
		//TODO: add validations for testName... can't have spaces or other characters invalid for python function
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
		if (hadError) {
			super.cancelPressed();
			return;
		}
		
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

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		System.out.println("widgetDefaultSelected");
		if (hadError)
			return;
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		if (hadError)
			return;
		
		int selectedPkg= packageCombo.getSelectionIndex();
		
		if (selectedPkg == -1) {
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
					"No package selected", 
					"A package must be selected.");
			
			return;
		}
		
		//clear current selection.
		suiteNameCombo.removeAll();
		
		populateSuite(packageCombo.getItem(selectedPkg));
	}

}
