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
package com.jcraft.eclipse.jcterm.internal;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;
import com.jcraft.eclipse.jcterm.Messages;

/**
 * 
 */
class OpenConnectionDialog extends TrayDialog {

	// widgets
	private Text[] texts;
	protected Image keyLockImage;
	protected Button allowCachingButton;
	protected Text usernameField;

	protected String name;
	protected String instruction;
	protected String[] prompt;
	protected boolean[] echo;
	private String message;
	private String[] result;

	/**
	 * Creates a new2 OpenConnectionDialog.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param instruction
	 *            the instruction
	 * @param prompt
	 *            the titles for text-fields
	 * @param echo
	 *            '*' should be used or not
	 */
	public OpenConnectionDialog(Shell parentShell, String instruction,
			String[] prompt, boolean[] echo) {
		super(parentShell);

		setShellStyle(getShellStyle() | SWT.RESIZE);

		this.instruction = instruction;
		this.prompt = prompt;
		this.echo = echo;

		this.message = NLS
				.bind(Messages.OpenConnectionDialog_message,
						new String[] { ""
								+ (name != null && name.length() > 0 ? ": " + name : "") }); //NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-2$ 
	}

	/**
	 * @see Window#configureShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(message);

		// set F1 help
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(newShell, IHelpContextIds.OPEN_CONNECTION_DIALOG);

	}

	/**
	 * @see Window#create
	 */
	public void create() {
		super.create();
		if (texts.length > 0) {
			texts[0].setFocus();
		}
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
		layout.numColumns = 3;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label imageLabel = new Label(imageComposite, SWT.NONE);
		keyLockImage = JCTermPlugin.getImageDescriptor(
				IUIConstants.IMG_TERMINAL32).createImage();
		imageLabel.setImage(keyLockImage);
		GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		imageLabel.setLayoutData(data);

		/*
		 * if(message!=null){ Label messageLabel=new Label(main, SWT.WRAP);
		 * messageLabel.setText(message); data=new
		 * GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		 * data.horizontalSpan=3; data.widthHint=300;
		 * messageLabel.setLayoutData(data); }
		 */

		if (instruction != null && instruction.length() > 0) {
			Label label = new Label(main, SWT.WRAP);
			label.setText(instruction);
			data = new GridData(GridData.FILL_HORIZONTAL
					| GridData.GRAB_HORIZONTAL);
			data.horizontalSpan = 3;
			data.widthHint = 300;
			label.setLayoutData(data);
		}

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
		texts = new Text[prompt.length];

		for (int i = 0; i < prompt.length; i++) {
			new Label(parent, SWT.NONE).setText(prompt[i]);
			int flag = SWT.BORDER;
			if (!echo[i]) {
				flag |= SWT.PASSWORD;
			}
			texts[i] = new Text(parent, flag);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH);
			texts[i].setLayoutData(data);
			if (!echo[i]) {
				texts[i].setEchoChar('*');
			}
		}
	}

	/**
	 * Returns the entered values, or null if the user canceled.
	 * 
	 * @return the entered values
	 */
	public String[] getResult() {
		return result;
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
		result = new String[prompt.length];
		for (int i = 0; i < texts.length; i++) {
			result[i] = texts[i].getText();
		}
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
		result = null;
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
}
