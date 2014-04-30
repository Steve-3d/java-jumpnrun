package de.gaffga.jumpnrun.controller;

import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * Interface das eine Abstraktion für alle Klassen bietet die
 * die Eingabedaten für das Spiel bereitstellen.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 * 
 */
public interface IGameController extends KeyListener {

	public boolean isRecording();
	
	public void saveRecordedEvents(String fileName) throws IOException;
	
	public boolean isKeyPressed(int key);
	
	public boolean wasKeyHit(int key);
}
