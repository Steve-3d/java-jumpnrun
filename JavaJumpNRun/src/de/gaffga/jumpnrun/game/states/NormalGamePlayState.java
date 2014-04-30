package de.gaffga.jumpnrun.game.states;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;

import de.gaffga.jumpnrun.FloatPosition;
import de.gaffga.jumpnrun.Player;
import de.gaffga.jumpnrun.controller.IGameController;
import de.gaffga.jumpnrun.effects.EffectManager;
import de.gaffga.jumpnrun.enemies.AbstractEnemyFactory;
import de.gaffga.jumpnrun.enemies.EasyEnemyFactory;
import de.gaffga.jumpnrun.enemies.Enemy;
import de.gaffga.jumpnrun.enemies.EnemyManager;
import de.gaffga.jumpnrun.enemies.HardEnemyFactory;
import de.gaffga.jumpnrun.enemies.NormalEnemyFactory;
import de.gaffga.jumpnrun.game.Game;
import de.gaffga.jumpnrun.game.GameException;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.IGameState;
import de.gaffga.jumpnrun.game.states.subgamestates.ISubGameState;
import de.gaffga.jumpnrun.game.states.subgamestates.ReadyState;
import de.gaffga.jumpnrun.map.Map;
import de.gaffga.jumpnrun.map.MapException;
import de.gaffga.jumpnrun.resources.ResourceManagerException;
import de.gaffga.jumpnrun.score.Score;
import de.gaffga.jumpnrun.score.ScoreView;
import de.gaffga.jumpnrun.sprites.Sprite;
import de.gaffga.jumpnrun.sprites.SpriteAnimation;
import de.gaffga.jumpnrun.sprites.SpriteException;
import de.gaffga.jumpnrun.sprites.SpriteManager;
import de.gaffga.jumpnrun.tiles.Tile;

/**
 * Zustand in dem das normale GamePlay stattfindet. Der Spieler wird 
 * anhand der Tastatureingaben bewegt, die Gegner anhand ihrer KI und
 * auf Events wird entsprechend reagiert (Level fertig, Spieler stirbt).
 *
 * Der NormalGamePlayState wird verlassen wenn das Spiel beendet wird
 * oder das Spiel zu Ende ist (Game Over).
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class NormalGamePlayState implements IGameState {

	/** Der aktive SubgameState */
	private ISubGameState currentSubGameState = null;
	
	/** Referenz auf das Game-Objekt */
	private Game game = null;
	
	/** Die Anzeige der Punkte */
	private ScoreView scoreView = null;
	
	/** Die aktuelle Punktzahl */
	private Score score = null;
	
	/** Die aktuelle Map */
	private Map map = null;
	
	/** Der Manager für alle Enemies */
	private EnemyManager enemyManager = null;
	
	/** Auf dieser Komponente läuft das Spiel */
	private Component component = null;
	
	/** Der SpriteManager */
	private SpriteManager spriteManager = null;
	
	/** Klasse die alle aktuell ablaufenden Effekte verwaltet */
	private EffectManager effectManager = null;
	
	/** Der Spieler */
	private Player player = null;
	
	/** Das Sprite mit dem Mainchar */
	private Sprite mainChar = null;
	
	/** Die Koordinaten des letzten Checkpoints */
	private Point lastCheckpoint = null;

	/**
	 * Konstruktor.
	 * 
	 * @param game das Spiel für das wir einen Zustand abbilden
	 * @param component die Komponente auf die gezeichnet werden soll
	 * @throws GameStateException 
	 */
	public NormalGamePlayState(Game game, Component component) throws GameStateException {
		this.component = component;
		this.game = game;
		this.currentSubGameState = null;
	}
	
	/**
	 * Liefert die Koordinaten des letzten Checkpoints.
	 * 
	 */
	public Point getLastCheckpoint() {
		return lastCheckpoint;
	}
	
	/**
	 * Setzt den letzten Checkpoint.
	 */
	public void setLastCheckpoint(Point point) {
		lastCheckpoint = point;
	}
	
	/**
	 * Bereitet das Spielen eines Levels vor.
	 * 
	 * @param name der Dateiname des Levels
	 * @throws GameStateException 
	 */
	public void initLevel(String name) throws GameStateException {
		
		try {
			map = new Map(name, component);
		} catch (MapException e1) {
			throw new GameStateException("Die Map konnte nicht geladen werden",e1);
		}
		
		enemyManager = new EnemyManager();
		effectManager = new EffectManager();
		spriteManager = new SpriteManager(map);
		
		try {
			mainChar = new Sprite("mainchar.sprite");
			spriteManager.add(mainChar);
			mainChar.setVisible(true);
			mainChar.setCurrentAnimation(SpriteAnimation.IDLE_RIGHT);
		} catch (SpriteException e) {
			throw new GameStateException("Das Sprite des Hauptchars konnte nicht geladen werden",e);
		}

		Point pos = map.getPosMapPixelFromPosMapTiles(map.getStartPosition());
		player = new Player(new FloatPosition(pos.x, pos.y), map, mainChar);
		mainChar.setPosition(pos);
		map.scrollToMapPixelPos(pos);
		
		// Der erste Checkpoint ist automatisch: Der Startpunkt
		setLastCheckpoint(pos);

		// In der Map nach Enemy-Startpunkten suchen und dort jeweils eine
		// Enemy für erzeugen
		try {
			createEnemys();
		} catch (GameException e) {
			throw new GameStateException("Die Feinde konnten nicht in die Map gesetzt werden",e);
		}
		
	}
	
	/**
	 * Methode die aufgerufen wird wenn dieser Zustand betreten wird.
	 * 
	 * @throws GameStateException 
	 */
	public void enterState() throws GameStateException {
		score = new Score();
		try {
			scoreView = new ScoreView(component);
		} catch (ResourceManagerException e1) {
			throw new GameStateException("Fehler beim Erzeugen der ScoreView", e1);
		}
		score.addScoreListener(scoreView);

		initLevel("level01.map");
		switchState(new ReadyState(this));
	}

	/**
	 * Wechselt den aktiven Zustand.
	 * 
	 * @param state der neue Zustand der angenommen werden soll
	 * @throws GameStateException
	 */
	public void switchState(ISubGameState state) throws GameStateException {
		if ( currentSubGameState != null ) {
			currentSubGameState.leaveState();
		}
		currentSubGameState = state;
		currentSubGameState.enterState();
	}

	/**
	 * Methode die aufgerufen wird wenn dieser Zustand verlassen wird.
	 */
	public void leaveState() {
		score.removeScoreListener(scoreView);
		effectManager.flush();
		enemyManager.flush();
		spriteManager.flush();
	}

	/**
	 * Methode die aufgerufen wird um pro Frame einen Spielschritt auszuführen.
	 * @throws GameStateException 
	 */
	public void step() throws GameStateException {
		currentSubGameState.step();
	}

	/**
	 * Zeichnen des aktuellen Zustands.
	 * 
	 * @param image auf dieses Image wird gezeichnet
	 */
	public void paint(Image image) {
		currentSubGameState.paint(image);
	}
	
	/**
	 * Liefert anhand des eingestellten Schwierigkeitsgrads die
	 * Factory für die Feinde.
	 * 
	 * @return die zuständige konkrete Factory.
	 */
	protected AbstractEnemyFactory getEnemyFactory() {
		AbstractEnemyFactory enemyFactory = null;
		int difficulty = (Integer)game.getConfigMap().get("difficulty");
		
		switch ( difficulty ) {
			case 0:
				enemyFactory = new EasyEnemyFactory();
				break;
			case 1:
				enemyFactory = new NormalEnemyFactory();
				break;
			case 2:
				enemyFactory = new HardEnemyFactory();
				break;
			default:
				enemyFactory = new EasyEnemyFactory();
		}
		
		return enemyFactory;
	}
	
	/**
	 * Sucht in der geladenen Map nach Startpunkten für Feinde und erzeugt sie dann dort.
	 * 
	 * Diese Methode wird einmal beim Start eines neuen Levels aufgerufen.
	 * 
	 * @throws GameException
	 */
	protected void createEnemys() throws GameException {
		
		AbstractEnemyFactory enemyFactory = getEnemyFactory();
		
		for ( int x=0 ; x<map.getWidth() ; x++ ) {
			for ( int y=0 ; y<map.getHeight() ; y++ ) {
				Tile tile = map.getTileAt(x,y);
				if ( tile.isEnemy() ) {
					Sprite enemy1Sprite;
					Enemy enemy1;
					try {
						// Ein neues Sprite für diesen Feind erzeugen 
						// (jeder braucht ein eigenes wegen der Animation)
						enemy1Sprite = new Sprite("enemy1.sprite");
						// Sichtbar schalten
						enemy1Sprite.setVisible(true);
						// Eine Animation auswählen (es muss immer eine aktiv sein)
						enemy1Sprite.setCurrentAnimation(SpriteAnimation.IDLE_RIGHT);
						
						// Das Sprite unter die Kontrolle des Sprite-Managers stellen
						spriteManager.add(enemy1Sprite);
						
						// Das Enemy-Objekt erzeugen...
						Point posx = map.getPosMapPixelFromPosMapTiles(new Point(x,y));
						enemy1 = enemyFactory.createEnemy(score.getLevel(), posx, enemy1Sprite);
						
						// ...und dem EnemyManager übergeben
						enemyManager.addEnemy(enemy1);
						
					} catch (SpriteException e) {
						throw new GameException("Fehler beim Erstellen eines Feindes!",e);
					}
				}
			}
		}
	}

	public EffectManager getEffectManager() {
		return effectManager;
	}

	public EnemyManager getEnemyManager() {
		return enemyManager;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}

	public ScoreView getScoreView() {
		return scoreView;
	}
	
	public IGameController getGameController() {
		return game.getGameController();
	}

	public Sprite getMainChar() {
		return mainChar;
	}

	public Score getScore() {
		return score;
	}
	
	public SpriteManager getSpriteManager() {
		return spriteManager;
	}
	
	public Game getGame() {
		return game;
	}
}
