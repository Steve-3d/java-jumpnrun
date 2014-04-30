package de.gaffga.jumpnrun.game.states.subgamestates;

import java.awt.Image;
import java.io.IOException;

import de.gaffga.jumpnrun.DecoratedText;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.states.MainMenuState;
import de.gaffga.jumpnrun.game.states.NormalGamePlayState;
import de.gaffga.jumpnrun.resources.ResourceManagerException;

/**
 * Zeigt "GAME OVER" an und beendet damit das Spiel. 
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class GameOverState implements ISubGameState {

	/** Das Hauptspielstatusobjekt */
	private NormalGamePlayState gameState = null;
	
	/** Der Startzeitpunkt dieses Zustands */
	private long startTime;
	
	/** Der Game-Over Textzeichner */
	private DecoratedText gameOverText = null;
	
	/**
	 * Konstruktor.
	 */
	public GameOverState(NormalGamePlayState gameState) {
		this.gameState = gameState;
		this.gameOverText = new DecoratedText(gameState.getGame().getComponent(), "GAME OVER");
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
		gameOverText.paint(image, w/2, h/2);
	}

	public void step() throws GameStateException {
		gameState.getEffectManager().step();
		
		if ( System.currentTimeMillis() - startTime > 4000 ) {
			gameState.switchState(new NormalSubState(gameState));
			try {
				gameState.getGame().switchGameState(new MainMenuState(gameState.getGame(),gameState.getGame().getComponent()));
			} catch (IOException e) {
				// TODO Meldung oder Loggen
				e.printStackTrace();
			} catch (ResourceManagerException e) {
				// TODO Meldung oder Loggen
				e.printStackTrace();
			}
		}
	}

}
