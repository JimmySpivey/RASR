package org.osehra.eclipse.atfrecorder.internal;

import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings("restriction")
public class GenericNotificationPopup extends AbstractNotificationPopup {

	private String title;
	private String message;
	
	public GenericNotificationPopup(Display display, String title, String message) {
		super(display);
		
		this.title = title;
		this.message = message;
	}

	@Override
	protected void createContentArea(Composite composite) {
		composite.setLayout(new GridLayout(1, true));
		Label testLabel = new Label(composite, SWT.WRAP);
		testLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
 
		testLabel.setText(message);
		testLabel.setBackground(composite.getBackground());
	}
 
	@Override
	protected String getPopupShellTitle() {
		return title;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

 
//	@Override
//	protected Image getPopupShellImage(int maximumHeight) {
//		// Use createResource to use a shared Image instance of the ImageDescriptor
//		return (Image) Activator.getImageDescriptor("/icons/information.png")
//				.createResource(Display.getDefault());
//	}
	
}
