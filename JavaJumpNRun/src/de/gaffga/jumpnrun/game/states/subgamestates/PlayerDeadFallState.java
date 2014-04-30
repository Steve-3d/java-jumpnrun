package de.gaffga.jumpnrun.game.states.subgamestates;

import java.awt.Image;

import de.gaffga.jumpnrun.FloatPosition;
import de.gaffga.jumpnrun.effects.SpriteMoveEffect;
import de.gaffga.jumpnrun.effects.imagemovestrategies.FallImageMove;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.states.NormalGamePlayState;
import de.gaffga.jumpnrun.sprites.Sprite;

/**
 * Lässt das Sprite des Spielers aus der Map fallen. Anschließend wird
 * in einen Zustand gesprungen der die Ansicht der Map zum letzten Checkpoint
 * fährt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class PlayerDeadFallState implements ISubGameState {

	/** Der Hauptspielzustand */
	private NormalGamePlayState gameState = null;

	/** Der Effekt den wir abspielen */
	private SpriteMoveEffect moveEffect = null;
	
	/**
	 * Konstruktor.
	 * 
	 * @param gameState der Hauptspielzustand
	 */
	public PlayerDeadFallState(NormalGamePlayState gameState) {
		this.gameState = gameState;
	}

	public void enterState() throws GameStateException {
		Sprite mainChar = gameState.getMainChar();
		
		moveEffect = new SpriteMoveEffect(mainChar,
				new FallImageMove(mainChar.getPosition(), -mainChar.getHeight()));
		gameState.getEffectManager().addEffect(moveEffect);
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
		
		if ( moveEffect.isFinished() ) {
			// Wenn der Spieler aus der Map gefallen ist
			
			if ( gameState.getScore().getNumReserveLives()==0 ) {
				// Falls kein Leben mehr übrig ist: Game Over!
				gameState.switchState(new GameOverState(gameState));
			} else {
				// Ein Leben abziehen
				gameState.getScore().decreaseLives();
				
				// Die aktuelle Position des Spielers und die Zielposition (der letzte
				// Checkpoint) ermitteln und die Bewegung starten.
				FloatPosition currentPos = new FloatPosition(gameState.getPlayer().getPosition());
				currentPos = gameState.getMap().getMapViewPositionFromPosition(currentPos);
				FloatPosition destinationPos = new FloatPosition(gameState.getLastCheckpoint());
				destinationPos = gameState.getMap().getMapViewPositionFromPosition(destinationPos);
				gameState.getPlayer().setPosition(gameState.getLastCheckpoint());
				gameState.getMainChar().setPosition(gameState.getLastCheckpoint());

				// Den Zustand starten der die Map bewegt
				gameState.switchState(new MapReturnAfterDeathState(gameState,currentPos,destinationPos));
			}
		}
	}
}
