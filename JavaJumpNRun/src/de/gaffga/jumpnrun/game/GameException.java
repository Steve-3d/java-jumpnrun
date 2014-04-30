package de.gaffga.jumpnrun.game;

public class GameException extends Exception {

	/** serialID */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 */
	public GameException(String msg) {
		super(msg);
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 * @param throwable die CausedBy-Exception
	 */
	public GameException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
