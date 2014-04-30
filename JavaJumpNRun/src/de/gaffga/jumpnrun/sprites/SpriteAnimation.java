package de.gaffga.jumpnrun.sprites;

import java.util.LinkedList;
import java.util.List;

/**
 * Eine SpriteAnimation ist eine Sammlung von SpriteAnimationFrame-Objekten
 * und verwaltet diese und bietet den Zugriff auf das aktuelle Frame.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class SpriteAnimation {

	/** Animationsname für das Laufen nach rechts */
	public final static String WALK_RIGHT = "walk_right";
	/** Animationsname für das Laufen nach links */
	public final static String WALK_LEFT = "walk_left";
	/** Animationsname für das Stillstehen und nach rechts schauen */
	public final static String IDLE_RIGHT = "idle_right";
	/** Animationsname für das Stillstehen und nach links schauen */
	public final static String IDLE_LEFT = "idle_left";
	/** Animationsname für das Springen nach rechts */
	public final static String JUMP_RAISE_RIGHT = "jump_raise_right";
	/** Animationsname für das Springen nach rechts */
	public final static String JUMP_FALL_RIGHT = "jump_fall_right";
	/** Animationsname für das Springen nach links */
	public final static String JUMP_RAISE_LEFT = "jump_raise_left";
	/** Animationsname für das Springen nach links */
	public final static String JUMP_FALL_LEFT = "jump_fall_left";
	/** Animationsname für das Gewinnen */
	public final static String DANCE = "dance";
	/** Animationsname für Tod */
	public final static String DEAD = "dead";
	
	/** Die Liste der Frames */
	private List<SpriteAnimationFrame> frames = null;

	/** Zeitpunkt seit dem die Animation läuft */
	private long startTime;
	
	/** Gesamtlaufzeit der Animation in Millisekunden */
	private int total;
	
	/** Das aktuelle Frame */
	private SpriteAnimationFrame currentFrame = null;
	
	/**
	 * Konstruktor
	 */
	public SpriteAnimation() {
		frames = new LinkedList<SpriteAnimationFrame>();
	}
	
	/**
	 * Fügt ein neues Frame der Liste hinzu.
	 * 
	 * @param frame das hinzuzufügende Frame 
	 */
	public void addFrame(SpriteAnimationFrame frame) {
		frames.add(frame);
		
		// Falls es kein aktuelles Frame gibt nehmen wir das erste
		// Frame das zu dieser Animation gehört als Default
		if ( currentFrame == null ) {
			currentFrame = frame;
		}

		total = 0;
		for ( SpriteAnimationFrame f : frames ) {
			total += f.getDuration();
		}
	}
	
	/**
	 * Startet die Animation neu.
	 */
	public void reset() {
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Liefert die Gesamtlaufzeit in Millisekunden der Animation.
	 * 
	 * @return die Gesamtlaufzeit in Millisekunden
	 */
	public int getTotalAnimTime() {
		return total;
	}
	
	/**
	 * Liefert das aktuelle Frame
	 * 
	 * @return das aktuelle Animationsframe
	 */
	public SpriteAnimationFrame getCurrentFrame() {
		return currentFrame;
	}
	
	/**
	 * Bestimmt das nächste Frame der Animation.
	 */
	public void step() {
		long jetzt = System.currentTimeMillis();
		long delta = (jetzt-startTime) % total;
		
		// Das passende Frame finden
		int timePos = 0;
		for ( SpriteAnimationFrame f : frames ) {
			if ( timePos <= delta && timePos + f.getDuration() >= delta ) {
				currentFrame = f;
			}
			timePos += f.getDuration();
		}
	}
}
