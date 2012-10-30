package org.osehra.eclipse.atfrecorder.codegen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.osehra.eclipse.atfrecorder.RecordableEvent;
import org.osehra.eclipse.atfrecorder.RecordedExpectEvent;
import org.osehra.eclipse.atfrecorder.RecordedSendEvent;
import org.osehra.python.codegen.LineNotFoundException;

public class TestATFCodeGeneratorTest {

	@Test
	public void testAddTest() throws FileNotFoundException, IOException, LineNotFoundException {
		ATFCodeGenerator atfCodeGen = new ATFCodeGenerator();
		List<RecordableEvent> recordableEvents = new ArrayList<RecordableEvent>();
		recordableEvents.add(new RecordedExpectEvent("ACCESS CODE:"));
		recordableEvents.add(new RecordedSendEvent("01vehu"));
		
		atfCodeGen.addTestToATF(recordableEvents , "rasr_demo", "new_test", "C:\\Users\\jspivey\\DEV\\GitHub\\ATF-RASR", false);
		
		//TODO: stub a property file for a temp directory location.
		//TODO: move stub file to temp location
		//TODO: stub property atf.location with temp location
		//TODO: parse file and add asserts
	}

}
