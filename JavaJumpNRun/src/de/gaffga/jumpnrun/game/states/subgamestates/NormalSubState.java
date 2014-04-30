package de.gaffga.jumpnrun.game.states.subgamestates;

import java.awt.Image;
import java.awt.Point;
import java.util.List;

import de.gaffga.jumpnrun.Player;
import de.gaffga.jumpnrun.controller.IGameController;
import de.gaffga.jumpnrun.effects.IEffect;
import de.gaffga.jumpnrun.effects.ImageMoveEffect;
import de.gaffga.jumpnrun.effects.imagemovestrategies.FallImageMove;
import de.gaffga.jumpnrun.effects.imagemovestrategies.StaticImageMove;
import de.gaffga.jumpnrun.enemies.Enemy;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.states.NormalGamePlayState;
import de.gaffga.jumpnrun.map.Map;
import de.gaffga.jumpnrun.resources.ResourceManager;
import de.gaffga.jumpnrun.sprites.Sprite;
import de.gaffga.jumpnrun.tiles.Tile;
import de.gaffga.jumpnrun.tiles.TileFactory;
import de.gaffga.jumpnrun.tiles.TouchingTile;

public class NormalSubState implements ISubGameState {

	/** Der Hauptzustand - gleichzeitig auch die Zustandsmaschine */
	private NormalGamePlayState gameState = null;
	
	/** Image für eine einsammelbare Münze */
	private Image coinImage = null;

	/** Das Image für 100 Punkte */
	private Image score100Image = null;

	/**
	 * Konstruktor.
	 * 
	 * @param gameState
	 * @throws GameStateException
	 */
	public NormalSubState(NormalGamePlayState gameState) {
		this.gameState = gameState;

		coinImage = ResourceManager.getInstance().getImage("coin.png");
		score100Image = ResourceManager.getInstance().getImage("100.png");
	}
	
	public void enterState() throws GameStateException {
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
		Player player = gameState.getPlayer();
		Map map = gameState.getMap();
		IGameController keyboardInput = gameState.getGameController();
		
		player.step(map, keyboardInput);
		gameState.getEnemyManager().step(map); 
		gameState.getSpriteManager().step();
		
		checkPlayerCollectItems();
		
		if ( checkPlayerDie() ) {
			// Alle Sonderzustände (wie z.B. "jumping") zurücksetzen
			player.reset();
			
			// Der Spieler hat irgendetwas berührt was ihm nicht gut tut
			gameState.switchState(new PlayerDeadAnimationState(gameState));
		}
		
		if ( checkPlayerFinish() ) {
			// Spieler hat die Finish-Fahne erreicht! Das Level ist fertig!
			player.reset();
			gameState.switchState(new LevelFinishedState(gameState));
		}
		
		gameState.getEffectManager().step();
	}

	/**
	 * Prüft ob der Spieler ein Item eingesammelt hat.
	 */
	protected void checkPlayerCollectItems() {
		Sprite mainChar = gameState.getMainChar();
		Map map = gameState.getMap();
		IEffect effect;

		List<TouchingTile> tiles = map.getTouchingTiles(mainChar.getPosition(), mainChar.getHitbox());
		for ( TouchingTile touchingTile : tiles ) {

			// Wenn ein Tile eingesammelt wird dann wird es durch das Hintergrundtile
			// ausgetauscht und die Punkte werden gezählt
			if ( touchingTile.getTile().isCollectable() ) {
				
				int scoreValue = touchingTile.getTile().getScore();

				if ( touchingTile.getTile().isCheckpoint() ) {
					Point point = map.getPosMapPixelFromPosMapTiles(touchingTile.getPosition());
					gameState.setLastCheckpoint(point);
				} else {
					// Die Punkte zählen und einen Coin zählen
					gameState.getScore().addScore(scoreValue, true);
				}
				
				// Das Tile durch das Hintergrund-Tile austauschen
				Point pos = touchingTile.getPosition();
				map.setTileAt(pos.x, pos.y, TileFactory.getInstance().getBackgroundTile().getCode());
				
				// Einen Effekt starten der visuell das Einsammeln bestätigt
				Point pointStart = map.getPosMapPixelFromPosMapTiles(pos.x,pos.y);
				effect = new ImageMoveEffect(touchingTile.getTile().getImage(), map, new FallImageMove(pointStart, -coinImage.getHeight(null)) );
				gameState.getEffectManager().addEffect(effect);

				if ( scoreValue == 100 ) {
					// Die Punktzahl als Bild kurz anzeigen lassen
					pointStart = map.getPosMapPixelFromPosMapTiles(pos.x,pos.y+1);
					effect = new ImageMoveEffect(score100Image, map, new StaticImageMove(2000, pointStart) );
					gameState.getEffectManager().addEffect(effect);
				}
			}
		}
	}
	
	/**
	 * Prüft ob der Spieler gestorben ist.
	 */
	protected boolean checkPlayerDie() {
		Map map = gameState.getMap();
		Sprite mainChar = gameState.getMainChar();
		// Kollision mit "bösen" Tiles prüfen
		List<TouchingTile> tiles = map.getTouchingTiles(mainChar.getPosition(), mainChar.getHitbox());
		for ( TouchingTile touchingTile : tiles ) {
			Tile tile = touchingTile.getTile();
			
			if ( tile.isDeadly() ) {
				return true;
			}
		}
		
		// Kollision mit den Feinden prüfen
		for ( Enemy enemy : gameState.getEnemyManager() ) {
			if ( enemy.getSprite().checkCollision(mainChar)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Prüft ob der Spieler die Zielfahne erreicht hat.
	 */
	protected boolean checkPlayerFinish() {
		Map map = gameState.getMap();
		Sprite mainChar = gameState.getMainChar();
		// Alle berührten Tiles holen
		List<TouchingTile> tiles = map.getTouchingTiles(mainChar.getPosition(), mainChar.getHitbox());
		
		// Nach der Zielfahne suchen
		for ( TouchingTile touchingTile : tiles ) {
			Tile tile = touchingTile.getTile();
			
			if ( tile.isFinish() ) {
				// gefunden!
				return true;
			}
		}
		
		return false;
	}
	
}
