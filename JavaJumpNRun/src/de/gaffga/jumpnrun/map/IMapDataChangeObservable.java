package de.gaffga.jumpnrun.map;

/**
 * Interface das das MapData-Objekt observierbar macht.
 *  
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public interface IMapDataChangeObservable {

	/**
	 * Fügt einen neuen Beobachter zur Liste der Beobachter auf Mapänderungen hinzu.
	 * 
	 * @param listener der neue Listener
	 */
	public void addMapChangedListener(IMapDataChangeObserver listener);
	
	/**
	 * Entfernt einen neuen Beobachter aus der Liste der Beobachter auf Mapänderungen.
	 * 
	 * @param listener der zu entfernende Listener
	 */
	public void removeMapChangedListener(IMapDataChangeObserver listener);
	
	/**
	 * Sendet eine Benachrichtigung an alle Observer dass sich ein Tile in der Map
	 * geändert hat bzw geändert wurde.
	 * 
	 * @param x die X-Koordinate des geänderten Tiles
	 * @param y die Y-Koordinate des geänderten Tiles 
	 */
	public void fireMapDataChanged(int x, int y);

}
