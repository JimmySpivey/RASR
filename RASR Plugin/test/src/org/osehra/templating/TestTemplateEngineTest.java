package org.osehra.templating;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class TestTemplateEngineTest {

	@Test
	public void testCompileTemplate() throws URISyntaxException, IOException {
		TemplateEngine engine = new TemplateEngine("samplePythonFile.txt");
		engine.setValue("testSuite.name", "unit_testing");
		
		
		FileWriter fw = new FileWriter("testFile.py");
		engine.compileTemplate(fw);
		fw.flush();
		fw.close();
		
//		engine.compileTemplate(new PrintWriter(System.err));
//		System.err.flush();
	}

}
