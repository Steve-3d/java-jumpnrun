package de.gaffga.jumpnrun.sprites;

public class SpriteException extends Exception {

	/** serialID */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 */
	public SpriteException(String msg) {
		super(msg);
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 * @param throwable die CausedBy-Exception
	 */
	SpriteException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
