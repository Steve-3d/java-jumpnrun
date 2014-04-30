package de.gaffga.jumpnrun.game.states.subgamestates;

import java.awt.Image;

import de.gaffga.jumpnrun.DecoratedText;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.states.NormalGamePlayState;

/**
 * Zustand der für einige Sekunden "READY?" anzeigt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 *
 */
public class ReadyState implements ISubGameState {

	/** Das Hauptspielstatusobjekt */
	private NormalGamePlayState gameState = null;
	
	/** Der Startzeitpunkt dieses Zustands */
	private long startTime;
	
	/** Der Zeichner für den Ready-Text */
	DecoratedText getReadyText = null;
	
	/** Der Zeichner für die Levelnummer */
	DecoratedText levelNumberText = null;
	
	/**
	 * Konstruktor.
	 */
	public ReadyState(NormalGamePlayState gameState) {
		this.gameState = gameState;
		this.getReadyText = new DecoratedText(gameState.getGame().getComponent(), "GET READY");
		String level = "Level " + String.format("%2d", gameState.getScore().getLevel());
		this.levelNumberText = new DecoratedText(gameState.getGame().getComponent(), level);
	}
	
	public void enterState() throws GameStateException {
		startTime = System.currentTimeMillis();
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
		
		if ( System.currentTimeMillis() - startTime > 2000 ) {
			gameState.switchState(new NormalSubState(gameState));
		}
	}

}
