package de.gaffga.jumpnrun;

import java.awt.Point;
import java.awt.event.KeyEvent;

import de.gaffga.jumpnrun.controller.IGameController;
import de.gaffga.jumpnrun.map.ICollidable;
import de.gaffga.jumpnrun.map.Map;
import de.gaffga.jumpnrun.sprites.Sprite;
import de.gaffga.jumpnrun.sprites.SpriteAnimation;
import de.gaffga.jumpnrun.tiles.Tile;

/**
 * Die Spielfigur und ihr aktueller Zustand
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Player implements ICollidable {

	/** Der Spieler hat zuletzt nach links geschaut */
	public static final int LEFT = 1;
	/** Der Spieler hat zuletzt nach rechts geschaut */
	public static final int RIGHT = 2;
	
	/** Die aktuelle Position */
	private FloatPosition pos = null;
	
	/** Die Beschleunigung wenn der Spieler losläuft */
	private float playerRunAcc = 400.0f;
	
	/** Die Schwerkraft */
	private float playerGravityAcc = 900.0f;
	
	/** Die Maximalgeschwindigkeit */
	private float maxPlayerVel = 200.0f;

	/** Die aktuelle vertikale Geschwindigkeit des Spielers */
	private float playerCurrentVelY;
	
	/** Die aktuelle horizontale Geschwindigkeit des Spielers */
	private float playerCurrentVelX;
	
	/** Die Richtung in die der Spieler schaut */
	private int lastDirection = RIGHT;

	/** Flag ob der Spieler gerade springt */
	private boolean jumping = false;
	
	/** Zeitpunkt des Absprungs */
	private long jumpStartTime;
	
	/** Y-Koordinate in MapPixeln beim Absprung */
	private int jumpStartY;
	
	/** Das Sprite des Spielers */
	private Sprite sprite = null;
	
	/** Die Map auf der der Spieler sich bewegt */
	private Map map = null;
	
	/**
	 * Konstruktor.
	 */
	public Player(FloatPosition pos, Map map, Sprite sprite) {
		this.pos=pos;
		playerCurrentVelY = 0.0f;
		playerCurrentVelX = 0.0f;
		this.sprite = sprite;
		this.map = map;
	}
	
	/**
	 * Liefert die aktuelle Position des Spielers.
	 * 
	 * @return die Position
	 */
	public FloatPosition getPosition() {
		return pos;
	}

	/**
	 * Ändert die aktuelle Position des Players.
	 * 
	 * @param newPos die neue Position
	 */
	public void setPosition(FloatPosition newPos) {
		pos = newPos;
	}

	/**
	 * Ändert die aktuelle Position des Players.
	 * 
	 * @param newPos die neue Position
	 */
	public void setPosition(Point newPos) {
		pos.x = newPos.x;
		pos.y = newPos.y;
	}

	public float getMaxPlayerVel() {
		return maxPlayerVel;
	}

	public float getPlayerGravityAcc() {
		return playerGravityAcc;
	}

	public float getPlayerRunAcc() {
		return playerRunAcc;
	}
	
	/**
	 * Fragt die Tastatur ab und setzt entsprechend die Bewegungsrichtung.
	 *
	 * @param playerMove Die aktuelle Bewegungsrichtung die angepasst wird
	 * @param keyboardInput Das Objekt für die Tastatureingaben
	 */
	public void stepKeyboard(FloatPosition playerMove, IGameController keyboardInput) {
		if ( keyboardInput.isKeyPressed(KeyEvent.VK_RIGHT) ) {
			playerCurrentVelX = getPlayerRunAcc();
			if ( playerCurrentVelX > getMaxPlayerVel() ) {
				playerCurrentVelX = getMaxPlayerVel();
			}
			
			if ( playerCurrentVelY==0) {
				// Wenn unsere Y-Geschwindigkeit 0 ist laufen wir gerade auf dem Boden
				sprite.setCurrentAnimation(SpriteAnimation.WALK_RIGHT);
			}
			lastDirection=RIGHT;
		} 
		if ( keyboardInput.isKeyPressed(KeyEvent.VK_LEFT)) {
			playerCurrentVelX = -getPlayerRunAcc();
			if ( playerCurrentVelX < -getMaxPlayerVel() ) {
				playerCurrentVelX = -getMaxPlayerVel();
			}
			
			if ( playerCurrentVelY==0) {
				sprite.setCurrentAnimation(SpriteAnimation.WALK_LEFT);
			}
			lastDirection=LEFT;
		}
		
		if ( keyboardInput.wasKeyHit(KeyEvent.VK_SPACE)) {
			if ( playerCurrentVelY==0 ) {
				// Nur wenn wir auf dem Boden stehen können wir springen
				if ( !jumping ) {
					jumping = true;
					jumpStartTime = Timer.getInstance().currentTimeMillis();
					jumpStartY = (int)getPosition().y;
				}
			}
		}
	}

	/**
	 * Lässt die Gravitation auf den Spieler wirken.
	 */
	public void stepGravity() {
		// Die Schwerkraft erhöht die Fallgeschwindigkeit (v = a * t)
		playerCurrentVelY -= getPlayerGravityAcc() * FpsMeter.getInstance().getSpeedFactor();
	}

	/**
	 * Lässt die aktuelle Bewegungsgeschwindigkeit sich auf die Position auswirken.
	 * 
	 * @param playerMove Die Bewegungsrichtung des Spielers
	 * @param speedFactor der aktuelle SpeedFactor
	 */
	public void stepMovement(FloatPosition playerMove, float speedFactor) {
		// Der Spieler bewegt sich gemäß der aktuellen Geschwindigkeit (s = v * t)
		playerMove.x = playerCurrentVelX * speedFactor;
		playerMove.y = playerCurrentVelY * speedFactor;
	}
	
	/**
	 * Behandlelt das Springen.
	 * 
	 * @param newPos die aktuell geplante neue Spielerposition
	 */
	public void handleJump(FloatPosition newPos) {
		if ( jumping ) {
			double w = (Timer.getInstance().currentTimeMillis() - jumpStartTime) / 800.0;
			w = w * Math.PI;
			newPos.y = (float)(jumpStartY + Math.sin(w) * 66.0);
			
			// Je nach Sprungrichtung (hoch / runter die Spriteanimation setzen)
			if ( w < Math.PI / 2 ) {
				if ( lastDirection==RIGHT) {
					sprite.setCurrentAnimation(SpriteAnimation.JUMP_RAISE_RIGHT);
				} else {
					sprite.setCurrentAnimation(SpriteAnimation.JUMP_RAISE_LEFT);
				}
			} else {
				if ( lastDirection==RIGHT) {
					sprite.setCurrentAnimation(SpriteAnimation.JUMP_FALL_RIGHT);
				} else {
					sprite.setCurrentAnimation(SpriteAnimation.JUMP_FALL_LEFT);
				}
			}
			
			if ( w>=Math.PI) {
				// Der Sprung ist zu Ende wenn der Bogen vollständig ist - danach
				// fallen wir nur :)
				jumping=false;
			}
		}
	}
	
	/**
	 * Dafür sorgen dass wir einen schönen Ausschnitt aus der Map sehen können.
	 * 
	 * @param newPos die Position des Spielers
	 */
	public void handleScreenViewport(FloatPosition newPos) {
	 	map.scrollToMapPixelPos(newPos);
	}
	
	/**
	 * Prüft ob eine Kollision bei einer Bewegung vorliegt und reagiert entsprechend.
	 * 
	 * @param currentPos die Startposition
	 * @param newPos die Zielposition
	 * @param map die Map in der die Bewegung stattfindet
	 */
	public void handleCollision(FloatPosition currentPos, FloatPosition newPos, Map map) {
		// Auf Kollision testen
		int collision = map.checkMove(this, currentPos, newPos, sprite.getHitbox());

		// Falls der Spieler auf dem Boden steht hat er keine vertikale Geschwindigkeit
		if ( (collision & Map.COLLISION_SOUTH) != 0 || (collision & Map.COLLISION_NORTH) != 0 ) {
			playerCurrentVelY = 0.0f;
			
			// Wenn wir auf dem Boden oder die Decke aufschlagen ist der Sprung beendet
			jumping = false;
		}
		
		// Falls er gegen etwas gelaufen ist stoppt er
		if ( (collision & Map.COLLISION_WEST) != 0 || (collision & Map.COLLISION_EAST) != 0	) {
			playerCurrentVelX = 0.0f;
		}
	}
	
	/**
	 * Führt einen Bearbeitungsschritt der Hauptspielfigur aus. 
	 * 
	 * Neben der Auswertung der Tastatureingabe wird hier auch die aktive Animation des
	 * Sprites gesetzt und die Position und Geschwindigkeit des Spielers aktualisiert.
	 * 
	 * @param map             die Map (zur Kollisionserkennung)
	 * @param keyboardInput   die aktuelle Tastatureingabe
	 */
	public void step(Map map, IGameController keyboardInput) {
		FloatPosition playerMove = new FloatPosition();

		setCurrentVelX(0.0f);
		
		// Tastatur abfragen
		stepKeyboard(playerMove, keyboardInput);
		
		// Schwerkraft anwenden
		stepGravity();
		
		// Aus der Geschwindigkeitsrichtung eine Bewegung machen
		stepMovement(playerMove, FpsMeter.getInstance().getSpeedFactor());
		
		FloatPosition currentPos = getPosition();
		FloatPosition newPos = new FloatPosition(currentPos);

		// Der Spieler kann sich nur nach rechts oder links bewegen - vertikal
		// kann er nur springen oder fallen. Darum ändern wir hier nur die
		// X-Koordinate. Die Y-Koordinate wird durch die Schwerkraft und/oder
		// das Springen geändert.
		newPos.x += playerMove.x;
		newPos.y += playerMove.y;
		
		// Springen 
		handleJump(newPos);

		// Kollisionen abfragen und behandeln
		handleCollision(currentPos, newPos, map);
				
		// Falls der Spieler gerade fällt und nicht springt
		if ( !jumping && playerCurrentVelY<0 ) {
			if ( lastDirection==RIGHT) {
				sprite.setCurrentAnimation(SpriteAnimation.JUMP_FALL_RIGHT);
			} else {
				sprite.setCurrentAnimation(SpriteAnimation.JUMP_FALL_LEFT);
			}
		}
		
		handleScreenViewport(newPos);
		
		if ( playerCurrentVelX==0 && playerCurrentVelY==0) {
			// Der Spieler steht gerade still
			if ( lastDirection==LEFT ) {
				sprite.setCurrentAnimation(SpriteAnimation.IDLE_LEFT);
			} else {
				sprite.setCurrentAnimation(SpriteAnimation.IDLE_RIGHT);
			}
		} 
				
		// Die neue Position des Spielers setzen
		setPosition(newPos);
		
		// Das Sprite auch auf diese Stelle bewegen
		sprite.setPosition(new Point((int)newPos.x, (int)newPos.y));
	}

	/**
	 * Setzt die aktuelle Geschwindigkeit in X-Richtung.
	 * 
	 * @param f die neue X-Geschwindigkeit
	 */
	public void setCurrentVelX(float f) {
		playerCurrentVelX = f;
	}

	/**
	 * Setzt alle Sonderzustände des Spielers zurück.
	 * 
	 * Dies ist wichtig wenn der Spieler stirbt - dann darf er beim
	 * nächsten Versuch keinen Sonderzustand mehr haben. Er wird 
	 * schließlich zurück zum letzten Checkpoint transportiert.
	 *
	 */
	public void reset() {
		jumping = false;
	}
	
	/**
	 * Prüft ob das angegebene Tile eine Kollision auslösen soll.
	 */
	public boolean isBlockedBy(Tile tile) {
		return tile.isPlayerBlocking(); 
	}
}
