package de.gaffga.jumpnrun.game.states.subgamestates;

import java.awt.Image;

import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.states.NormalGamePlayState;
import de.gaffga.jumpnrun.sprites.SpriteAnimation;

/**
 * Spielt die DEAD-Animation ab und wechselt nach einer kurzen Zeit in einen
 * Zustand der den Spieler aus der Map fallen lässt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class PlayerDeadAnimationState implements ISubGameState {

	/** Der Hauptspielzustand */
	private NormalGamePlayState gameState = null;

	/** Der Zeitpunkt an dem dieser Zustand betreten wurde */
	private long enterTime;

	/**
	 * Konstruktor.
	 * 
	 * @param gameState der Hauptspielzustand
	 */
	public PlayerDeadAnimationState(NormalGamePlayState gameState) {
		this.gameState = gameState;
	}

	public void enterState() throws GameStateException {
		gameState.getMainChar().setCurrentAnimation(SpriteAnimation.DEAD);
		enterTime = System.currentTimeMillis();
	}

	public void leaveState() {
	}

	public void paint(Image image) {
		gameState.getMap().draw(image);
		gameState.getSpriteManager().draw(image);
		gameState.getEffectManager().draw(image);
		gameState.getScoreView().draw(image);
	}

	public void step() throws GameStateException {
		gameState.getEffectManager().step();

		// Nur das Mainchar-Sprite animieren - den Rest so stehen lassen
		gameState.getMainChar().step();

		// Nach einer gewissen Zeit stoppen wir die DEAD-Animation und lassen
		// das mainChar-Sprite aus der Map fallen
		if ( System.currentTimeMillis() - enterTime >= 1000 ) {
			gameState.switchState(new PlayerDeadFallState(gameState));
		}
	}
}
