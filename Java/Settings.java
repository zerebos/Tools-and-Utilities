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
package com.zackrauen.types;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.zackrauen.utilities.IniUtils;

// TODO: Auto-generated Javadoc
/**
 * This class manages settings for a program.
 * This class uses IniUtils to load in and save settings.
 * The format comes as a map of maps illustrated below:<br><br>
 * {@code Map ((String) Headings => Map ((String) Keys => (String) Values))}<br><br>
 * Since this class manages settings for a program there are also functions
 * for adding updating and removing settings from the object.
 * @see IniUtils
 */
public class Settings {
	
	/** The default settings file. */
	public InputStream defaultSettingsFile = null;
	private Map<String, Map<String,String>> data = new HashMap<>();
	private Boolean matchCase = true;

	/**
	 * Instantiates a new settings using the default file.
	 */
	public Settings() {
		if (defaultSettingsFile==null)
				defaultSettingsFile=this.getClass().getResourceAsStream("/defaults/default.ini");
	}

	/**
	 * Instantiates a new settings based on case.
	 *
	 * @param caseSensitive the case sensitive token
	 */
	public Settings(Boolean caseSensitive) {
		this();
		setup(null,caseSensitive);
	}

	/**
	 * Instantiates a new settings.
	 *
	 * @param path the path to load the data from.
	 */
	public Settings(String path) {
		this();
		setup(path,true);
	}

	/**
	 * Instantiates a new settings with path and case token.
	 *
	 * @param path the path to load from.
	 * @param caseSensitive the case sensitive token.
	 */
	public Settings(String path, Boolean caseSensitive) {
		this();
		setup(path,caseSensitive);
	}

	/**
	 * Adds the setting specified to the category specified.
	 *
	 * @param category the category to add to.
	 * @param item the item to add (key)
	 * @param value the value to associate with key.
	 * @param overwrite if overwriting old values is desired.
	 */
	public void addSetting(String category, String item, Object value, Boolean overwrite) {
		if (!overwrite)
			this.data.get(category).putIfAbsent(item, value.toString());
		else
			this.updateSetting(category,item,value);
	}

	/**
	 * Gets all the settings.
	 *
	 * @return all the settings
	 */
	public Map<String,Map<String,String>> getAllSettings() {
		return data;
	}

	/**
	 * Gets the category data.
	 *
	 * @param category the category to select
	 * @return the category data as a map from keys to values
	 */
	public Map<String,String> getCategoryData(String category) {
		return data.get(category);
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public Map<String, Map<String,String>> getData() {
		return data;
	}

	/**
	 * Gets the setting.
	 *
	 * @param category the category to select.
	 * @param setting the setting or key to select.
	 * @param defaultValue the default value if the key is not found.
	 * @return the setting as a string.
	 */
	public String getSetting(String category, String setting, String defaultValue) {
		Map<String, String> keyval = data.get(category);
		String item = matchCase ? setting : setting.toLowerCase();
		if (keyval == null || keyval.get(item) == null || keyval.get(item).length()==0) {
			return defaultValue;
		}
		return keyval.get(item);
	}

	/**
	 * Gets the setting as boolean.
	 *
	 * @param category the category to select.
	 * @param setting the setting or key to select.
	 * @param defaultValue the default value if the key is not found.
	 * @return the setting as boolean
	 */
	public Boolean getSettingAsBoolean(String category, String setting, Boolean defaultValue) {
		Map<String, String> keyval = data.get(category);
		String item = matchCase ? setting : setting.toLowerCase();
		if (keyval == null || keyval.get(item) == null || keyval.get(item).length()==0) {
			return defaultValue;
		}
		return Boolean.parseBoolean(keyval.get(item));
	}

	/**
	 * Gets the setting as a double.
	 *
	 * @param category the category to select.
	 * @param setting the setting or key to select.
	 * @param defaultValue the default value if the key is not found.
	 * @return the setting as a double.
	 */
	public double getSettingAsDouble(String category, String setting, double defaultValue) {
		Map<String, String> keyval = data.get(category);
		String item = matchCase ? setting : setting.toLowerCase();
		if (keyval == null || keyval.get(item) == null || keyval.get(item).length()==0) {
			return defaultValue;
		}
		return Double.parseDouble(keyval.get(item));
	}

	/**
	 * Gets the setting as float.
	 *
	 * @param category the category to select.
	 * @param setting the setting or key to select.
	 * @param defaultValue the default value if the key is not found.
	 * @return the setting as float
	 */
	public float getSettingAsFloat(String category, String setting, float defaultValue) {
		Map<String, String> keyval = data.get(category);
		String item = matchCase ? setting : setting.toLowerCase();
		if (keyval == null || keyval.get(item) == null || keyval.get(item).length()==0) {
			return defaultValue;
		}
		return Float.parseFloat(keyval.get(item));
	}

	/**
	 * Gets the setting as integer.
	 *
	 * @param category the category to select.
	 * @param setting the setting or key to select.
	 * @param defaultValue the default value if the key is not found.
	 * @return the setting as integer
	 */
	public int getSettingAsInteger(String category, String setting, int defaultValue) {
		Map<String, String> keyval = data.get(category);
		String item = matchCase ? setting : setting.toLowerCase();
		if (keyval == null || keyval.get(item) == null || keyval.get(item).length()==0) {
			return defaultValue;
		}
		return Integer.parseInt(keyval.get(item));
	}

	/**
	 * Load settings.
	 *
	 * @param inStream the input stream to load from.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void loadSettings(InputStream inStream) throws IOException {
		this.data=IniUtils.parseFile(inStream,this.matchCase);
	}

	/**
	 * Load settings.
	 *
	 * @param path the path to load settings from.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void loadSettings(String path) throws IOException {
		this.data=IniUtils.parseFile(new FileInputStream(path),this.matchCase);
	}

	/**
	 * Put value.
	 *
	 * @param category the category to add to.
	 * @param item the item to add (key)
	 * @param value the value to associate with key.
	 */
	public void putValue(String category, String item, Object value) {
		this.addSetting(category,item,value,true);
	}
	
	/**
	 * Save settings.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void saveSettings(String path) throws IOException {
		IniUtils.saveFile(path,this.data);
	}

	/**
	 * Sets the data.
	 *
	 * @param data the data
	 */
	public void setData(Map<String, Map<String,String>> data) {
		this.data = data;
	}

	/**
	 * Update setting.
	 *
	 * @param category the category to update in.
	 * @param item the item to update.
	 * @param value the new value to use.
	 */
	public void updateSetting(String category, String item, Object value) {
			this.data.get(category).put(item, value.toString());
	}

	private void setup(String path, Boolean caseSensitive) {
		this.matchCase=caseSensitive;
		try {
			this.loadSettings(path==null ? defaultSettingsFile : new FileInputStream(path));
		} catch (IOException e) {
			System.err.println("Either the path provided or the fallback could not be read");
		}
	}
}
