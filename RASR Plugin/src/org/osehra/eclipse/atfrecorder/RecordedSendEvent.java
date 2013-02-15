package org.osehra.eclipse.atfrecorder;

import java.util.List;

public class RecordedSendEvent extends RecordableEvent {

	private List<String> recordedValues;
	
	public RecordedSendEvent(List<String> commands) {
		super();
		this.recordedValues = commands;
	}
	
	@Override
	public RecordableEventType getType() {
		return RecordableEventType.SEND;
	}

	@Override
	public List<String> getRecordedValues() {
		return recordedValues;
	}

//	public void setRecordedValue(String recordedValue) {
//		this.recordedValue = recordedValue;
//	}

}
