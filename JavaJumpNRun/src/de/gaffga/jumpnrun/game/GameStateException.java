package de.gaffga.jumpnrun.game;

public class GameStateException extends Exception {

	/** serialID */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 */
	public GameStateException(String msg) {
		super(msg);
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @param throwable die CausedBy-Exception
	 */
	public GameStateException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 * @param throwable die CausedBy-Exception
	 */
	public GameStateException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
