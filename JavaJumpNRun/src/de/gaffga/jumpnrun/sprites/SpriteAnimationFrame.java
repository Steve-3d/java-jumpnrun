package de.gaffga.jumpnrun.sprites;

/**
 * Dies ist ein Frame einer Sprite-Animation.
 * 
 * Diese Klasse definiert welches Frame für wie lange angezeigt werden soll.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class SpriteAnimationFrame {

	/** Die Y-Position im Spritepool aus der das Bild stammt */
	int y;
	
	/** Die X-Position im Spritepool aus der das Bild stammt */
	int x;
	
	/** Die Dauer in Millisekunden */
	int duration;
	
	/**
	 * Konstruktor.
	 * 
	 * @param x die X-Koordinate
	 * @param y die Y-Koordinate
	 * @param duration die Anzeigedauer in Millisekunden
	 */
	public SpriteAnimationFrame(int x, int y, int duration) {
		this.y = y;
		this.x = x;
		this.duration = duration;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
