package de.gaffga.jumpnrun.enemies;

import java.awt.Point;

import de.gaffga.jumpnrun.FloatPosition;
import de.gaffga.jumpnrun.FpsMeter;
import de.gaffga.jumpnrun.map.ICollidable;
import de.gaffga.jumpnrun.map.Map;
import de.gaffga.jumpnrun.sprites.Sprite;
import de.gaffga.jumpnrun.sprites.SpriteAnimation;
import de.gaffga.jumpnrun.tiles.Tile;

/**
 * Klasse die einen Feind verwaltet.
 * 
 * Es kann mehrere Typen von Feinden geben - diese Klasse bildet daher die
 * Gemeinsamkeiten aller Feinde ab.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Enemy implements ICollidable {

	/** Bewegung nach links */
	public static final int LEFT = 1;
	/** Bewegung nach rechts */
	public static final int RIGHT = 2;
	
	/** Die aktuelle Position */
	private FloatPosition position = null;
	
	/** Die aktuelle Bewegungsrichtung */
	private int direction;
	
	/** Das Sprite das den Feind darstellt */
	private Sprite sprite = null;
	
	/** Die Geschwindigkeit des Feinds */
	private float speed = 1.0f;
	
	/**
	 * Geschützter Konstruktor - nur die Factories sollen Enemies
	 * erstellen können.
	 * 
	 * @param pos die Position des Feindes.
	 * @param sprite das Sprite des Feindes
	 */
	Enemy(Point pos, Sprite sprite) {
		this.position = new FloatPosition(pos.x,pos.y);
		this.sprite = sprite;
		this.sprite.setPosition(new Point((int)pos.x,(int)pos.y));
		this.direction = LEFT;
		this.sprite.setCurrentAnimation(SpriteAnimation.WALK_LEFT);
	}
	
	/**
	 * Liefert die aktuelle Position.
	 * 
	 * @return die Position
	 */
	public FloatPosition getPosition() {
		return position;
	}
	
	/**
	 * Liefert das Sprite-Objekt.
	 * 
	 * @return das Sprite-Objekt
	 */
	public Sprite getSprite() {
		return sprite;
	}
	
	/**
	 * Setzt die Geschwindigkeit des Gegeners (1=normal, höher=schneller).
	 * 
	 * @param speed die Geschwindigkeit
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	/**
	 * Führt einen Schritt des Feindes aus.
	 * 
	 * @param map die Map auf der der Feind sich bewegt
	 */
	public void step(Map map) {
		FloatPosition pos1 = new FloatPosition(position);
		FloatPosition pos2 = new FloatPosition(position);
		
		if ( direction==LEFT ) {
			pos2.x -= FpsMeter.getInstance().getSpeedFactor() * speed * 100.0;
		} else {
			pos2.x += FpsMeter.getInstance().getSpeedFactor() * speed * 100.0;
		}
		
		int rc = map.checkMove(this, pos1, pos2, sprite.getHitbox());
		
		if ( (rc & Map.COLLISION_WEST) != 0 || (rc & Map.COLLISION_EAST) != 0 ) {
			if ( direction == LEFT ) {
				direction = RIGHT;
				sprite.setCurrentAnimation(SpriteAnimation.WALK_RIGHT);
			} else {
				direction = LEFT;
				sprite.setCurrentAnimation(SpriteAnimation.WALK_LEFT);
			}
		}

		position = pos2;
		sprite.setPosition(new Point((int)pos2.x, (int)pos2.y));
	}
	
	/**
	 * Prüft ob das angegebene Tile eine Kollision auslösen soll.
	 */
	public boolean isBlockedBy(Tile tile) {
		return tile.isEnemyBlocking(); 
	}
}
