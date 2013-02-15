package org.osehra.eclipse.atfrecorder;

import java.util.List;

public class RecordedExpectEvent extends RecordableEvent {

	private List<String> recordedValue;
	
	public RecordedExpectEvent(List<String> recordedValues) {
		super();
		this.recordedValue = recordedValues;
	}
	
	@Override
	public RecordableEventType getType() {
		return RecordableEventType.EXPECT;
	}

	@Override
	public List<String> getRecordedValues() {
		return recordedValue;
	}

//	public void setRecordedValue(String recordedValue) {
//		this.recordedValue = recordedValue;
//	}

}
