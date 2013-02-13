//package org.osehra.eclipse.atfrecorder.actions;
//
//import org.eclipse.jface.action.Action;
//import org.eclipse.jface.action.IAction;
//import org.eclipse.swt.widgets.Menu;
//import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;
//
//import com.jcraft.eclipse.jcterm.IUIConstants;
//import com.jcraft.eclipse.jcterm.JCTermPlugin;
//
//public class StopIconAction extends Action {
//	
//	private IAction stopIcon;
//	
//	public StopIconAction(final ATFRecorderAWT atfRecorderAwt) {
//		super();
//		
//		stopIcon = new Action() {
//			public void run() {
//				atfRecorderAwt.disableRecording();
//				disable();
//			}
//		};
//		stopIcon.setText("Stop Recording");
//
//		//disable();
//		setText("Stop Recording");
//		setToolTipText("Stop Recording");
//		setImageDescriptor(JCTermPlugin
//				.getImageDescriptor(IUIConstants.IMG_STOP));
//	}
//	
//	public void run() {
//		stopIcon.run();
//	}
//
//	public void dispose() {
//	}
//
//	public Menu getMenu(Menu parent) {
//		return null;
//	}
//
//	public void enable() {
//		setEnabled(true);
//	}
//	
//	public void disable() {
//		setEnabled(false);
//	}
//
//}
