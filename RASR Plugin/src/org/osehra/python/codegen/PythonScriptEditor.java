package org.osehra.python.codegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonScriptEditor {

	private File file;

	private final int INDENT_AMOUNT = 4;
	private String currentIndent = "";

	public PythonScriptEditor(File file) {
		this.file = file;
	}

	/**
	 * Adds a method to the very end of a python script. Does not parse the
	 * current file.
	 * 
	 * Writes characters out as unicode (Java standard).
	 * 
	 * @throws IOException
	 * @throws InvalidEditStateException
	 */
	public void appendFunction(String functSignature, List<String> statements)
			throws IOException {

		FileWriter writer = new FileWriter(file, true);

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

	private void increaseIndent() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < INDENT_AMOUNT; i++)
			sb.append(' ');
		currentIndent += sb.toString();
	}

	private void resetIndent() {
		currentIndent = "";
	}

	/**
	 * Appends a single line to at the very end of the python file
	 * 
	 * @param comment
	 * @throws InvalidEditStateException
	 * @throws IOException
	 */
	private void appendLine(FileWriter writer, String line) throws IOException {
		writer.append(rightTrim(currentIndent + line) + "\n");
	}

	/**
	 * Simple append a line to the current file. Newline character is added
	 * automatically. Right spaces are trimed.
	 * 
	 * @param line
	 * @throws IOException
	 */
	public void appendLine(String line) throws IOException {
		FileWriter writer = new FileWriter(file, true);
		writer.append(rightTrim(currentIndent + line) + "\n");
		writer.flush();
		writer.close();
	}

	private String rightTrim(String str) {
		
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

	public void insertLines(List<String> lines, String insertAtRegex)
			throws IOException, LineNotFoundException {
		Pattern lineSeekPattern = Pattern.compile(insertAtRegex);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();

		boolean matchFound = false; // TODO: should probably move parsing/regex
									// lookup to a new concern/class
		while (true) {
			String readLine = reader.readLine();
			if (readLine != null) {
				// pos += readLine.length();
				// writer.write(readLine + '\n');
				Matcher m = lineSeekPattern.matcher(readLine);
				if (!matchFound && m.matches()) {
					matchFound = true;

					// count number of white spaces
					StringBuilder spaces = new StringBuilder();
					for (int i = 0; i < readLine.length(); i++) {
						char character = readLine.charAt(i);
						if (character == ' ')
							spaces.append(" ");
						else if (character == ' ')
							spaces.append("\t");
						else
							break;
						currentIndent = spaces.toString();
					}

					for (String line : lines) {
						sb.append(currentIndent + line + "\n");
						// writer.append(line + "\n");
					}
				}
				sb.append(readLine + "\n");
			} else
				break;
		}

		reader.close();

		if (!matchFound)
			throw new LineNotFoundException();

		// replace the existing file with the new one
		FileWriter writer = new FileWriter(file);
		writer.append(sb);
		writer.flush();
		writer.close();
	}

	/**
	 * 
	 * This is rather inefficient for just one line, should probably make
	 * another method for writing multiple lines.
	 * 
	 * @param line
	 * @param regex
	 * @throws LineNotFoundException
	 * @throws IOException
	 */
	public void insertLine(String line, String insertAtRegex)
			throws LineNotFoundException, IOException {
		List<String> lines = new ArrayList<String>();
		lines.add(line);
		insertLines(lines, insertAtRegex);
	}

}
