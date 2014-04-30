package de.gaffga.jumpnrun.tiles;

import java.awt.Image;

/**
 * Ein Tile ist ein Element einer Map das sowohl eine grafische Darstellung als
 * auch Eigenschaften hat.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Tile {

	/** Das Tile ist sichtbar */
	public static final int VISIBLE = 1;
	/** Durch das Tile kann man nicht durchgehen */
	public static final int PLAYERBLOCKING = 2;
	/** Das Tile tötet den Spieler */
	public static final int DEADLY = 4;
	/** Das Tile markiert den Startpunkt */
	public static final int START = 8;
	/** Das Tile markiert den Endpunkt */
	public static final int FINISH = 16;
	/** Das Tile kann eingesammelt werden */
	public static final int COLLECTABLE = 32;
	/** Das Tile ist ein Checkpoint (Wiedereinstieg nach Tod) */
	public static final int CHECKPOINT = 64;
	/** Das Tile ist teiltransparent und benötigt einen Hintergrund */
	public static final int USEBACKGROUND = 128;
	/** Das Tile markiert den Startpunkt eines Feindes */
	public static final int ENEMYSTART = 256;
	/** Blockt das Tile Feinde? */
	public static final int ENEMYBLOCKING = 512;
	
	/** Der Code des Tiles der in der Map zum Einbauen dieses Tiles vorhanden sein muss */
	private char code;
	
	/** Das Image das dieses Tile besitzt */
	private Image image;
	
	/** Punktzahl die das Sammeln dieses Items bringt (nur sinnvoll wenn COLLECTABLE) */
	private int score;
	
	/** Die Eigenschaften des Tiles */
	private int flags;
	
	/** Id des Feindes dessen Startpunkt dies ist (ggfls.) */
	private int enemy;
	
	/**
	 * Konstruktor
	 */
	public Tile(char code, Image image, String flags, int score, int enemy) {
		this.code = code;
		this.image = image;
		this.score = score;
		this.enemy = enemy;
		
		this.flags = 0;
		if ( flags.contains("VISIBLE")) {
			this.flags |= VISIBLE;
		} 
		
		if ( flags.contains("PLAYERBLOCKING")) {
			this.flags |= PLAYERBLOCKING;
		} 
		
		if ( flags.contains("ENEMYBLOCKING")) {
			this.flags |= ENEMYBLOCKING;
		}
		
		if ( flags.contains("DEADLY")) {
			this.flags |= DEADLY;
		} 
		
		if ( flags.contains("START")) {
			this.flags |= START;
		} 
		
		if ( flags.contains("FINISH")) {
			this.flags |= FINISH;
		} 
		
		if ( flags.contains("COLLECTABLE")) {
			this.flags |= COLLECTABLE;
		} 
		
		if ( flags.contains("CHECKPOINT")) {
			this.flags |= CHECKPOINT;
		} 
		
		if ( flags.contains("USEBACKGROUND")) {
			this.flags |= USEBACKGROUND;
		}
		
		if ( flags.contains("ENEMYSTART")) {
			this.flags |= ENEMYSTART;
		}
	}

	public int getEnemy() {
		return enemy;
	}
	
	public int getScore() {
		return score;
	}
	
	public char getCode() {
		return code;
	}
	
	public Image getImage() {
		return image;
	}
	
	public boolean isVisible() {
		return (flags & VISIBLE) != 0 ;
	}
	
	public boolean isPlayerBlocking() {
		return (flags & PLAYERBLOCKING) != 0;
	}
	
	public boolean isEnemyBlocking() {
		return (flags & ENEMYBLOCKING) != 0;
	}

	public boolean isDeadly() {
		return (flags & DEADLY) != 0;
	}
	
	public boolean isCollectable() {
		return (flags & COLLECTABLE) != 0;
	}
	
	public boolean isCheckpoint() {
		return (flags & CHECKPOINT) != 0;
	}
	
	public boolean isUsebackground() {
		return (flags & USEBACKGROUND) != 0;
	}
	
	public boolean isStart() {
		return (flags & START) != 0;
	}
	
	public boolean isFinish() {
		return (flags & FINISH) != 0;
	}
	
	public boolean isEnemy() {
		return (flags & ENEMYSTART) != 0;
	}
}
