package de.gaffga.jumpnrun.score;

/**
 * Interface das die Score-Klasse implementiert um von IScoreObservern
 * beobachtet werden zu können.
 *   
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public interface IScoreObservable {
	/**
	 * Einen neuen Listener registrieren.
	 * 
	 * @param listener der neue Listener
	 */
	public void addScoreListener(IScoreObserver listener);
	
	/**
	 * Entfernt einen Listener aus der Beachrichtigungsliste.
	 * 
	 * @param listener der zu entfernende Listener
	 */
	public void removeScoreListener(IScoreObserver listener);
	
	/**
	 * Benachtichtigt alle Listener über eine Änderung der Punktzahl.
	 */
	public void fireScoreChanged();
}
