package de.gaffga.jumpnrun.controller;

import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Adapter-Klasse die verschiedene Eingabegeräte an das Interface
 * IGameController anpassen kann.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class KeyboardInputAdapter implements IGameController {

	/** Das DemoPlayback-Objekt für das wir den Adapter darstellen */
	private DemoPlayback demoPlayback = null;
	
	/**
	 * Konstruktor.
	 * 
	 * @param demo das DemoPlayback-Objekt
	 */
	public KeyboardInputAdapter(DemoPlayback demo) {
		this.demoPlayback = demo;
	}
	
	public boolean isKeyPressed(int key) {
		return demoPlayback.isKeyDown(key);
	}

	public boolean isRecording() {
		// Nicht implementiert
		return false;
	}

	public void saveRecordedEvents(String fileName) throws IOException {
		// Nicht implementiert
	}

	public boolean wasKeyHit(int key) {
		return demoPlayback.wasKeyHit(key);
	}

	public void keyPressed(KeyEvent arg0) {
		// ignorieren wir hier
	}

	public void keyReleased(KeyEvent arg0) {
		// ignorieren wir hier
	}

	public void keyTyped(KeyEvent arg0) {
		// ignorieren wir hier
	}

}
