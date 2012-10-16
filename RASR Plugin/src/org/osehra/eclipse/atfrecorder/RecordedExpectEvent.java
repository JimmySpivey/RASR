package org.osehra.eclipse.atfrecorder;

public class RecordedExpectEvent extends RecordableEvent {

	private String recordedValue;
	
	public RecordedExpectEvent(String recordedValue) {
		super();
		this.recordedValue = recordedValue;
	}
	
	@Override
	public RecordableEventType getType() {
		return RecordableEventType.EXPECT;
	}

	@Override
	public String getRecordedValue() {
		return recordedValue;
	}

	public void setRecordedValue(String recordedValue) {
		this.recordedValue = recordedValue;
	}

}
