package org.osehra.eclipse.atfrecorder.codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

	private Properties getPropertiesFromClasspath(String propFileName) throws IOException {
	    // loading xmlProfileGen.properties from the classpath
	    Properties props = new Properties();
	    InputStream inputStream = this.getClass().getClassLoader()
	        .getResourceAsStream(propFileName);

	    if (inputStream == null) {
	        throw new FileNotFoundException("property file '" + propFileName
	            + "' not found in the classpath");
	    }

	    props.load(inputStream);

	    return props;
	}
	
	public void addTestToATF(List<RecordableEvent> recordableEvents,
			String testSuiteName, String testName, boolean isNewTestSuite) throws FileNotFoundException, IOException, LineNotFoundException {

		Properties properties = getPropertiesFromClasspath("atfRecorder.properties");
		String dir = properties.getProperty("atf.location");
		
		//TODO: add package directory name as parm
		String packageDir= "SSH Demo";
		
		//TODO: get correct file seperator as per OS
		File driverFile = new File(dir+"FunctionalTest\\RAS\\VistA-FOIA\\Packages\\" +packageDir+ "\\"+testSuiteName+"_test.py"); 
		File testsFile = new File(dir+"FunctionalTest\\RAS\\VistA-FOIA\\Packages\\" +packageDir+ "\\"+testSuiteName+"_suite.py");
		
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
				String recorded = recordAbleEvent.getRecordedValue().trim();
				int lastCR = recorded.lastIndexOf("\r");
				int lastLF = recorded.lastIndexOf("\n");
				if (lastCR != -1 || lastLF != -1)
					recorded = recorded.substring(Math.max(lastCR, lastLF) + 1);
				//recorded = recorded.replaceAll("\r", "");
				//recorded = recorded.replaceAll("\n", "");
				statements.add("    vista.wait('" +recorded+"')");
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
