package de.gaffga.jumpnrun.game.states.subgamestates;

import java.awt.Image;

import de.gaffga.jumpnrun.DecoratedText;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.states.NormalGamePlayState;
import de.gaffga.jumpnrun.sprites.SpriteAnimation;

public class LevelFinishedState implements ISubGameState {

	/** Der Hauptspielzustand */
	private NormalGamePlayState gameState = null;

	/** Der Zeitpunkt an dem dieser Zustand betreten wurde */
	private long enterTime;

	/** Dekorationstext */
	private DecoratedText levelCompletedText = null;
	
	/**
	 * Konstruktor.
	 * 
	 * @param gameState der Hauptspielzustand
	 */
	public LevelFinishedState(NormalGamePlayState gameState) {
		this.gameState = gameState;
		levelCompletedText = new DecoratedText(gameState.getGame().getComponent(), "Level completed!");
	}

	public void enterState() throws GameStateException {
		gameState.getMainChar().setCurrentAnimation(SpriteAnimation.DANCE);
		enterTime = System.currentTimeMillis();
	}

	public void leaveState() {
	}

	public void paint(Image image) {
		gameState.getMap().draw(image);
		gameState.getSpriteManager().draw(image);
		gameState.getEffectManager().draw(image);
		gameState.getScoreView().draw(image);
		
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		levelCompletedText.paint(image, w/2, h/2);
	}

	public void step() throws GameStateException {
		gameState.getEffectManager().step();

		// Nur das Mainchar-Sprite animieren - den Rest so stehen lassen
		gameState.getMainChar().step();

		// Nach 3 Sekunden stoppen wir die Animation
		if ( System.currentTimeMillis() - enterTime >= 3000 ) {
			gameState.getScore().increaseLevel();
			int level = gameState.getScore().getLevel();
			String levelName = gameState.getGame().getLevelList().get(level-1);
			gameState.initLevel(levelName);
			gameState.switchState(new ReadyState(gameState));
		}
	}

}
