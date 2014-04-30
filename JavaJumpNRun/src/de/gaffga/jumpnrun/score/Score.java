package de.gaffga.jumpnrun.score;

import java.util.ArrayList;
import java.util.List;

/**
 * Der aktuelle Punktestand des Spiels.
 * 
 * Über ein Observer-Pattern kann der Punktestand andere Objekte über
 * eine Änderung informieren.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Score implements IScoreObservable{

	/** Der aktuelle Punktestand */
	private int score;
	
	/** Anzahl eingesammelte Münzen */
	private int coinsCollected;
	
	/** Anzahl an restlichen Leben */
	private int numReserveLives;
	
	/** Liste der Listener */
	private List<IScoreObserver> listeners = null;
	
	/** Level-Nummer */
	private int level;
	
	/**
	 * Konstruktor.
	 */
	public Score() {
		score = 0;
		coinsCollected = 0;
		listeners = new ArrayList<IScoreObserver>();
		numReserveLives = 2;
		level = 1;
	}
	
	/**
	 * Einen neuen Listener registrieren.
	 * 
	 * @param listener der neue Listener
	 */
	public void addScoreListener(IScoreObserver listener) {
		listeners.add(listener);
	}
	
	/**
	 * Entfernt einen Listener aus der Beachrichtigungsliste.
	 * 
	 * @param listener der zu entfernende Listener
	 */
	public void removeScoreListener(IScoreObserver listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Benachtichtigt alle Listener über eine Änderung der Punktzahl.
	 */
	public void fireScoreChanged() {
		for ( IScoreObserver listener : listeners ) {
			listener.scoreChanged(this);
		}
	}
	
	/**
	 * Addiert einen Wert zur Punktzahl.
	 * 
	 * @param score die zu addierenden Punkte.
	 * @param coinCollected flag ob es eine Münze war die gezählt werden muss 
	 */
	public void addScore(int score, boolean coinCollected) {
		this.score += score;
		if ( coinCollected ) this.coinsCollected++;
		fireScoreChanged();
	}
	
	/**
	 * Erhöht die Anzahl gesammelter Münzen. 
	 */
	public void coinCollected() {
		coinsCollected++;
		fireScoreChanged();
	}
	
	/**
	 * Liefert den Wer der aktuellen Punktzahl.
	 * 
	 * @return die aktuelle Punktzahl
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Liefert die Anzahl an restlichen Leben.
	 * 
	 * @return die Anzahl an restlichen Leben
	 */
	public int getNumReserveLives() {
		return numReserveLives;
	}
	
	/**
	 * Verringert die Anzahl an Leben um eins.
	 * 
	 * Falls keine Leben mehr verfügbar sind bleibt der Wert bei 0.
	 */
	public void decreaseLives() {
		if (numReserveLives > 0) {
			numReserveLives--;
			fireScoreChanged();
		}
	}

	/**
	 * Erhöht die Anzahl an Leben um eins.
	 */
	public void increaseLives() {
		numReserveLives++;
		fireScoreChanged();
	}

	/**
	 * Liefert die Anzahl bisher gesammelter Coins.
	 * 
	 * @return die Anzahl gesammelter Coins
	 */
	public int getCoinsCollected() {
		return coinsCollected;
	}
	
	/**
	 * Erhöht die Levelnummer um eins.
	 */
	public void increaseLevel() {
		level++;
		fireScoreChanged();
	}
	
	/**
	 * Liefert die aktuelle Levelnummer.
	 * 
	 * @return die aktuelle Levelnummer
	 */
	public int getLevel() {
		return level;
	}
}
