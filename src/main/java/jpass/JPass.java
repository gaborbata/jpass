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
package jpass;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import jpass.ui.JPassFrame;
import jpass.util.Configuration;
import jpass.util.LanguageConverter;

/**
 * Entry point of JPass.
 *
 * @author Gabor_Bata
 *
 */
public class JPass {

	private static final Logger LOG = Logger.getLogger(JPass.class.getName());
	public static Locale loc = setLocale();
	public static ResourceBundle lang = setLanguageBundle(loc);

	public static String getkey(String key) {
		try {
			return new String(lang.getString(key).getBytes("ISO-8859-1"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return key;

	}

	/**
	 * The function returns a Locale object that is set to the language specified in
	 * the configuration file
	 *
	 * @return The locale object.
	 */
	public static Locale setLocale() {
		String lang = LanguageConverter.getConfigLanguage();
		return new Locale(lang);
	}

	/**
	 * This function returns a ResourceBundle object that contains the localized
	 * strings for the given locale
	 *
	 * @param loc The locale of the language you want to use.
	 * @return The ResourceBundle object.
	 */
	public static ResourceBundle setLanguageBundle(Locale loc) {

		return ResourceBundle.getBundle("resources/language/language", loc); // NON-NLS
	}

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
	}

	public static void main(final String[] args) {
		try {
			UIManager.put("Button.arc", 4);
			FlatLaf lookAndFeel;
			if (Configuration.getInstance().is("ui.theme.dark.mode.enabled", false)) {
				lookAndFeel = new FlatDarkLaf();
			} else {
				lookAndFeel = new FlatLightLaf();
			}

			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			LOG.log(Level.CONFIG, "Could not set look and feel for the application", e);
		}

		SwingUtilities.invokeLater(() -> JPassFrame.getInstance((args.length > 0) ? args[0] : null));
	}
}
