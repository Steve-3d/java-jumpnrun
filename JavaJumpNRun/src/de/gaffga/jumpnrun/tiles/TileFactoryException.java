package de.gaffga.jumpnrun.tiles;

public class TileFactoryException extends Exception {

	/** serialID */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 */
	public TileFactoryException(String msg) {
		super(msg);
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 * @param throwable die CausedBy-Exception
	 */
	TileFactoryException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
