package de.gaffga.jumpnrun;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Klasse die alle Methoden zur Behandlung von Mehrsprachigkeit kapselt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class I18n {
	
	/**
	 * Gibt den übersetzten Text für den angegebenen Key zurück.
	 * 
	 * Falls der Text nicht gefunden werden konnte wird der Key mit einem
	 * vorangestellten "?" zurückgegeben.
	 * 
	 * @param key der Key dessen Text geliefert werden soll
	 * @return der übersetzte Text
	 */
	public static String getString(String key) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("i18n");
			return bundle.getString(key);
		} catch(MissingResourceException mre) {
			return "?"+key;
		}
	}
	
}
