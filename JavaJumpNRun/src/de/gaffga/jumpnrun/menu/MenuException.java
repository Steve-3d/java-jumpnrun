package de.gaffga.jumpnrun.menu;

public class MenuException extends Exception {

	/** serialID */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 */
	public MenuException(String msg) {
		super(msg);
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 * @param throwable die CausedBy-Exception
	 */
	public MenuException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
