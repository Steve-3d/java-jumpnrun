package de.gaffga.jumpnrun.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Kapselung von Mapdaten - die Dimensionen der Map sowie der Inhalte.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class MapData implements IMapDataChangeObservable {

	/** Die Breite der Map */
	private int width;
	
	/** Die Höhe der Map */
	private int height;
	
	/** Die Daten der Map */
	private char[][] data;
	
	/** Liste aller Listener für Änderungen an der Map */
	private List<IMapDataChangeObserver> mapDataChangedListeners = null;
	
	/**
	 * Konstruktor.
	 */
	public MapData(int width, int height, char[][] data) {
		this.width = width;
		this.height = height;
		this.data = data;
		
		mapDataChangedListeners = new ArrayList<IMapDataChangeObserver>(); 
	}
	
	/**
	 * Kopierkonstruktor.
	 * 
	 * Da die MapDaten während des Spielens verändert werden dürfen wir
	 * das Original nicht verändern und bieten einen Kopierkonstruktor dafür
	 * an.
	 * 
	 * @param data das Data-Objekt das kopiert werden soll
	 */
	public MapData(MapData data) {
		this.width = data.width;
		this.height = data.height;
		this.data = new char[width][height];
		
		for ( int x=0 ; x<width ; x++ ) {
			for ( int y=0 ; y<height ; y++ ) {
				this.data[x][y] = data.data[x][y];
			}
		}
		
		mapDataChangedListeners = new ArrayList<IMapDataChangeObserver>(); 
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @throws MapDataException 
	 */
	public MapData(InputStream inStream) throws MapDataException {
		mapDataChangedListeners = new ArrayList<IMapDataChangeObserver>(); 

		InputStreamReader reader = new InputStreamReader(inStream);
		BufferedReader breader = new BufferedReader(reader);
		String zeile;
		ArrayList<String> zeilen = new ArrayList<String>();
		
		try {
			zeile = breader.readLine();
			while(zeile != null) {
				zeilen.add(zeile);
				zeile = breader.readLine();
			}
		} catch (IOException e) {
			throw new MapDataException("Fehler beim Lesen der Mapdaten");
		}
		
		// Die Dimensionen der Map ermitteln
		height = zeilen.size();
		width = 9999999;
		for ( String currentZeile : zeilen ) {
			width = Math.min(width, currentZeile.length() );
		}
		
		// Die Mapdaten in das Array übertragen
		data = new char[width][height];
		
		for ( int y=0 ; y<height ; y++ ) {
			for ( int x=0 ; x<width ; x++ ) {
				data[x][y] = zeilen.get(y).charAt(x);
			}
		}
	}
	
	/**
	 * Fügt einen neuen Beobachter zur Liste der Beobachter auf Mapänderungen hinzu.
	 * 
	 * @param listener der neue Listener
	 */
	public void addMapChangedListener(IMapDataChangeObserver listener) {
		mapDataChangedListeners.add(listener);
	}
	
	/**
	 * Entfernt einen neuen Beobachter aus der Liste der Beobachter auf Mapänderungen.
	 * 
	 * @param listener der zu entfernende Listener
	 */
	public void removeMapChangedListener(IMapDataChangeObserver listener) {
		mapDataChangedListeners.remove(listener);
	}
	
	/**
	 * Liefert die Breite der Map
	 * 
	 * @return die Breite der Map
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Liefert die Höhe der Map
	 * 
	 * @return die Höhe der Map
	 */
	public int getHeight() {
		return height;
	}
	
	/** 
	 * Liefert den Code des Tiles an der angegebenen Position.
	 * 
	 * @param x die X-Koordinate
	 * @param y die Y-Koordinate
	 * @return der Code des Tiles
	 */
	public char getTile(int x, int y) {
		return data[x][height-y-1];
	}
	
	/**
	 * Setzt ein Tile.
	 * 
	 * @param x die X-Koordinate
	 * @param y die Y-Koordinate
	 * @param c der Code des Tiles
	 */
	public void setTile(int x, int y, char c) {
		data[x][height-y-1]=c;
		fireMapDataChanged(x, y);
	}
	
	/**
	 * Sendet eine Benachrichtigung an alle Observer dass sich ein Tile in der Map
	 * geändert hat bzw geändert wurde.
	 * 
	 * @param x die X-Koordinate des geänderten Tiles
	 * @param y die Y-Koordinate des geänderten Tiles 
	 */
	public void fireMapDataChanged(int x, int y) {
		for ( IMapDataChangeObserver l : mapDataChangedListeners ) {
			l.mapDataChanged(x,y);
		}
	}
}
