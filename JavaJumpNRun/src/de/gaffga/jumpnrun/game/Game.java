package de.gaffga.jumpnrun.game;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;

import de.gaffga.jumpnrun.DecoratedText;
import de.gaffga.jumpnrun.FpsMeter;
import de.gaffga.jumpnrun.Timer;
import de.gaffga.jumpnrun.controller.IGameController;
import de.gaffga.jumpnrun.game.states.MainMenuState;
import de.gaffga.jumpnrun.menu.MenuException;
import de.gaffga.jumpnrun.menu.MenuSystem;
import de.gaffga.jumpnrun.resources.ResourceManager;
import de.gaffga.jumpnrun.resources.ResourceManagerException;

/**
 * Diese Klasse speichert den aktuellen Spielzustand und ruft in der
 * Hauptspielschleife die entsprechenden Methoden des aktuellen GameState
 * auf.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Game {

	/** Das Objekt das uns mit Tastaturzuständen versorgt */
	private IGameController input = null;
	
	/** Der aktuelle Spielzustand */
	private IGameState currentGameState = null;
	
	/** Das Image für DoubleBuffering */
	private Image backgroundImage = null;
	
	/** Die Komponente auf die gezeichnet wird */
	private Component component = null;
	
	/** Die Liste mit allen Levels */
	private LevelList levelList = null;
	
	/** Das Ende-Flag für die Hauptspielschleife */
	private boolean ende = false;
	
	/** Die Map mit der Konfiguration */
	private HashMap<String, Object> configMap = null;
	
	/**	Pause-Flag */
	private boolean pause = false;
	
	/** Der Text der eine Pause anzeigt */
	private DecoratedText pauseText = null;
	
	/** Das Menu das während einer Pause angezeigt wird */
	private MenuSystem pauseMenu = null;
	
	/**
	 * Der Konstruktor für ein neues Spiel.

	 * @param keyboardInput das Objekt das die Eingaben für das Spiel bereitstellt
	 * @param component die Komponente auf der gezeichnet werden soll
	 * @throws GameException 
	 * @throws ResourceManagerException 
	 * @throws IOException 
	 */
	public Game(IGameController keyboardInput, Component component) throws GameException, IOException, ResourceManagerException {
		this.input = keyboardInput;
		this.component = component;
		try {
			this.levelList = ResourceManager.getInstance().getLevelList();
		} catch (ResourceManagerException e1) {
			throw new GameException("Fehler beim Laden der LevelList", e1);
		}
		
		// Das Bild für das DoubleBuffering erzeugen
		backgroundImage = component.createVolatileImage(component.getWidth(), component.getHeight());
		
		configMap = new HashMap<String, Object>();
		
		// Defaults setzen
		configMap.put("fpsTransparency", 50);
		
		// Den initialien Zustand erzeugen und setzen
		try {
			switchGameState(new MainMenuState(this, component));
			//switchGameState(new NormalGamePlayState(this, component));
		} catch (GameStateException e) {
			throw new GameException("Konnte den NormalGamePlayState-Zustand nicht setzen", e);
		}
		
		pauseText = new DecoratedText(component, "PAUSE");
		
		pauseMenu = new MenuSystem(input, component, configMap);
		pauseMenu.readMenu(ResourceManager.getInstance().getProperties("pausemenu.data"));
		pauseMenu.setVisible(true);
		pauseMenu.setDrawBackground(false);
	}
	
	/**
	 * Beendet die Hauptspielschleife.
	 */
	public void exit() {
		ende=true;
	}
	
	/**
	 * Dies ist die Hauptspielschleife.
	 * 
	 * Solange das Spiel nicht beendet wird (indem "ende" auf true steht) läuft mit
	 * einer geringen Verzögerung die Hauptschleife immer wieder durch. Nachdem alle
	 * Spielelemente mittels der "step"-Methode etwas Zeit bekommen haben ihren Zustand
	 * zu aktualisieren, wird der aktuelle Spielzustand schließlich auf das backgroundImage
	 * gezeichnet. Dieses wird zuletzt in einem Rutsch auf dem Schirm angezeigt.
	 * 
	 * @throws GameException 
	 * @throws GameStateException 
	 */
	public void start() throws GameException {
		long frameStart;
		
		ende = false;

		while (!ende) {
			frameStart = System.nanoTime();
			
			if ( input.wasKeyHit(KeyEvent.VK_ESCAPE)) {
				setPause(!isPaused());
			}
			
			if ( !pause ) {
				// Den aktuellen Spielzustand bearbeiten
				try {
					currentGameState.step();
				} catch (GameStateException e1) {
					throw new GameException("Fehler beim Ausführen eines GameState-Steps", e1);
				}
			} else {
				try {
					pauseMenu.step();
				} catch (MenuException e) {
					throw new GameException("Fehler beim Ausführen des PauseMenues", e);
				}
			}

			// Zeichnen des aktuellen Spielzustands
			currentGameState.paint(backgroundImage);
			
			Graphics2D bg = (Graphics2D) backgroundImage.getGraphics();
			
			// Falls konfiguriert den FPS-Graphen zeichnen
			if ( (Boolean)configMap.get("showFps") ) {
				Image graph = FpsMeter.getInstance().getGraph();
				float transparency = (float)((Integer)configMap.get("fpsTransparency")) / 100.0f;
				bg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
				
				bg.drawImage(graph, backgroundImage.getWidth(null)-20-graph.getWidth(null), 
						backgroundImage.getHeight(null)-20-graph.getHeight(null), null);
			}
			
			// Falls Pause ist dies anzeigen
			if ( pause ) {
				int x = backgroundImage.getWidth(null)/2;
				int y = backgroundImage.getHeight(null)/2;

				pauseText.paint(backgroundImage, x, y);
				pauseMenu.paint(bg);
			}
			
			// Den gesamten Spielbildschirm anzeigen
			Graphics g = component.getGraphics();
			g.drawImage(backgroundImage, 0, 0, null);
			
			float targetFPS = 60;
			float targetFrameTime = 1000.0f / targetFPS;
			float frameTime = (float) ((System.nanoTime() - frameStart) / 1e6);
			
			long wait = (long) (targetFrameTime - frameTime);
			
			if(wait>0) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					// egal ;-)
				}
			}
		
			// Den Timer informieren, dass ein Frame fertig ist
			FpsMeter.getInstance().notifyNewFrame();
		}
	}
	
	/**
	 * Setzt das Pause-Flag.
	 * 
	 * @param pause
	 */
	public void setPause(boolean pause) {
		this.pause = pause;
		Timer.getInstance().setPause(pause);
	}
	
	/**
	 * Liefert den Pause-Zustand.
	 */
	public boolean isPaused() {
		return pause;
	}

	/**
	 * Wechselt den aktuellen Spielzustand.
	 * 
	 * Dies ist die Hauptmethode im State-Pattern.
	 * 
	 * @param newstate der neue Spielzustand
	 * @throws GameStateException 
	 */
	public void switchGameState(IGameState newstate) throws GameStateException {
		if ( currentGameState != null ) {
			currentGameState.leaveState();
		}
		currentGameState = newstate;
		currentGameState.enterState();
	}

	/**
	 * Liefert das Tastatureingabe-Objekt.
	 * 
	 * @return das KeyboardInput-Objekt
	 */
	public IGameController getGameController() {
		return input;
	}
	
	/**
	 * Liefert die Komponente in der das Spiel läuft.
	 * 
	 * @return die Komponente in der das Spiel läuft.
	 */
	public Component getComponent() {
		return component;
	}
	
	/**
	 * Liefert die LevelList.
	 * 
	 * @return die LevelList
	 */
	public LevelList getLevelList() {
		return levelList;
	}
	
	/**
	 * Liefert die Map mit allen Konfigurationseinstellungen.
	 * 
	 * @return die ConfigMap
	 */
	public HashMap<String, Object> getConfigMap() {
		return configMap;
	}
}
