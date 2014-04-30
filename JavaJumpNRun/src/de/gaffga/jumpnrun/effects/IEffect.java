package de.gaffga.jumpnrun.effects;

import java.awt.Image;

/**
 * Interface für alle Effekte.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public interface IEffect {
	
	/**
	 * Startet einen Effekt indem er auf die Startkonfiguration zurückgestellt wird.
	 */
	public void start();
	
	/**
	 * Führ einen Berechnungsschritt für den Effekt durch. 
	 */
	public void step();
	
	/**
	 * Liefert true falls der Effekt zu Ende ist.
	 * 
	 * @return true falls der Effekt zu Ende ist, sonst false
	 */
	public boolean isFinished();
	
	/**
	 * Zeichnet den Effekt auf eine Bitmap.
	 */
	public void draw(Image image);
}
