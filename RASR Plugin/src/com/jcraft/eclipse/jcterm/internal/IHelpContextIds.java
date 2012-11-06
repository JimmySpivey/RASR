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

import com.jcraft.eclipse.jcterm.JCTermPlugin;

public interface IHelpContextIds {

	public static final String PREFIX = JCTermPlugin.ID + "."; //$NON-NLS-1$

	// Dialogs
	public static final String OPEN_CONNECTION_DIALOG = PREFIX
			+ "keyboard_interactive_dialog_context"; //$NON-NLS-1$

	public static final String SAVE_TEST_DIALOG = PREFIX //TODO: change this value to one specific to saveTest? (ie: different from open conn dialog)
			+ "keyboard_interactive_dialog_context"; //$NON-NLS-1$
}
