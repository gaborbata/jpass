package jpass.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LanguageConverter {

	// KEY -> LOCALE
	// VALUE -> LANGUAGE

	// COMPLETE LIST using:
	// a) https://www.oracle.com/java/technologies/javase/java8locales.html
	// and b) https://wiki.freepascal.org/Language_Codes
	private HashMap<String, String> langs = new HashMap<>();

	public LanguageConverter() {

		this.langs.put("zh_CN", "简体中文");

		this.langs.put("en_US", "English");

	}

	public String convertToLanguage(String locale) {
		return this.langs.get(locale);
	}

	public HashMap<String, String> getLanguage() {
		return this.langs;
	}

	public String convertToLocale(String language) {
		for (Map.Entry<String, String> entry : this.langs.entrySet()) {
			if (Objects.equals(entry.getValue(), language)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * If the config file exists, read the config file and return the language. If
	 * the config file doesn't exist, return the default language
	 *
	 * @return The last part of the config file, which is the language.
	 */
	public static String getConfigLanguage() {
		return Configuration.getInstance().get("ui.language", Locale.getDefault().toString());

	}

	/**
	 * Detects the operating system and returns an integer
	 * 
	 * @return The number of the OS (1 => Windows, 2 => Mac, 3 => Unix).
	 */
	public static int detectOS() {
		String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (os.contains("win"))
			return 1;// NON-NLS
		else if (os.contains("osx"))
			return 2;// NON-NLS
		else if (os.contains("nix") || os.contains("aix") || os.contains("nux"))
			return 3;// NON-NLS
		return 0;
	}

}
