package de.gaffga.jumpnrun.resources;

/**
 * Exception die einen Fehler im ResourceManager berichtet.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class ResourceManagerException extends Exception {

	/** serialID */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 */
	public ResourceManagerException(String msg) {
		super(msg);
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @param msg die Fehlernachricht
	 * @param throwable die CausedBy-Exception
	 */
	ResourceManagerException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
