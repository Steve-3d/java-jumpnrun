package de.gaffga.jumpnrun;

/**
 * Die Timer-Klasse versorgt das Spiel mit zeitbasierten Informationen.
 * 
 * Die Timer-Klasse ist ein Singleton damit von jeder Stelle des Spiels auf diesen
 * zentralen Zeitgeber zugegriffen werden kann. 
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Timer {

	/** Die einzige Timer-Referenz */
	private static Timer instance = null;
	
	/** Pause-Flag */
	private boolean pause = false;
	
	/** Millisekunden-Zeitpunkt des Beginns der Pause oder 0 falls keine Pause aktiv */
	private long lastMillisBeforePause = 0;
	
	/** Zeitverschiebung gegenüber dem echten currentTimeMillis durch die Pausen */
	private long delta = 0;
	
	/**
	 * Erzeugt einen neuen Timer.
	 */
	private Timer() {
	}
	
	/**
	 * Liefert die einzige Timer-Instanz.
	 * 
	 * @return die einzige Timer-Instanz
	 */
	public static Timer getInstance() {
		if ( instance==null ) {
			instance=new Timer();
		} 
		
		return instance;
	}
	
	/**
	 * Setzt den Pause-Zustand
	 * 
	 * @param pause true wenn eine Pause aktiviert werden soll, sonst false
	 */
	public void setPause(boolean pause) {
		this.pause = pause;
		if ( pause ) {
			lastMillisBeforePause = System.currentTimeMillis();
		}
	}
	
	/**
	 * Liefert den aktuellen Pause-Zustand.
	 * 
	 * @return true falls gerade Pause ist, sonst false.
	 */
	public boolean isPaused() {
		return pause;
	}
	
	/**
	 * Liefert den aktuellen Timewert in Millisekunden. 
	 * 
	 * Der Wert bleibt Konstant während eine Pause aktiv ist und läuft
	 * nach dem Ender der Pause da weiter wo er aufgehört hat.
	 * 
	 * @return der Timewert in Millisekunden
	 */
	public long currentTimeMillis() {
		long now = System.currentTimeMillis();
		long rc = now;
		
		if ( !isPaused() ) {
			// Kein Pause
			if ( lastMillisBeforePause != 0 ) {
				// Erster Aufruf nach dem Ende der Pause
				delta += (now - lastMillisBeforePause);
				// Wieder auf 0 stellen damit der Beginn der nächsten 
				// Pause wieder erkannt wird
				lastMillisBeforePause = 0;
			}
			
			rc = now - delta;
		} else {
			if ( lastMillisBeforePause==0 ) {
				// Erster Aufruf seit Beginn einer Spielpause - merken wann das war
				lastMillisBeforePause = now;
			}
			
			// Während einer Pause immer die gleiche Zeit liefern
			rc = lastMillisBeforePause;
		}
		
		return rc;
	}
}
