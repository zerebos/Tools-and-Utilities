/*
 * Copyright (c) 2015 Zachary Rauen
 * Website: www.ZackRauen.com
 *
 * All rights reserved. Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * If a copy of the License is not provided with the work, you may
 * obtain a copy at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zackrauen.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * This class parses and saves *.ini files. It uses regular expressions
 * to parse the needed data. The format comes as a map of maps illustrated
 * below:<br><br>
 * {@code Map ((String) Headings => Map ((String) Keys => (String) Values))}
 * <br><br>This class is also capable of writing *.ini files when given the settings
 * in the previously illustrated format.
 * @author Zack Rauen
 * @version 1.3
 */
public final class IniUtils {

	/**
	 * The pattern for the ini headings. Looking for [.*]
	 */
	public static Pattern headingPattern = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
	
	/**
	 * The pattern for the ini values. Looking for .*=.*
	 */
	public static Pattern keyPattern = Pattern.compile("\\s*([^=]*)=(.*)");
	
	
	/**
	 * Defaults to be case sensitive.
	 * @param path The path of the file. This is directly used to attempt a parsing.
	 * @return Map of headings to maps of keys to values.
	 * @throws IOException If no file is found.
	 */
	public static Map<String, Map<String,String>> parseFile(String path) throws IOException {
		return IniUtils.parseFile(new FileInputStream(path), false);
	}

	/**
	 * Parses the file given into a map of headings (strings) to maps of keys (strings) to values (strings).
	 *
	 * @param path the path to load from.
	 * @param caseSensitive changes if it is case sensitive.
	 * @return Map of headings to maps of keys to values.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Map<String, Map<String,String>> parseFile(InputStream path, Boolean caseSensitive) throws IOException {
		Map<String, Map<String, String>> settings = new HashMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(path));
		String line;
		String section = null;
		while ((line = reader.readLine()) != null) {
			Matcher m = headingPattern.matcher(line);
			if (m.matches()) {
				section = m.group(1).trim();
			} else if (section != null) {
				m = keyPattern.matcher(line);
				if (m.matches()) {
					String key = m.group(1).trim();
					String value = m.group(2).trim();
					Map<String, String> keyval = settings.get(caseSensitive ? section.toLowerCase() : section);
					if (keyval == null) {
						settings.put(caseSensitive ? section.toLowerCase() : section, keyval = new HashMap<>());
					}
					keyval.put(caseSensitive ? key.toLowerCase() : key, value);
				}
			}
		}
		reader.close();
		return settings;
	}

	/**
	 * Save file of settings when given the correct format of settings.
	 *
	 * @param path the path to save to including filename.
	 * @param setup the setup of the settings.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void saveFile(String path, Map<String,Map<String,String>> setup) throws IOException {
		PrintWriter p = new PrintWriter(new FileWriter(path),true);
		for (String heading : setup.keySet()) {
			p.println("["+heading+"]");
			for (String key : setup.get(heading).keySet()) {
				p.println(key+"="+setup.get(heading).get(key));
			}
			p.println("");
		}
		p.close();
	}
}
