/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2016 Fabian Prasser, Florian Kohlmayer and contributors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deidentifier.arx.r;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

/**
 * OS-specific functions for finding the R executable
 * 
 * @author Fabian Prasser
 * @author Alexander Beischl
 * @author Thuy Tran
 * @author Dana Novanova
 * @author Alisa Fedorenko
 */
public class OS {

	/**
	 * Enum for the OS type
	 * 
	 * @author Fabian Prasser
	 */
	public static enum OSType {
		WINDOWS, UNIX, MAC
	}

	/** R Path in Windows */
	static File rLocationWindows = new File("C:\\Program Files\\R");
	/** Additional R Path in Windows */
	static File rLocationWindowsx86 = new File("C:\\Program Files (x86)\\R");

	/** Locations */
	private static final String[] locationsMac = { "/usr/local/bin/", "/Applications/R.app/Contents/MacOS/R" };
	/** Locations */
	private static final String[] locationsUnix = { "/usr/lib/R/bin", "/usr/bin/", "/usr/share/R/share" };

	/** Locations */

	private static final String[] locationsWindows = windowsLocation(rLocationWindows);
	private static final String[] locationsWindowsx86 = windowsLocation(rLocationWindowsx86);

	/** Executables */
	private static final String[] executablesMac = { "R", "R.app" };
	/** Executables */
	private static final String[] executablesUnix = { "R", "exec" };
	/** Executables */
	private static final String[] executablesWindows = { "R.exe" };

	/**
	 * returns full path to the R
	 * 
	 * @author Dana Novanova
	 */
	static public String[] windowsLocation(File dir) {
		File[] files = dir.listFiles();
		if (files == null)
			return null;
		ArrayList<String> tempArray = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && files[i] != null) {
				File[] arrayElem = files[i].listFiles();
				for (int k = 0; k < arrayElem.length; k++) {
					if (arrayElem[k] != null && arrayElem[k].toString().indexOf("bin") >= 0)
						tempArray.add(arrayElem[k].toString());
				}
			}

		}
		String[] stringArray = new String[tempArray.size()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = tempArray.get(i);
		}
		return stringArray;
	}

	/**
	 * Returns the OS
	 * 
	 * @return
	 */
	public static OSType getOS() {

		String os = System.getProperty("os.name").toLowerCase();

		if (os.indexOf("win") >= 0) {
			return OSType.WINDOWS;
		} else if (os.indexOf("mac") >= 0) {
			return OSType.MAC;
		} else if ((os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)) {
			return OSType.UNIX;
		} else {
			throw new IllegalStateException("Unsupported operating system");
		}
	}

	/**
	 * Returns a path to the R executable or null if R cannot be found
	 * 
	 * @return
	 */
	public static String getR() {
		switch (getOS()) {
		case MAC:
			return getPath(locationsMac, executablesMac);
		case UNIX:
			return getPath(locationsUnix, executablesUnix);
		// changed code for searching in both folders: Program Files and Program
		// Files(86)
		case WINDOWS:
			if (locationsWindowsx86 == null)
				return getPath(locationsWindows, executablesWindows);
			else {
				String[] finalLocationWindows = (String[]) ArrayUtils.addAll(locationsWindows, locationsWindowsx86);
				return getPath(finalLocationWindows, executablesWindows);
			}
		default:
			throw new IllegalStateException("Unknown operating system");
		}
	}

	/**
	 * Returns a path to the R executable or null if R cannot be found
	 * 
	 * @param folder The folder to look in
	 * @return
	 */
	public static String getR(String folder) {
		switch (getOS()) {
		case MAC:
			return getPath(new String[] { folder }, executablesMac);
		case UNIX:
			return getPath(new String[] { folder }, executablesUnix);
		case WINDOWS:
			return getPath(new String[] { folder }, executablesWindows);
		default:
			throw new IllegalStateException("Unknown operating system");
		}
	}

	/**
	 * Returns the path of the R executable or null if R cannot be found
	 * 
	 * @return
	 */
	private static String getPath(String[] locations, String[] executables) {

		// For each location
		for (String location : locations) {
			if (!location.endsWith(File.separator)) {
				location += File.separator;
			}

			// For each name of the executable
			for (String executable : executables) {
				try {

					// Check whether the file exists
					File file = new File(location + executable);
					if (file.exists()) {

						// Check if we have the permissions to run the file
						ProcessBuilder builder = new ProcessBuilder(file.getCanonicalPath(), "--vanilla");
						builder.start().destroy();

						// Return
						return file.getCanonicalPath();
					}
				} catch (Exception e) {
					// Ignore: try the next location
				}
			}
		}

		// We haven't found anything
		return null;
	}

	/**
	 * Returns the parameters for the R process
	 * 
	 * @param path
	 * @return
	 */
	public static String[] getParameters(String path) {
		switch (getOS()) {
		case MAC:
			return new String[] { path, "--vanilla", "--quiet", "--interactive" };
		case UNIX:
			return new String[] { path, "--vanilla", "--quiet", "--interactive" };
		case WINDOWS:
			return new String[] { path, "--vanilla", "--quiet", "--ess" };
		default:
			throw new IllegalStateException("Unknown operating system");
		}

	}

	public static String[] getPossibleExecutables() {
		switch (getOS()) {
		case MAC:
			return executablesMac;
		case UNIX:
			return executablesUnix;
		case WINDOWS:
			return executablesWindows;
		default:
			throw new IllegalStateException("Unknown operating system");
		}
	}
}
