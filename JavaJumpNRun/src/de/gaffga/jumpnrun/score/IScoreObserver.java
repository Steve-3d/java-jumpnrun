package de.gaffga.jumpnrun.score;

/**
 * Interface das alle Objekte implementieren müssen die über Änderungen
 * an der Score-Klasse informiert werden möchten.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public interface IScoreObserver {

	/**
	 * Info, dass sich die Punktzahl geändert hat.
	 * 
	 * @param score das Objekt das sich geändert hat.
	 */
	public void scoreChanged(IScoreObservable score);
	
}
