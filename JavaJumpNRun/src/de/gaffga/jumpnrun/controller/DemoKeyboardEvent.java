package de.gaffga.jumpnrun.controller;

/**
 * Ein Event der zusammen mit vielen anderen das Abspielen einer Demo
 * ermöglicht.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class DemoKeyboardEvent {

	/** Der Zeitpunkt des Events in Millisekunden seit Spielstart */
	long time;
	
	/** "R"=Taste wurde released, "P"=Taste wurde gedrückt */
	String event;
	
	/** Der Code der Taste */
	int code;
	
	/**
	 * Konstruktor.
	 */
	public DemoKeyboardEvent(long time, String event, int code) {
		this.time=time;
		this.event = event;
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getEvent() {
		return event;
	}

	public long getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return getEvent() + "|" + getCode() + "|" + getTime();
	}
}
