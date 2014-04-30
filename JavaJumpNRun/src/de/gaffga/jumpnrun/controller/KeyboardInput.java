package de.gaffga.jumpnrun.controller;

import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Handler der Tastatureinhaben entgegennimmt und speichert.
 * 
 * Das Game-Objekt fragt uns dann später wie der Tastenzustand ist wenn
 * es diesen braucht.
 * 
 * Diese Klasse speichert alle Tastaturevents in DemoKeyboardEvent-Objekten wenn
 * recording=true ist.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class KeyboardInput implements IGameController {

	/** Map mit den Zuständen von Tasten */
	private HashMap<Integer, Boolean> pressedKeys = null;
	
	/** Map mit den Zuständen von einmalig gedrückten Tasten */
	private HashMap<Integer, Boolean> hitKeys = null;

	/** Flag ob wir gerade die Tastaturevents aufzeichnen (Demo-Recording) */
	private boolean recording = false;

	/** Start-Zeitpunkt der Aufzeichnung */
	private long startMillis;
	
	/** Liste mit den gespeicherten DemoKeyboardEvents */
	private LinkedList<DemoKeyboardEvent> recordedEvents = null;
	
	/**
	 * Konstruktor.
	 *
	 */
	public KeyboardInput() {
		pressedKeys = new HashMap<Integer, Boolean>();
		hitKeys = new HashMap<Integer, Boolean>();
		recordedEvents = new LinkedList<DemoKeyboardEvent>();
	}
	
	/**
	 * Methode um abzufragen ob gerade eine bestimmte Taste gedrückt ist.
	 * 
	 * @param key die Taste die abgefragt werden soll
	 */
	public boolean isKeyPressed(int key) {
		Boolean pressed = pressedKeys.get(key);
		if (pressed==null) return false;
		else return pressed;
	}
	
	/**
	 * Methode um abzufragen ob eine Taste gedrückt wurde (sie kann inzwischen wieder
	 * losgelassen worden sein).
	 * 
	 * @param key die zu testende Taste
	 */
	public boolean wasKeyHit(int key) {
		Boolean hit = hitKeys.get(key);
		if ( hit==null ) return false;
		else {
			// Nach dieser Abfrage wieder zurücksetzen
			hitKeys.put(key,false);
			return hit;
		}
	}
	
	/**
	 * Setzt den Recording-Status.
	 * 
	 * @param recording true damit das Recording stattfindet - sonst false.
	 */
	public void setRecording(boolean recording) {
		this.recording = recording;
		if ( recording ) {
			startMillis = System.currentTimeMillis();
		}
	}
	
	/**
	 * Liefert den Zeitcode der für die zeitliche Markierung der Tastatus-Events beim
	 * Demo-Recording benötigt wird.
	 * 
	 * @return Millisekunden seit Beginn der Aufzeichnung
	 */
	protected long getTimeCode() {
		return System.currentTimeMillis() - startMillis;
	}
	
	/**
	 * Event: Eine Taste wurde gedrückt.
	 * 
	 * @param e der KeyEvent
	 */
	public void keyPressed(KeyEvent e) {
		if ( recording ) {
			recordedEvents.add(new DemoKeyboardEvent(getTimeCode(), "P", e.getKeyCode()));
		}
		
		pressedKeys.put(e.getKeyCode(), true);
		hitKeys.put(e.getKeyCode(), true);
	}

	/**
	 * Event: Eine Taste wurde losgelassen.
	 * 
	 * @param e der KeyEvent
	 */
	public void keyReleased(KeyEvent e) {
		if ( recording ) {
			recordedEvents.add(new DemoKeyboardEvent(getTimeCode(), "R", e.getKeyCode()));
		}
		
		pressedKeys.put(e.getKeyCode(), false);
	}

	/**
	 * Event: Ein Zeichen wurde eingegeben.
	 * 
	 * @param e der KeyEvent
	 */
	public void keyTyped(KeyEvent e) {
		/* interessiert uns nicht */
	}
	
	/**
	 * Liefert die aufgezeichneten Tastaturevents.
	 * 
	 * @return die Liste mit den Tastaturevents
	 */
	public LinkedList<DemoKeyboardEvent> getRecordedEvents() {
		return recordedEvents;
	}

	/**
	 * Liefert den Aufzeichnungsstatus.
	 * 
	 * @return true falls Keyboardevents aufgezeichnet werden, sonst false.
	 */
	public boolean isRecording() {
		return recording;
	}

	/**
	 * Speichert die aufgezeichneten Events in die angegebene Datei.
	 * 
	 * @param fileName der Dateiname
	 * @throws IOException 
	 */
	public void saveRecordedEvents(String fileName) throws IOException {
		LinkedList<DemoKeyboardEvent> events = getRecordedEvents();
		
		File file = new File(fileName);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for ( DemoKeyboardEvent ev : events ) {
			bw.write(ev.toString());
			bw.write("\n");
		}
		
		bw.close();
	}
}
