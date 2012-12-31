package org.osehra.eclipse.atfrecorder.dialogs;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private List<String> packageList;
	private List<String> suiteList;
	
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
	
	public SaveTestDialog(Shell shell) {
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

	/**
	 * @see Window#create
	 */
	public void create() {
		super.create();
		if (testNameText != null)
			testNameText.setFocus();
	}

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
			packageList = populator.getPackageNames();
		} catch (FileNotFoundException e) {
			preferences.saveValue(RASRPreferences.PACKAGE_NAME, "");
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"ATF Location not found",
					e.getMessage());
			hadError = true;
			return;
		}
		int i = 0;
		for (String packageName : packageList) {
			packageCombo.add(packageName);
			
			//preselect the last chosen package
			if (packageName.equals(lastSelectedPkg)) {
				packageCombo.select(i);
			}
			
			i++;
		}

		//testSuiteName combo
		try {
			suiteList = populator.getSuiteNames(lastSelectedPkg);
		} catch (FileNotFoundException e) {
			preferences.saveValue(RASRPreferences.PACKAGE_NAME, "");
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"ATF Location not found",
					e.getMessage());
			hadError = true;
			return;
		}
		
		Label suiteNameLabel = new Label(parent, SWT.WRAP);
		suiteNameLabel.setText("Suite");
		suiteNameCombo = new Combo(parent, SWT.DROP_DOWN);
		
		//if any package is selected, initialize the suite combo
		if (packageCombo.getSelectionIndex() != -1)
			populateSuite();
				
		//testName text field
		Label testNameLabel = new Label(parent, SWT.WRAP);
		testNameLabel.setText("Test Name");
		
		testNameText = new Text(parent, SWT.BORDER);
		
		hadError = false;
	}

	private void populateSuite() {
		String lastSelectedSte = preferences.getValue(RASRPreferences.TEST_SUITE_NAME);
		int i = 0;
		for (String suite : suiteList) {
			suiteNameCombo.add(suite);
			
			if (suite.equals(lastSelectedSte))
				suiteNameCombo.select(i);
			i++;
		}
	}
	

	private static Pattern ValidDirPattern = Pattern.compile("(\\w+[ ]?)*\\w+");
	private static Pattern ValidNamePattern = Pattern.compile("[a-zA-Z0-9_]*");
	
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
		
		int selectedPkg = packageCombo.getSelectionIndex();
		
		//validate that the input is in fact present
		if (selectedPkg == -1 && 
				(packageCombo.getText() == null || packageCombo.getText() == "")) {
			invalidPackageInput("A package must be selected or entered.");
			return;
		}
		
		int selectedSte= suiteNameCombo.getSelectionIndex();
		if (selectedSte == -1 && 
				(suiteNameCombo.getText() == null || suiteNameCombo.getText() == "")) {
			invalidSuiteName("A suite must be selected or entered.");
			return;
		}
		
		if (testNameText.getText() == null || testNameText.getText().isEmpty()) {
			invalidTestName("A test name must be entered");
			return;
		}
		
		String packageName, testSuite, testName;
		
		if (selectedPkg != -1)
			packageName = packageCombo.getItem(selectedPkg);
		else
			packageName = packageCombo.getText();
		if (selectedSte != -1)
			testSuite = suiteNameCombo.getItem(selectedSte);
		else
			testSuite = suiteNameCombo.getText();
		testName = testNameText.getText();
		
		//validate the contents of the input
		if (!validateDir(packageName)) {
			invalidPackageInput("Package name may only contain letters, numbers and the '_' character.");
			return;
		}
		if (!validateName(testSuite)) {
			invalidSuiteName("Suite name may only contain letters, numbers and the '_' character.");
			return;
		}
		if (!validateName(testName)) {
			invalidTestName("Test name may only contain letters, numbers and the '_' character.");
			return;
		}
		
		this.packageName = packageName;
		this.testSuite = testSuite;
		this.testName = testName;
		super.okPressed();
	}

	private void invalidTestName(String message) {
		MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
				"No test name", 
				message);
		testNameText.setFocus();
	}

	private void invalidSuiteName(String message) {
		MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
				"No suite selected", 
				message);
		suiteNameCombo.setFocus();
	}

	private void invalidPackageInput(String message) {
		MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
				"Invalid Package Name", 
				message);
		packageCombo.setFocus();
	}

	private boolean validateName(String inputValue) {
		Matcher m = ValidNamePattern.matcher(inputValue);
		return m.matches();
	}

	private boolean validateDir(String inputValue) {
		Matcher m = ValidDirPattern.matcher(inputValue);
		return m.matches();
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
	
	public boolean isNewPackage() throws IllegalStateException {
		if (packageName == null)
			throw new IllegalStateException("packageName is null");
		if (packageList == null)
			throw new IllegalStateException("packageList is null");
		
		return !packageList.contains(packageName);
	}
	
	public boolean isNewSuite() throws IllegalStateException {
		if (testSuite == null)
			throw new IllegalStateException("testSuite is null");
		if (suiteList == null)
			throw new IllegalStateException("suiteList is null");
		
		return !suiteList.contains(testSuite);
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
		
		int selectedPkg = packageCombo.getSelectionIndex();
		
		if (selectedPkg == -1) {
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), 
					"No package selected", 
					"A package must be selected.");
			return;
		}
		
		//clear current selection.
		suiteNameCombo.removeAll();
		//reload the suiteNames based on the latest package selected
		try {
			suiteList = populator.getSuiteNames(packageCombo.getItem(selectedPkg));
		} catch (FileNotFoundException e) {
			preferences.saveValue(RASRPreferences.PACKAGE_NAME, "");
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"ATF Location not found",
					e.getMessage());
			hadError = true;
			return;
		}
		//update combo
		populateSuite();
		suiteNameCombo.select(0);
		//suiteNameCombo.setFocus();
	}

}
