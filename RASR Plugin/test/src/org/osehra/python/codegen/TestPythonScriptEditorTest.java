package org.osehra.python.codegen;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestPythonScriptEditorTest {

	@Test
	public void testAppendFunctionSignature() throws IOException, URISyntaxException, LineNotFoundException {
	
		File file = new File(this.getClass().getClassLoader().getResource("samplePythonFile.txt").toURI());
		
		PythonScriptEditor pse = new PythonScriptEditor(file);
		List<String> statements = new ArrayList<String>();
		statements.add("vista.wait('ACCESS CODE:')");
		statements.add("vista.write('myaccesscode')");
		pse.appendLine("#start methods");
		pse.appendFunction("def test_function(blah):", statements);
		pse.appendLine("#end methods");
		
		
		pse.insertLine("MY TEST LINE", "^\\s*#Begin Tests$");
		//TODO: open existing file, then add verify statements
	}

}
