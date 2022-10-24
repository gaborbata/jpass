/*
 * JPass
 *
 * Copyright (c) 2009-2022 Gabor Bata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jpass.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;

/**
 * Class for loading configurations from {@code jpass.properties}.
 *
 * @author Gabor_Bata
 */
public final class Configuration {

	private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
	private static Configuration INSTANCE;
	private Properties properties = new Properties();
	private String propertiesfile = "jpass.properties";

	private Configuration() {
		try {
			InputStream is = null;
			if (Configuration.class.getResource("").getProtocol().equals("jar")) {
				// 以 jar 的方式运行

				String pathString = new File(".").getCanonicalPath();
				File file = new File(pathString + "/jpass.properties");

				if (!file.exists()) {
					// 复制配置文件
					Files.copy(getFileResource("" + propertiesfile), Paths.get(pathString + "/jpass.properties"));
					is = new FileInputStream(pathString + "/jpass.properties");
				} else {

					is = new BufferedInputStream(new FileInputStream(pathString + "/jpass.properties"));
				}

			} else {
				is = new FileInputStream(new File("src/main/resources/" + propertiesfile));
			}

			properties.load(is);

			is.close();

		} catch (Exception e) {
			LOG.log(Level.WARNING, "An error occurred during loading configuration.", e);
		}
	}

	private <T> T getValue(String key, T defaultValue, Class<T> type) {
		T value = defaultValue;
		String prop = properties.getProperty(key);
		if (prop != null) {
			try {
				value = type.getConstructor(String.class).newInstance(prop);
			} catch (Exception e) {
				LOG.log(Level.WARNING,
						String.format("Could not parse value as [%s] for key [%s]", type.getName(), key));
			}
		}
		return value;
	}

	public Boolean is(String key, Boolean defaultValue) {
		return getValue(key, defaultValue, Boolean.class);
	}

	public Boolean isString(String key, String defaultValue) {
		return get(key, defaultValue).equals(defaultValue);
	}

	public Integer getInteger(String key, Integer defaultValue) {
		return getValue(key, defaultValue, Integer.class);
	}

	public String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public void set(String key, String value) throws FileNotFoundException, IOException {

		if (Configuration.class.getResource("").getProtocol().equals("jar")) {
			String pathString = new File(".").getCanonicalPath();
			File file = new File(pathString + "/jpass.properties");
			// 有文件
			if (file.exists()) {
				File filePath = new File(pathString + "/jpass.properties");
				properties.setProperty(key, value);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
				properties.store(bw, "");
			}

		} else {
			File filePath = new File("src/main/resources/jpass.properties");
			properties.setProperty(key, value);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
			properties.store(bw, "");
		}

	}

	public String[] getArray(String key, String[] defaultValue) {
		String prop = properties.getProperty(key);
		if (prop != null) {
			return prop.split(",");
		}
		return defaultValue;
	}

	public static synchronized Configuration getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Configuration();
		}
		return INSTANCE;
	}

	/**
	 * 获取jar包文件流（resources目录下）
	 *
	 * @param name 文件名
	 * @return msyh InputStream文件流
	 */
	public static InputStream getFileResource(String name) throws IOException, BadLocationException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream msyh = loader.getResourceAsStream(name);
		return msyh;
	}
}
