package de.gaffga.jumpnrun.map;

/**
 * Interface dass alle Klassen implementieren müssen die über Änderungen an den
 * MapDaten die zur Laufzeit passieren informiert werden möchten.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public interface IMapDataChangeObserver {

	/** 
	 * Nachricht, dass sich ein Tile in der Map geändert hat.
	 * 
	 * @param x die Spalte in der Map
	 * @param y die Zeile in der Map
	 */
	public void mapDataChanged(int x, int y);
	
}
