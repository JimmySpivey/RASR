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

import java.awt.Frame;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;
import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;
import org.osehra.eclipse.atfrecorder.RASRPreferences;
import org.osehra.eclipse.atfrecorder.actions.ManageConnectionsAction;
import org.osehra.eclipse.atfrecorder.actions.SetATFDirAction;
import org.osehra.eclipse.atfrecorder.actions.RecordingIconAction;
import org.osehra.eclipse.atfrecorder.actions.SaveTestAction;
import org.osehra.eclipse.atfrecorder.actions.SetCMakeOutDirAction;
import org.osehra.eclipse.atfrecorder.actions.StopIconAction;

import com.jcraft.eclipse.jcterm.internal.OpenConnectionAction;
import com.jcraft.eclipse.jsch.core.IJSchLocation;
import com.jcraft.eclipse.jsch.core.JSchCoreException;
import com.jcraft.eclipse.jsch.core.JSchLocation;
import com.jcraft.eclipse.jsch.core.JSchLocationAdapter;
import com.jcraft.eclipse.jsch.core.JSchSession;
import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.Term;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;

public class JCTermView extends ViewPart {

	private static final String ID = "com.jcraft.eclipse.jcterm.view";

	public static final int SHELL = 0;

	private String xhost = "127.0.0.1";
	private int xport = 0;
	private boolean xforwarding = false;

	private JSchSession jschsession = null;

	private int compression = 0;

	private Term term = null;

	private Connection connection = null;
	private String location = null;

	public static final String INFO_PASSWORD = "com.jcraft.eclipse.jcterm.password";//$NON-NLS-1$
	public static final String AUTH_SCHEME = ""; //$NON-NLS-1$ 
	public static final URL FAKE_URL;
	
	private RASRPreferences preferences = RASRPreferences.getInstance();

	static {
		URL temp = null;
		try {
			temp = new URL("http://com.jcraft.eclipse.jcterm");//$NON-NLS-1$ 
		} catch (MalformedURLException e) {
			// Should never fail
		}
		FAKE_URL = temp;
	}

	public JCTermView() {
	}

	Composite container = null;
	Frame frame = null;

	public void createPartControl(Composite parent) {
		
		//manage the parent's layout
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		parent.setLayout(gridLayout);
		
		//create the top-bar for RASR
//		Composite topBar = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
//		GridData gridData = new GridData();
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = false;
//		topBar.setLayoutData(gridData);
		
		//TODO: just create a green flag icon that is dimmed when it is not recording.
		
//		Label recordingIcon = new Label(topBar, SWT.NONE);
//		recordingIcon.setText("BLAH");
//		AnimatorThread at = new AnimatorThread(recordingIcon,
//				"ATF_Recorder_Plugin",
//				IUIConstants.ICON_PATH+"recording_off.gif");
	
		//create the frame which holds the terminal
		container = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		container.setLayoutData(gridData);
		frame = org.eclipse.swt.awt.SWT_AWT.new_Frame(container);
		

		container.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				Rectangle bounds = container.getBounds();
				if (bounds.width == 0 || bounds.height == 0)
					return;
				if (term != null && term instanceof ATFRecorderAWT)
					((ATFRecorderAWT) term)
							.setSize(bounds.width, bounds.height);
			}

		});
		container.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// System.out.println("keyPressed: "+e);
				int code = -1;
				switch (e.keyCode) {
				case 9: // TAB
					code = 9;
					break;
				case SWT.ARROW_DOWN:
					code = java.awt.event.KeyEvent.VK_DOWN;
					break;
				case SWT.ARROW_UP:
					code = java.awt.event.KeyEvent.VK_UP;
				case SWT.ARROW_LEFT:
					code = java.awt.event.KeyEvent.VK_LEFT;
					break;
				case SWT.ARROW_RIGHT:
					code = java.awt.event.KeyEvent.VK_RIGHT;
					break;
				}
				if (code != -1) {
					if (term instanceof ATFRecorderAWT) {
						((ATFRecorderAWT) term).keyTypedCode(code);
						((ATFRecorderAWT) term).requestFocusInWindow();
					}
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		container.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// System.out.println("focusGained: "+e);
				frame.requestFocus();
				if (term instanceof ATFRecorderAWT) {
					((ATFRecorderAWT) term).requestFocusInWindow();
				}
			}

			public void focusLost(FocusEvent e) {
			}
		});

		frame.setFocusable(true);
		frame.setFocusableWindowState(true);

		// pass in the sourceProviderService so that the term can communicate
		// between views.
		term = new ATFRecorderAWT((ISourceProviderService) getSite()
				.getService(ISourceProviderService.class));
		frame.add((ATFRecorderAWT) term);
		//frame.pack();

		frame.addKeyListener((java.awt.event.KeyListener) term);

		setPartName("RAS Recorder"); //$NON-NLS-1$
		makeAction();
		
		//setup for first run
		//if the ATF Location is not set or is set to an invalid location.
		String atfLoc = preferences.getValue(RASRPreferences.ATF_LOCATION);
		if (atfLoc == null || atfLoc.equals("")) {
//			GenericNotificationPopup popup = new GenericNotificationPopup(Display.getDefault(), 
//					"ATF Location not set", 
//					"The ATF Location has not yet been set. Please set an ATF Location from the RAS Recorder view.");
//			popup.create();
//			popup.open();
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), 
					"ATF Location not set", 
					"The ATF Location has not yet been set. Please set an ATF Location from the RAS Recorder view.");
		}
	}

	private void makeAction() {
		IActionBars bars = getViewSite().getActionBars();
		ATFRecorderAWT recorder = (ATFRecorderAWT) term;

		// Display Code Button
		// org.osehra.eclipse.atfrecorder.internal.DisplayCodeAction
		// displayCodeAction=new
		// org.osehra.eclipse.atfrecorder.internal.DisplayCodeAction(
		// (ATFRecorderAWT) term);
		// bars.getToolBarManager().add(displayCodeAction);

		//Stop Icon
		StopIconAction stopIcon = new StopIconAction(recorder);
		recorder.setStopIcon(stopIcon);
		bars.getToolBarManager().add(stopIcon);
		
		//Recording Icon
		RecordingIconAction recordingIcon = new RecordingIconAction(recorder, stopIcon);
		recorder.setRecordingIcon(recordingIcon);
		bars.getToolBarManager().add(recordingIcon);

		// Save Test Button
		SaveTestAction saveTestAction = new SaveTestAction(recorder);
		bars.getToolBarManager().add(saveTestAction);
		
		//Recording image
		//bars.getToolBarManager().ad

		// Connect Menu
		IAction openConnection = new OpenConnectionAction(this);
		bars.getToolBarManager().add(openConnection);
//		IAction mangageConnections = new ManageConnectionsAction();
//		bars.getToolBarManager().add(mangageConnections);

		// pull down menu
		IMenuManager manager = bars.getMenuManager();
		//preferences action
		SetATFDirAction setAtfLocAction = new SetATFDirAction();
		manager.add(setAtfLocAction);
		
		SetCMakeOutDirAction cmakeOutAction = new SetCMakeOutDirAction();
		manager.add(cmakeOutAction);
		
		
//		//remove a connection
//		manager.add(new Separator());
//		ManageConnectionsAction mca = new ManageConnectionsAction();
//		manager.add(mca);
	}

	public void openConnection(int mode, String location) {

		if (connection != null) {
			JCTermView view = getUnusedView();
			if (view != this) {
				view.openConnection(mode, location);
				return;
			}
			// TODO
			return;
		}

		this.location = location;

		try {
			IProgressMonitor monitor = new NullProgressMonitor();
			jschsession = JSchSession.getSession(
					getJSchLocation(this.location), monitor);
			if (jschsession == null) {
				// TODO
				this.location = null;
				return;
			}

			jschsession.getSession().setServerAliveInterval(60000);
			setCompression(compression);
		} catch (Exception e) {
			// System.out.println(e);
			// break;
			return;
		}

		try {
			Channel channel = null;
			OutputStream out = null;
			InputStream in = null;

			if (mode == SHELL) {
				channel = jschsession.getSession().openChannel("shell"); //$NON-NLS-1$

				if (xforwarding) {
					jschsession.getSession().setX11Host(xhost);
					jschsession.getSession().setX11Port(xport + 6000);
					channel.setXForwarding(true);
				}

				((ChannelShell) channel).setAgentForwarding(true);

				out = channel.getOutputStream();
				in = channel.getInputStream();
				System.out.println("setting xterm");
				((ChannelShell) channel).setPtyType("xterm");
				//abstract Channel |extended by-> ChannelSession |extended by-> ChannelShell
				channel.connect(); //calls abstract channel#start(), calls ChannelShell#start, calls ChannelSession#sendRequests
				((ChannelShell) channel).setPtySize(term.getColumnCount(),
						term.getRowCount(), term.getTermWidth(),
						term.getTermHeight()); //TODO: perhaps this should be moved to before connect?
			}

			final OutputStream fout = out;
			final InputStream fin = in;
			final Channel fchannel = channel;

			Connection connection = new Connection() {
				public InputStream getInputStream() {
					return fin;
				}

				public OutputStream getOutputStream() {
					return fout;
				}

				public void requestResize(Term term) {
					if (fchannel instanceof ChannelShell) {
						int c = term.getColumnCount();
						int r = term.getRowCount();
						((ChannelShell) fchannel).setPtyType("xterm");
						((ChannelShell) fchannel).setPtySize(c, r,
								c * term.getCharWidth(),
								r * term.getCharHeight());
					}
				}

				public void close() {
					fchannel.disconnect();
				}
			};

			start(connection);
		} catch (Exception e) {
			// System.out.println(e);
			// break;
		}
	}

	private void start(Connection connection) {
		this.connection = connection;
		Thread termThread = new Thread(new Runnable() {
			public void run() {
				setPartName(location);
				term.start(JCTermView.this.connection);
				if (JCTermView.this.connection != null)
					JCTermView.this.connection.close();
				JCTermView.this.connection = null;
				setPartName("RAS Recorder");
			}
		});
		setFocus();
		termThread.start();
	}

	public void setPartName(final String name) {
		final Display display = Display.getDefault();
		if (display == null) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				JCTermView.super.setPartName(name);
			}
		});
	}

	private void setCompression(int compression) {
		this.compression = compression;
		if (jschsession == null || !jschsession.getSession().isConnected()) {
			return;
		}
		java.util.Properties config = new java.util.Properties();
		if (compression == 0) {
			config.put("compression.s2c", "none"); //$NON-NLS-1$ //$NON-NLS-2$
			config.put("compression.c2s", "none"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			config.put("compression.s2c", "zlib,none"); //$NON-NLS-1$ //$NON-NLS-2$
			config.put("compression.c2s", "zlib,none"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try {
			jschsession.getSession().setConfig(config);
			jschsession.getSession().rekey();
		} catch (Exception e) {
		}
	}

	public void setFocus() {
		container.setFocus();

		if (term instanceof ATFRecorderAWT) {
			((ATFRecorderAWT) term).requestFocusInWindow();
		}
	}

	//TODO: rework this to only have 1 view in eclipse at a time
	private JCTermView getUnusedView() {
		JCTermView view = this;
		if (view.connection != null) {
			try {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				IViewPart v = page.showView(JCTermView.ID);

				if (v == null) 
					return null;

				if (v instanceof JCTermView)
					view = (JCTermView) v;
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return view;
	}

	private IJSchLocation getJSchLocation(final String location) {
		IJSchLocation _location = null;
		try {
			_location = JSchLocation.fromString(location);
		} catch (JSchCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		_location = new JSchLocationAdapter(_location) {
			public void flushUserInfo() {
				flushCache();
			}

			private void flushCache() {
				try {
					Platform.flushAuthorizationInfo(FAKE_URL, location,
							AUTH_SCHEME);
				} catch (CoreException e) {
				}
			}

			public void setAllowCaching(boolean value) {
				if (value)
					updateCache();
				else
					flushCache();
			}

			private boolean updateCache() {
				// put the password into the Platform map
				Map map = Platform.getAuthorizationInfo(FAKE_URL, location,
						AUTH_SCHEME);
				if (map == null) {
					map = new java.util.HashMap(10);
				}
				if (getPassword() != null)
					map.put(INFO_PASSWORD, getPassword());
				try {
					Platform.addAuthorizationInfo(FAKE_URL, location,
							AUTH_SCHEME, map);
				} catch (CoreException e) {
					// We should probably wrap the CoreException here!
					return false;
				}
				return true;
			}
		};

		String password = null;
		Map map = Platform
				.getAuthorizationInfo(FAKE_URL, location, AUTH_SCHEME);
		if (map != null) {
			password = (String) map.get(INFO_PASSWORD);
		}
		if (password != null)
			_location.setPassword(password);
		return _location;
	}
}
