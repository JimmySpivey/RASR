package org.osehra.templating;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class TemplateEngine {

	private Reader templateFile;
	private Map<String, String> replacerMap = new HashMap<String, String>();
	
	
	/**
	 * Create a new template based on the URL (typically the URL will be a classpath location to the template).
	 * 
	 * @param url
	 * @throws FileNotFoundException 
	 */
	public TemplateEngine(String classPathLoc) {
		templateFile = new BufferedReader(new InputStreamReader(
			    getClass().getClassLoader().getResourceAsStream(
			    		classPathLoc)));
	}
	
	public void setValue(String key, String value) {
		replacerMap.put(key, value);
	}
	
	/**
	 * Must pass in a writer to capture the template's replacements.
	 * 
	 * @param out
	 * @throws IOException 
	 */
	public void compileTemplate(Writer out) throws IOException {
		BufferedReader br = new BufferedReader(templateFile);
		
//		boolean maybeVariable = false;
//		char last3Char = 0xf;
//		char last2Char = 0xf;
//		char last1Char = 0xf;
//		boolean insideVar = false;
//		StringBuilder varName = new StringBuilder();
//		for (int read = br.read(); read != -1; read = br.read() ) {
//			
//			if (insideVar) {
//				if (last1Char == '}') {
//					
//					
//					insideVar = false;
//					varName = new StringBuilder();
//				} else
//					varName.append(read);
//			}
//			
//			if (last1Char == '{' && last2Char == '$' && last3Char != '/')
//				insideVar = true;
//			
//			last3Char = last2Char;
//			last2Char = last1Char;
//			last1Char = (char)read;
//		}

		boolean dollarFound = false;
		boolean openBracFound = false;
		StringBuilder varName = new StringBuilder("");
		for (int read = 0; read != -1;) {
			read = br.read();
			
			if (read == '$') {
				dollarFound = true;
				continue;
			} else if (read == '{') {
				if (dollarFound) {
					openBracFound = true;
					continue;
				}
			} else if (read == '}') {
				if (dollarFound && openBracFound) {
					//capture the variable
					String var = replacerMap.get(varName.toString());
					if (var != null)
						out.write(var);
					dollarFound = false;
					openBracFound = false;
					continue;
				}
			}
			
			if (dollarFound && !openBracFound) { //failed to find a bracket after the dollar sign, reset
				dollarFound = false;
			} else if (dollarFound && openBracFound) { //inside variable, record the name
				varName.append((char)read);
			} else { //not inside a variable, capture whatever data is found
				out.write(read);
			}
		}
		
		
		//open file with a stream
		//use a buffered reader to read the stream
		//if a non escaped ${text} is found, towards the end and isn't ended with a }
		//then re-read from the buffer and append instead of clearing the buffer, then perform replacements
	}
	
	
	
}
