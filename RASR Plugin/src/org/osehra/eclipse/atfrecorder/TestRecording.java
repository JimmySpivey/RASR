package org.osehra.eclipse.atfrecorder;

import java.util.ArrayList;
import java.util.List;

public class TestRecording {
	
	private List<RecordableEvent> events;
	private String accessCode;
	private String verifyCode;
	
	public TestRecording() {
		super();
		
		events = new ArrayList<RecordableEvent>();
	}
	
	public List<RecordableEvent> getEvents() {
		return events;
	}
	public void setEvents(List<RecordableEvent> events) {
		this.events = events;
	}
	public String getAccessCode() {
		return accessCode;
	}
	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

}
