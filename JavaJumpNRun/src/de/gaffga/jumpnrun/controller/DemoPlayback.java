package de.gaffga.jumpnrun.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Abspielen einer Demo-Aufzeichnung.
 * 
 * TODO Das Timing stimmt noch nicht 100%ig mit der Aufzeichnung überein!
 *  
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class DemoPlayback {

	/** Map mit den Zuständen von Tasten */
	private HashMap<Integer, Boolean> pressedKeys = null;
	
	/** Map mit den Zuständen von einmalig gedrückten Tasten */
	private HashMap<Integer, Boolean> hitKeys = null;
	
	/** Die Liste mit den Events */
	private LinkedList<DemoKeyboardEvent> events = null;
	
	/** Zeitpunkt bei dem das Playback startete */
	private long startTime;
	
	/** Der Zeitpunkt zu dem das letzte mal step() aufgerufen wurde */
	private long lastCall = 0;
	
	/**
	 * Konstruktor.
	 * 
	 * @param stream aus diesem InputStream werden die DemoKeyboardEvents eingelesen 
	 * @throws IOException 
	 */
	public DemoPlayback(InputStream stream) throws IOException {
		pressedKeys = new HashMap<Integer, Boolean>();
		hitKeys = new HashMap<Integer, Boolean>();
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		
		events = new LinkedList<DemoKeyboardEvent>();
		
		String line=null;
		while ( (line = br.readLine())!=null ) {
			String[] parts = line.split("\\|");
			String event = parts[0];
			int code = Integer.parseInt(parts[1]);
			int time = Integer.parseInt(parts[2]);
			
			events.add(new DemoKeyboardEvent(time, event, code));
		}
	}
	
	/**
	 * Startet das Playback.
	 */
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Liefert eine Liste mit Events die innerhalb eines Zeitabschnitts
	 * auftraten.
	 * 
	 * @param t1 Anfangszeitpunkt
	 * @param t2 Endzeitpunkt
	 */
	private LinkedList<DemoKeyboardEvent> getEventsInTime(long t1, long t2) {
		LinkedList<DemoKeyboardEvent> liste = new LinkedList<DemoKeyboardEvent>();
		
		for ( DemoKeyboardEvent event : events ) {
			if ( event.getTime() >= t1 && event.getTime() < t2 ) {
				liste.add(event);
			}
		}
		return liste;
	}
	
	/**
	 * Führt einen Schritt beim Abspielen der Demo aus. 
	 */
	public void step() {
		long zeit = System.currentTimeMillis() - startTime;
		
		// Die Events die seit dem letzten Aufruf aufgetreten sind auswerten
		LinkedList<DemoKeyboardEvent> events = getEventsInTime(lastCall, zeit);
		
		for ( DemoKeyboardEvent event : events ) {
			if ( event.getEvent().equals("P") ) {
				// Taste gedrückt
				pressedKeys.put(event.getCode(), true);
				hitKeys.put(event.getCode(), true);
			} else if ( event.getEvent().equals("R") ) {
				// Taste losgelassen
				pressedKeys.put(event.getCode(), false);
			}
		}
		
		lastCall = zeit;
	}
	
	/**
	 * Fragt ab ob eine bestimmte Taste in der Zwischenzeit gedrückt wurde und
	 * setzt anschließend das Flag zurück.
	 * 
	 * @param code der zu testende Keycode
	 * @return true falls die Taste gedrückt ist, sonst false
	 */
	public boolean wasKeyHit(int code) {
		step();
		if ( hitKeys.get(code)==null ) return false;
		
		if ( hitKeys.get(code) ) {
			hitKeys.put(code, false);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Ermittelt ob gerade eine Taste gedrückt ist.
	 * 
	 * @param code der zu testende Keycode
	 * @return true falls die Taste gedrückt ist, sonst false
	 */
	public boolean isKeyDown(int code) {
		step();
		if ( pressedKeys.get(code)==null ) return false;
		
		return pressedKeys.get(code);
	}
}
