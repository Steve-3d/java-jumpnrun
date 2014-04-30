package de.gaffga.jumpnrun.game.states;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import de.gaffga.jumpnrun.game.Game;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.game.IGameState;
import de.gaffga.jumpnrun.menu.IMenuActionListener;
import de.gaffga.jumpnrun.menu.MenuException;
import de.gaffga.jumpnrun.menu.MenuSystem;
import de.gaffga.jumpnrun.resources.ResourceManager;
import de.gaffga.jumpnrun.resources.ResourceManagerException;

public class MainMenuState implements IGameState, IMenuActionListener {

	/** Das Menüsystem */
	private MenuSystem menuSystem = null;
	
	/** Referenz auf das Game-Objekt */
	private Game game = null;
	
	/** Die Komponente in der das Spiel läuft */
	private Component component = null;
	
	public MainMenuState(Game game, Component component) throws IOException, ResourceManagerException {
		menuSystem = new MenuSystem(game.getGameController(), component, game.getConfigMap());
		menuSystem.readMenu(ResourceManager.getInstance().getProperties("menu.data"));
		menuSystem.setVisible(true);
		menuSystem.addActionListener(this);
		
		this.game = game;
		this.component = component;
	}
	
	public void enterState() throws GameStateException {
	}

	public void leaveState() {
	}

	public void paint(Image image) {
		Graphics g = image.getGraphics();
		menuSystem.paint(g);
	}

	public void step() throws GameStateException {
		try {
			menuSystem.step();
		} catch (MenuException e) {
			throw new GameStateException(e);
		}
	}

	public void actionPerformed(String actionName) throws MenuException {
		if ( actionName.equals("startgame")) {
			try {
				game.switchGameState(new NormalGamePlayState(game, component));
			} catch (GameStateException e) {
				throw new MenuException("Fehler beim Wechsel zum NormalGamePlayState", e);
			}
		} else if ( actionName.equals("exit")) {
			game.exit();
		}
	}

}
