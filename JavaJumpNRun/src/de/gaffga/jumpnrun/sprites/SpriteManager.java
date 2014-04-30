package de.gaffga.jumpnrun.sprites;

import java.awt.Image;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.gaffga.jumpnrun.map.Map;

/**
 * Verwaltung für alle Sprites. Die Reihenfolge des Zeichnens wird hier
 * unter anderem festgelegt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class SpriteManager implements Iterable<Sprite> {

	/** Die Liste mit den Sprites */
	private List<Sprite> sprites = null;
	
	/** Die Map auf der die Sprites gezeichnet werden */
	private Map map = null;
	
	/**
	 * Konstruktor.
	 * 
	 * @param map Map auf der die Sprites gezeichnet werden sollen
	 */
	public SpriteManager(Map map) {
		sprites = new LinkedList<Sprite>();
		this.map = map;
	}

	/**
	 * Fügt ein neues Sprite hinzu.
	 * 
	 * @param sprite das neue Sprite
	 */
	public void add(Sprite sprite) {
		sprites.add(sprite);
	}
	
	/**
	 * Liefert den Iterator mit dem über alle Sprites iteriert werden kann.
	 */
	public Iterator<Sprite> iterator() {
		return sprites.iterator();
	}
	
	/**
	 * Zeichnet alle Sprites.
	 * 
	 * @param img das Bild auf das gezeichnet werden soll
	 */
	public void draw(Image img) {
		for ( Sprite sprite : sprites ) {
			if ( !sprite.isVisible() ) continue;
			Point pos = sprite.getPosition();
			Point screenPos = map.getPosScreenPixelFromPosMapPixel(pos);
			screenPos.y -= sprite.getHeight();
			
			try {
				sprite.draw(screenPos, img);
			} catch (SpriteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Führt einen Animationsschritt für alle Sprites aus.
	 */
	public void step() {
		for ( Sprite sprite : sprites ) {
			sprite.step();
		}
	}

	/**
	 * Alle Animationen aller Sprites stoppen.  
	 */
	public void stopAllAnimations() {
		for ( Sprite sprite : sprites ) {
			sprite.setCurrentAnimation(null);
		}
	}

	/**
	 * Löscht alle Einträge.
	 */
	public void flush() {
		sprites.clear();
	}
}
