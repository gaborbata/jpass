/*
 * JPass
 *
 * Copyright (c) 2009-2019 Gabor Bata
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
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for loading configurations from {@code jpass.properties}.
 *
 * @author Gabor_Bata
 */
public final class Configuration {

    private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
    private static volatile Configuration INSTANCE;
    private Properties properties = new Properties();

    private Configuration() {
        try {
            File filePath = new File("jpass.properties");
            if (filePath.exists() && filePath.isFile()) {
                InputStream is = new FileInputStream(filePath);
                properties.load(is);
                is.close();
            }
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
                LOG.log(Level.WARNING, String.format("Could not parse value as [%s] for key [%s]", type.getName(), key));
            }
        }
        return value;
    }

    public Boolean is(String key, Boolean defaultValue) {
        return getValue(key, defaultValue, Boolean.class);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return getValue(key, defaultValue, Integer.class);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static Configuration getInstance() {
        if (INSTANCE == null) {
            synchronized (Configuration.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Configuration();
                }
            }
        }
        return INSTANCE;
    }
}
