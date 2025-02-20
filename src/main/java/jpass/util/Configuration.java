/*
 * JPass
 *
 * Copyright (c) 2009-2024 Gabor Bata
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for loading configurations from {@code jpass.properties} or system properties.
 *
 * <p>
 * Each configuration property can be overridden by system properties,
 * with the {@code jpass.} prefix.
 * </p>
 *
 * @author Gabor_Bata
 */
public final class Configuration {

    private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
    private static Configuration instance;
    private final Properties properties = new Properties();

    private Configuration() {
        try {
            File filePath = new File(getConfigurationFolderPath(), "jpass.properties");
            if (filePath.exists() && filePath.isFile()) {
                InputStream is = new FileInputStream(filePath);
                properties.load(is);
                is.close();
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "An error occurred during loading configuration.", e);
        }
    }

    private File getConfigurationFolderPath() {
        File configurationFolderPath = null;
        try {
            configurationFolderPath = new File(Configuration.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()).getParentFile();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Could not determine configuration folder path.", e);
        }
        return configurationFolderPath;
    }

    private <T> T getValue(String key, T defaultValue, Class<T> type) {
        T value = defaultValue;
        String prop = getProperty(key);
        if (prop != null) {
            try {
                value = type.getConstructor(String.class).newInstance(prop);
            } catch (Exception e) {
                LOG.log(Level.WARNING, String.format("Could not parse value as [%s] for key [%s]", type.getName(), key));
            }
        }
        return value;
    }

    private String getProperty(String key) {
        return Optional.ofNullable(System.getProperty(String.format("jpass.%s", key)))
            .orElseGet(() -> properties.getProperty(key));
    }

    public Boolean is(String key, Boolean defaultValue) {
        return getValue(key, defaultValue, Boolean.class);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return getValue(key, defaultValue, Integer.class);
    }

    public String get(String key, String defaultValue) {
        String prop = getProperty(key);
        return prop != null ? prop : defaultValue;
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }

    private void saveProperties() {
        File filePath = new File(getConfigurationFolderPath(), "jpass.properties");
        try (OutputStream os = Files.newOutputStream(filePath.toPath())) {
            properties.store(os, "Configuration properties");
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Could not save configuration to file.", e);
        }
    }

    public String[] getArray(String key, String[] defaultValue) {
        String prop = getProperty(key);
        if (prop != null) {
            return prop.split(",");
        }
        return defaultValue;
    }

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }
}
