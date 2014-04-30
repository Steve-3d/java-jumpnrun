package de.gaffga.jumpnrun.tiles;

import java.awt.Point;


/**
 * Container für die Information welches Tile gerade berührt wird und
 * wo dieses Tile sich in der Map befindet.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class TouchingTile {

	/** Das Tile */
	private Tile tile = null;
	
	/** Die Position in MapTile-Koordinaten */
	private Point position = null;
	
	/** 
	 * Konstruktor.
	 * 
	 * @param tile das Tile das gerade berührt wird
	 * @param pos die Position des Tiles in der Map in MapTile-Koordinaten
	 */
	public TouchingTile(Tile tile, Point pos) {
		this.tile = tile;
		this.position = pos;
	}
	
	/**
	 * Liefert die Position des Tiles.
	 * 
	 * @return die Position
	 */
	public Point getPosition() {
		return position;
	}
	
	/**
	 * Liefert das Tile.
	 * 
	 * @return das Tile
	 */
	public Tile getTile() {
		return tile;
	}
}
