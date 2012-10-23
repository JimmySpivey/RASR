package org.osehra.python.codegen;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class PythonGenerator {
	
	private final int INDENT_AMOUNT = 4;
	String currentIndent = "";

	public void appendFunction(String functSignature, List<String> statements,
			Writer writer) throws IOException {
		resetIndent();
		appendLine(writer, "\n");
		appendLine(writer, functSignature);
		increaseIndent();

		for (String statement : statements)
			appendLine(writer, statement);
		resetIndent();

		writer.flush();
		writer.close();
	}
	
	/**
	 * Appends a single line to at the very end of the writer
	 * 
	 * @param comment
	 * @throws InvalidEditStateException
	 * @throws IOException
	 */
	private void appendLine(Writer writer, String line) throws IOException {
		writer.append(rightTrim(currentIndent + line) + "\n");
	}

	private void increaseIndent() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < INDENT_AMOUNT; i++)
			sb.append(' ');
		currentIndent += sb.toString();
	}

	private void resetIndent() {
		currentIndent = "";
	}
	
	String rightTrim(String str) {
		
		if (str.length() == 0)
			return str;
		
		int end = str.length() - 1;
		if (str.charAt(end) != ' ' && str.charAt(end) != '\t')
			return str;

		int i = end;
		for (i = end; i >= 0; i--) {
			if (str.charAt(end) != ' ' && str.charAt(end) != '\t')
				break;
		}

		return str.substring(0, i+1);
	}
		
}
