package org.osehra.eclipse.atfrecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SaveTestMenuPopulator {
	
	private RASRPreferences preferences = RASRPreferences.getInstance();

	public List<String> getPackageNames() throws FileNotFoundException {
		List<String> result = new ArrayList<String>();
		String atfLoc = preferences.getValue(RASRPreferences.ATF_LOCATION);
		String sep = System.getProperty("file.separator");
		File packagesDir = new File(atfLoc+sep+"Packages");
		
		if (!packagesDir.exists()) {
			//they are not currently linked to a valid ATF location
			throw new FileNotFoundException("The ATF Location " + (atfLoc == null ? "null" : atfLoc)+ " is invalid.");
		}
		
		File[] packages = packagesDir.listFiles();		
		for (int i = 0; i < packages.length; i++) {
			if (!packages[i].isDirectory())
				continue;
			
			File isDir = new File(packages[i], "Testing"+sep+"RAS"+sep);
			if (!isDir.exists() || !isDir.isDirectory())
				continue;
			
			result.add(packages[i].getName());
		}
		
		return result;
	}
	
	//idea: get rid of the 2 file system _main and _suite. just put the main() method in the _suite file and just populate any file ending in .py
	private static Pattern testSuitePattern = Pattern.compile(".*_suite\\.py");
	
	/**
	 * returns a list of suiteNames based on a given package Directory
	 * @return
	 * @throws FileNotFoundException 
	 */
	public List<String> getSuiteNames(String packageName) throws FileNotFoundException {
		List<String> result = new ArrayList<String>();
		
		if (packageName == null || packageName.isEmpty())
			return result;
		
		String atfLoc = preferences.getValue(RASRPreferences.ATF_LOCATION);
		String sep = System.getProperty("file.separator");
		File suitesDirectory = new File(atfLoc+sep+"Packages"+sep+packageName+sep+"Testing"+sep+"RAS"+sep);

		if (!suitesDirectory.exists()) {
			throw new FileNotFoundException("The selected package " +packageName+ " does not exist at " +atfLoc);
		}
		
		File[] suites = suitesDirectory.listFiles();		
		for (int i = 0; i < suites.length; i++) {
			
			if (!suites[i].isFile())
				continue;

			Matcher m = testSuitePattern.matcher(suites[i].getName());
			if (!m.matches())
				continue;

			String fileName = suites[i].getName();
			result.add(fileName.substring(0, fileName.lastIndexOf("_")));
		}
		
		return result;
	}

}
