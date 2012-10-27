package org.osehra.eclipse.atfrecorder.views;


import java.util.Map;

import org.eclipse.swt.SWT;
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
import org.osehra.eclipse.atfrecorder.internal.EVSelectedTextListener;
import org.osehra.eclipse.atfrecorder.internal.ScreenStateSourceProvider;

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
	//private String currentScreen; //not instance related, move to single method where referenced
	private EVSelectedTextListener selectedListener;
	private ScreenStateSourceProvider selectedTextProvider;

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

		ISourceProviderService service = (ISourceProviderService)getSite().getService(ISourceProviderService.class);
		ISourceProvider screenStateProvider = service.getSourceProvider(ScreenStateSourceProvider.NAME_SCREEN);
		//register our Listener View (so it can get updates to the current screen)
		screenStateProvider.addSourceProviderListener(this);
		
		selectedTextProvider = (ScreenStateSourceProvider) service
		        .getSourceProvider(ScreenStateSourceProvider.NAME_SELECTED);
		
		selectedListener = new EVSelectedTextListener(selectedTextProvider);
	    text.addListener(SWT.MouseUp, selectedListener);
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
		if (providerVarName.equals(ScreenStateSourceProvider.NAME_SCREEN)) {
			
			//System.out.println("source changed");
			//currentScreen = new String((String) providerVarValue);
			//currentScreen = (String) providerVarValue; //might want to give it its own local copy of the string so that it isn't updated when it is modified.
			final String threadString = (String) providerVarValue;
			
			//needed to put heavier work in a thread for UI performance. otherwise eclipse will throw an exception.
			new Thread(new Runnable() {
				public void run() {
					//System.out.println(currentScreen);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							String currentScreen = new String(threadString);
							text.setText(currentScreen);
							int offset = text.getCharCount() - currentScreen.length();
							int start = offset+Math.max(1, currentScreen.length() - 21);
							int end = offset+currentScreen.length();
							String last20 = text.getText(start, end);
							for (int i = last20.length() - 1; i >= 0; i--) {
								if (last20.charAt(i) == '\r' || last20.charAt(i) == '\n' || last20.charAt(i) == ' ')
									end--;
								else
									break;
							}
							for (int i = last20.length() - 2 - (text.getCharCount() - end); i >= 0; i--) {
								if (last20.charAt(i) == '\r' || last20.charAt(i) == '\n'|| last20.charAt(i) == ' ') {
									start += i + 1;
									break;
								}
							}
//							int firstCR = last20.indexOf("\r");
//							int firstLF = last20.indexOf("\n");
//							
//							int lastCR = last20.lastIndexOf("\r");
//							int lastLF = last20.lastIndexOf("\n");
//							if (lastCR != -1 || lastLF != -1)
//								start = Math.max(lastCR, lastLF) + 1;
//								//last20 = last20.substring(Math.max(lastCR, lastLF) + 1);
							
							text.setSelection(start, end);
							text.showSelection();
							String selected = text.getSelectionText();
							//System.out.println("setting start and end:" + start + " " + end);
							selectedListener.setPreviousStart(start);
							selectedListener.setPreviousEnd(end);
							selectedListener.setPreviousSelectedText(selected);
							selectedTextProvider.setSelected(text.getSelectionText());
//						    while (!text.isDisposed()) {
//						        if (!Display.getDefault().readAndDispatch())
//						        	Display.getDefault().sleep();
//						      }
						}
					});
				}
			}).start();
			

			
		}
	}
}