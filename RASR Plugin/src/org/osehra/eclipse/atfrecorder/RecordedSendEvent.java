package org.osehra.eclipse.atfrecorder;

public class RecordedSendEvent extends RecordableEvent {

	private String recordedValue;
	
	public RecordedSendEvent(String recordedValue) {
		super();
		this.recordedValue = recordedValue;
	}
	
	@Override
	public RecordableEventType getType() {
		return RecordableEventType.SEND;
	}

	@Override
	public String getRecordedValue() {
		return recordedValue;
	}

	public void setRecordedValue(String recordedValue) {
		this.recordedValue = recordedValue;
	}

}
