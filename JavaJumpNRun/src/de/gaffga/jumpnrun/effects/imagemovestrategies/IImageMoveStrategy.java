package de.gaffga.jumpnrun.effects.imagemovestrategies;

import java.awt.Point;

/**
 * Interface das alle Strategien zum Bewegen von Bildern als Effekt implementieren müssen.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public interface IImageMoveStrategy {

	/**
	 * Prüft, ob die Bewegung am Ende angekommen ist.
	 * 
	 * @return true falls die Bewegung zu Ende ist, sonst false.
	 */
	public boolean isFinished();
	
	/**
	 * Startet die Bewegung neu.
	 */
	public void start();
	
	/**
	 * Liefert die aktuelle Position der Bewegung.
	 * 
	 * @return die aktuelle Position
	 */
	public Point getCurrentPosition();
	
	/**
	 * Führt einen Schritt der Bewegung aus.
	 */
	public void step();
}
