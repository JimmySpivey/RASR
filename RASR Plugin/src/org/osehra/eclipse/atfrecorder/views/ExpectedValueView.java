package org.osehra.eclipse.atfrecorder.views;


import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ExpectedValueView extends ViewPart implements ISourceProviderListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.osehra.eclipse.atfrecorder.views.OverrideExpectView";

	private Text text;
	private String currentScreen;

	public ExpectedValueView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		text = new Text(parent, SWT.V_SCROLL );
		text.setText("");
		Device device = Display.getCurrent();
		text.setBackground(new Color(device, 0, 0, 0));
		text.setEditable(false);
		text.setForeground(new Color(device, 255, 255, 255));
		FontData fd = new FontData("Courier New", 10, 0);
		text.setFont(new Font(device, fd)); //TODO: add backup fonts/test for other supported OS'es
		text.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				System.out.println("widgetSelected");
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				System.out.println("widgetDefaultSelected");				
			}
		}); //doesn't work on selected text
				
		//register our Listener View (so it can get updates to the current screen)
		ISourceProviderService service = (ISourceProviderService)getSite().getService(ISourceProviderService.class);
		ISourceProvider screenStateProvider = service.getSourceProvider("org.osehra.rasr.sourceprovider.screen");
		screenStateProvider.addSourceProviderListener(this);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		text.setFocus();
		//viewer.getControl().setFocus();
	}

	@Override
	public void sourceChanged(int arg0, @SuppressWarnings("rawtypes") Map arg1) {
		System.out.println("source changed via map");
	}

	@Override
	public void sourceChanged(int arg0, String providerVarName, Object providerVarValue) {
		//it should always equals this since we just have 1 variable in our provider and we only listen to that one provider
		if (providerVarName.equals("org.osehra.rasr.sourceprovider.screen")) {
			
			System.out.println("source changed");
			//currentScreen = new String((String) providerVarValue);
			currentScreen = (String) providerVarValue; //might want to give it its own local copy of the string so that it isn't updated when it is modified.
			
			//needed to put heavier work in a thread for UI performance. otherwise eclipse will throw an exception.
			new Thread(new Runnable() {
				public void run() {
					System.out.println(currentScreen);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							text.setText(currentScreen);
							int offset = text.getCharCount() - currentScreen.length();
							text.setSelection(offset+Math.max(1, currentScreen.length() - 21), offset+currentScreen.length() );
							text.showSelection();
						    while (!text.isDisposed()) {
						        if (!Display.getDefault().readAndDispatch())
						        	Display.getDefault().sleep();
						      }
						}
					});
				}
			}).start();
			

			
		}
	}
}