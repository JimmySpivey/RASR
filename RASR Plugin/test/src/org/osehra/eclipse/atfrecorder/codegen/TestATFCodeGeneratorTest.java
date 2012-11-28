package org.osehra.eclipse.atfrecorder.codegen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.osehra.eclipse.atfrecorder.RecordableEvent;
import org.osehra.eclipse.atfrecorder.RecordedExpectEvent;
import org.osehra.eclipse.atfrecorder.RecordedSendEvent;
import org.osehra.eclipse.atfrecorder.TestRecording;
import org.osehra.python.codegen.LineNotFoundException;

public class TestATFCodeGeneratorTest {

	@Test
	public void testAddTest() throws FileNotFoundException, IOException, LineNotFoundException, URISyntaxException {
		ATFCodeGenerator atfCodeGen = new ATFCodeGenerator();
		List<RecordableEvent> recordableEvents = new ArrayList<RecordableEvent>();
		recordableEvents.add(new RecordedExpectEvent("ACCESS CODE:"));
		recordableEvents.add(new RecordedSendEvent("01vehu"));
		
		TestRecording testRecording = new TestRecording();
		testRecording.setAccessCode("accessCode01");
		testRecording.setVerifyCode("verifyCode01");
		testRecording.setEvents(recordableEvents);
		atfCodeGen.addTestToATF(testRecording , "my package", "rasr_demo", "new_test", "C:\\Users\\jspivey\\DEV\\GitHub\\ATF-RASR", false, false);
		
		//TODO: stub a property file for a temp directory location.
		//TODO: move stub file to temp location
		//TODO: stub property atf.location with temp location
		//TODO: parse file and add asserts
	}

//	@Test
//	public void testUpdateLocalUserConfigFile() throws URISyntaxException, IOException {
//		ATFCodeGenerator atfCodeGen = new ATFCodeGenerator();
//		TestRecording recordedSession = new TestRecording();
//		recordedSession.setAccessCode("03unittest");
//		recordedSession.setVerifyCode("unitest03");
//		atfCodeGen.updateLocalUserConfigFile("SSH Demo", "ssh_demo_suite", "dive_into_menus2", recordedSession );
//
//	}
}
