package org.osehra.eclipse.atfrecorder;

import java.util.List;

/**
 * This abstract class reprsents an object which will eventually be written 
 * into a python statement for Automated Testing purposes.
 * 
 * @author jspivey
 *
 */
public abstract class RecordableEvent {
	
	public abstract RecordableEventType getType();
	
	public abstract List<String> getRecordedValues();
	
}
