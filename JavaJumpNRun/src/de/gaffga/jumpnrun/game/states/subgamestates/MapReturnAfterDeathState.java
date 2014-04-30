package de.gaffga.jumpnrun.game.states.subgamestates;

import java.awt.Image;
import java.awt.Point;

import de.gaffga.jumpnrun.DecoratedText;
import de.gaffga.jumpnrun.FloatPosition;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.states.NormalGamePlayState;
import de.gaffga.jumpnrun.sprites.SpriteAnimation;

/** 
 * Bewegt den sichtbaren Mapausschnitt langsam zu einer Zielposition.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class MapReturnAfterDeathState implements ISubGameState {

	/** Der Hauptspielzustand */
	private NormalGamePlayState gameState = null;

	/** Die Startposition */
	private FloatPosition pos1 = null;
	
	/** Die Zielposition */
	private FloatPosition pos2 = null;

	/** Die aktuelle Position */
	private FloatPosition curPos = null;
	
	/** Die Startzeit der Bewegung */
	private long startTime;
	
	/** Der euklidsche Abstand zwischen dem Start- und Zielpunkt */
	private float distance;
	
	/** Der Zeichner für den Ready-Text */
	DecoratedText getReadyText = null;
	
	/** Der Zeichner für den Level-Nummer-Text */
	DecoratedText levelNumberText = null;
	
	/**
	 * Konstruktor.
	 * 
	 * @param gameState der Hauptspielzustand
	 */
	public MapReturnAfterDeathState(NormalGamePlayState gameState, FloatPosition pos1, FloatPosition pos2) {
		this.gameState = gameState;
		this.pos1 = pos1;
		this.pos2 = pos2;
		
		/* Wir werden die Distanz vom Start zum Zielpunkt in einer Geschwindigkeit
		 * von 1 Pixel / Millisekunde zurücklegen. Dies ist recht schnell. Daher teilen wir
		 * den Weg durch das 2-fache der Distanz und verlangsamen so die Bewegung */
		this.distance = (float)Math.sqrt((pos2.x-pos1.x)*(pos2.x-pos1.x)+(pos2.y-pos1.y)*(pos2.y-pos1.y));
		this.distance *= 2.0f;
		
		this.getReadyText = new DecoratedText(gameState.getGame().getComponent(), "GET READY");
		String level = "Level " + String.format("%2d", gameState.getScore().getLevel());
		this.levelNumberText = new DecoratedText(gameState.getGame().getComponent(), level);
	}

	public void enterState() throws GameStateException {
		curPos = new FloatPosition(pos1);
		startTime = System.currentTimeMillis();
		gameState.getMainChar().setCurrentAnimation(SpriteAnimation.IDLE_RIGHT);
	}

	public void leaveState() {
	}

	public void paint(Image image) {
		gameState.getMap().draw(image);
		gameState.getSpriteManager().draw(image);
		gameState.getEffectManager().draw(image);
		gameState.getScoreView().draw(image);

		int w = gameState.getMap().getScreenPixelWidth();
		int h = gameState.getMap().getScreenPixelHeight();
		getReadyText.paint(image, w/2, h/2);
		levelNumberText.paint(image, w/2, h/2 + 40);
	}

	public void step() throws GameStateException {
		gameState.getEffectManager().step();
		
		// Nur das Mainchar-Sprite animieren - den Rest so stehen lassen
		gameState.getMainChar().step();
		
		float t = System.currentTimeMillis() - startTime;
		
		if ( t <= distance ) {
			// Die aktuelle Position linear interpolieren
			curPos.x = (pos2.x-pos1.x) / distance * t + pos1.x;
			curPos.y = (pos2.y-pos1.y) / distance * t + pos1.y;
		} else {
			// Wenn wir schon da sind bleiben wir auch da
			curPos.x = pos2.x;
			curPos.y = pos2.y;
		}
		
		// Die Map zur interpolierten Position bewegen
		gameState.getMap().setMapViewPosition(new Point((int)curPos.x, (int)curPos.y));
		
		if ( t >= distance+1500.0f ) {
			// Etwas über eine Sekunde warten wir noch bevor wir weiterspielen lassen 
			gameState.switchState(new NormalSubState(gameState));
		}
	}
}
