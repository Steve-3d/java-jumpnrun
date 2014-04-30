package de.gaffga.jumpnrun.map;

import de.gaffga.jumpnrun.tiles.Tile;

/**
 * Ein kollidierbares Spielelement.
 * 
 * Jedes Element das auf Kollision mit Map-Elementen 
 * abgefragt werden können soll muss dieses Interface
 * implementieren.
 *  
 * @author Stefan Gaffga <stefan@gaffga.de>
 *
 */
public interface ICollidable {
	
	/**
	 * Liefert die Info ob das Objekt durch ein Tile eine
	 * Kollision erfahren würde.
	 * 
	 * @param tile das zu prüfende Tile
	 * @return true falls das Tile eine Kollision auslösen soll, false wenn nicht.
	 */
	public boolean isBlockedBy(Tile tile);
}
