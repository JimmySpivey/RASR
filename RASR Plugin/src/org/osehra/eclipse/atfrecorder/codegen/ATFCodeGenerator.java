package org.osehra.eclipse.atfrecorder.codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.osehra.eclipse.atfrecorder.RecordableEvent;
import org.osehra.eclipse.atfrecorder.RecordableEventType;
import org.osehra.python.codegen.LineNotFoundException;
import org.osehra.python.codegen.PythonGenerator;
import org.osehra.python.codegen.PythonScriptEditor;

/*
 * 
 * usage:
 * 
 * How does the user pick a test Suite? if they have a typo it will create a new suite.
 * RASR should prompt them radio option list or let them create a new test.
 * 
 * 
 * A flag will be passed into which let's me know if this is a new Test-Suite or not.
 * 
 * if (newTestSuite)
 * 		create file in /tests/[testSuiteName]_driver.py
 * 		create file in /scripts/[testSuiteName]_tests.py
 * 		setup files using templates from classpath
 * else
 * 		File = /tests/[testSuiteName]_driver.py
 * 		File = /scripts/[testSuiteName]_tests.py
 * 
 * 
 * Concerns: 
 * 
 * -python language code generation. should only write to a stream. does not know of ATF.
 * 
 * 1) Check if test suite driver and test suite files exist, create if not the case.
 * 
 * 2) open and parse files, find location to insert/append function/function call.
 * 
 */

public class ATFCodeGenerator {

	/**
	 * 
	 * 
	 * @param recordableEvents
	 * @param testSuiteName
	 * @param testName
	 * @param atfLoc
	 * @param isNewTestSuite
	 * @return returns the directory location to where the test was created.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws LineNotFoundException
	 */
	public String addTestToATF(List<RecordableEvent> recordableEvents,
			String packageName, String testSuiteName, String testName, String atfLoc, boolean isNewTestSuite) throws FileNotFoundException, IOException, LineNotFoundException {

		String sep = System.getProperty("file.separator");
		String testSuiteDirectory = atfLoc +sep+ "FunctionalTest"+sep+"RAS"+sep+"VistA-FOIA"+sep+"Packages"+sep+ packageName +sep;
		File driverFile = new File(testSuiteDirectory + testSuiteName+"_test.py"); 
		File testsFile = new File(testSuiteDirectory + testSuiteName+"_suite.py");
		
		if (isNewTestSuite) {
			driverFile.createNewFile();
			testsFile.createNewFile();
			
			//TODO: load template into files or use file editor to manually create them
		}
		
		//1) append test to _tests.py file
		PythonScriptEditor testFileEditor = new PythonScriptEditor(testsFile);
		List<String> statements = generateFunctionStatements(recordableEvents);
		testFileEditor.appendFunction("def " +testName+ "(test_suite_details):", statements);
		
		//2) insert method call to test in _driver.py file
		PythonScriptEditor driverFileEditor = new PythonScriptEditor(driverFile);
		//TODO: don't rely on comments regex, add it to the last test function call
		driverFileEditor.insertLine(testSuiteName+ "_suite." +testName+ "(test_suite_details)", "^\\s*#End Tests$");
		
		return testSuiteDirectory;
	}
	
	public String getRecordedAsString(List<RecordableEvent> recordableEvents) throws IOException {
		
		PythonGenerator pg = new PythonGenerator();
		StringWriter sw = new StringWriter();
		List<String> statements = generateFunctionStatements(recordableEvents);
		
		pg.appendFunction("RASR_GENERATED_TEST", statements, sw);
		
		return sw.toString();		
	}

	private List<String> generateFunctionStatements(List<RecordableEvent> recordableEvents) {
		List<String> statements = new ArrayList<String>();
		statements.add("testname = sys._getframe().f_code.co_name");
		statements.add("test_driver = TestHelper.TestDriver(testname)");
		statements.add("");
		statements.add("test_driver.pre_test_run(test_suite_details)");
		statements.add("");
		statements.add("try:");
		statements.add("    vista = test_driver.connect_VistA(test_suite_details)");
		for (RecordableEvent recordAbleEvent : recordableEvents) {
			if (recordAbleEvent.getType() == RecordableEventType.EXPECT) {

				statements.add("    vista.wait('" +recordAbleEvent.getRecordedValue()+"')");
			} else if (recordAbleEvent.getType() == RecordableEventType.SEND) {
				statements.add("    vista.write('" +recordAbleEvent.getRecordedValue()+"')"); 
			}
		}
		statements.add("");
		statements.add("    test_driver.post_test_run(test_suite_details)");
		statements.add("except TestHelper.TestError, e:");
		statements.add("    test_driver.exception_handling(e)");
		statements.add("else:");
		statements.add("    test_driver.try_else_handling(test_suite_details)");
		statements.add("finally:");
		statements.add("    test_driver.finally_handling(test_suite_details)");
		statements.add("test_driver.end_method_handling(test_suite_details)");
		
		return statements;
	}

}
