package de.gaffga.jumpnrun.game;

import java.awt.Image;

/**
 * Interface das alle Zustände des Spiels implementieren müssen.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public interface IGameState {

	/**
	 * Methode die aufgerufen wird wenn dieser Zustand betreten wird.
	 */
    public void enterState() throws GameStateException;

    /**
	 * Methode die aufgerufen wird wenn dieser Zustand verlassen wird.
	 */
    public void leaveState();
    
	/**
	 * Methode die aufgerufen wird um pro Frame einen Spielschritt auszuführen.
	 */
    public void step() throws GameStateException;
    
    /**
     * Zeichnen des aktuellen Zustands
     */
    public void paint(Image image);
}
