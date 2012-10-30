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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.jcraft.eclipse.jcterm.messages";//$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String OpenConnectionDialog_5;
	public static String OpenConnectionDialog_6;
	public static String OpenConnectionDialog_7;

	public static String OpenConnectionDialog_required;
	public static String OpenConnectionDialog_labelUser;
	public static String OpenConnectionDialog_labelPassword;
	public static String OpenConnectionDialog_password;
	public static String OpenConnectionDialog_user;

	public static String OpenConnectionDialog_message;
	public static String OpenConnectionDialog_labelRepository;
}
