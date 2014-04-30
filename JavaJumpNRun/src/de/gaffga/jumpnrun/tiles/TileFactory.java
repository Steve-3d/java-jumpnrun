package de.gaffga.jumpnrun.tiles;

import java.awt.Image;
import java.util.HashMap;
import java.util.Properties;

import de.gaffga.jumpnrun.resources.ResourceManager;
import de.gaffga.jumpnrun.resources.ResourceManagerException;

/**
 * Diese Factory erzeugt die verschiedenen Bildschirmelemente aus denen die Grafik
 * zusammengesetzt wird. Es handelt sich hierbei nicht um eine Factory im klassischen
 * Sinne da nicht für jede Anfrage ein neues Tile erzeugt wird sondern die bereits
 * erzeugten zurückgegeben werden.
 *  
 * Diese Klasse passt eher zum Flyweight (Fliegengewicht) Muster: Die Tiles sind die
 * Fliegengewichte und diese Factory ist die Fliegengewichtfabrik.
 * 
 * Zudem ist diese Klasse als Singleton implementiert damit es nur eine davon gibt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class TileFactory {

	/** Map mit allen geladenen Tiles - als Key dient der Code */
	private HashMap<Character, Tile> tileMap = null;
	
	/** Das Background-Tile */
	private Tile backgroundTile = null; 

	/** Die Referenz auf die Singletoninstanz */
	private static TileFactory instance = null;
	
	/** Die Größe eines Tiles */
	int tileSize = -1;

	/**
	 * Konstruktor.
	 * @throws TileFactoryException 
	 */
	private TileFactory() throws TileFactoryException {
		tileMap = new HashMap<Character, Tile>();
		
		try {
			initTileMap();
		} catch (ResourceManagerException e) {
			throw new TileFactoryException("Fehler beim Lesen der Tile-Konfiguration", e);
		}
	}
	
	/**
	 * Liefert die einzige Instanz.
	 * 
	 * @return die einzige Instanz.
	 */
	public static TileFactory getInstance() {
		if ( instance == null ) {
			try {
				instance = new TileFactory();
			} catch (TileFactoryException e) {
				e.printStackTrace();
			}
		}
		
		return instance;
	}
	
	/**
	 * Liefert ein Tile zum angegebenen Code.
	 * 
	 * @param code der Code des gewünschten Tiles.
	 */
	public Tile getTile(char code) {
		return tileMap.get(code);
	}
	
	/**
	 * Liefert das als BackgroundTile konfigurierte Tile.
	 * 
	 * @return das Background-Tile
	 */
	public Tile getBackgroundTile() {
		return backgroundTile;
	}
	
	/**
	 * Lädt die tiles.properties ein und initialisiert damit die TileFactory.
	 * 
	 * @throws ResourceManagerException
	 */
	private void initTileMap() throws ResourceManagerException {
		Properties prop = ResourceManager.getInstance().getProperties("tiles.properties");
		String imageFile = null;
		String flags = null;
		String score = null;
		String code = null;
		String enemy = null;
		Image image = null;
		
		// Das erste Tile hat in der Konfiguration die Nummer 1
		int nummer=1;
		
		do {
			// Lesen der Einträge in der Konfiguration
			imageFile = prop.getProperty("tile_"+nummer+"_imagefile");
			flags = prop.getProperty("tile_"+nummer+"_flags");
			score = prop.getProperty("tile_"+nummer+"_score");
			code = prop.getProperty("tile_"+nummer+"_code");
			enemy = prop.getProperty("tile_"+nummer+"_enemy");

			// Falls keine Werte angegeben wurden sinnvolle Defaults vergeben
			if ( flags == null ) flags="";
			if ( score == null ) score="0";
			if ( imageFile == null ) imageFile="";
			if ( enemy == null ) enemy="0";
			
			// Das Code-Feld ist das einzige Pflichtfeld - nur wenn der Code
			// angegeben ist kann ein Tile verarbeitet werden
			if ( code != null ) {
				// Ein vollwertiges Tile liegt vor
				
				// Das Image des Tiles aus dem ResourceManager besorgen falls eines
				// angegeben wurde
				if ( ! imageFile.trim().equals("")) {
					image = ResourceManager.getInstance().getImage(imageFile);
				} else {
					image = null;
				}
				
				// Das Tile erzeugen und speichern
				Tile tile = new Tile(code.charAt(0),image,flags,Integer.parseInt(score),Integer.parseInt(enemy));
				tileMap.put(tile.getCode(), tile);
			}
			
			nummer++;
		} while(code!=null);
		
		// Falls konfiguriert das Hintergrundtile holen
		String backgroundCode = prop.getProperty("background_tile_code");
		if ( backgroundCode != null ) {
			backgroundTile = tileMap.get(backgroundCode.trim().charAt(0));
		}
		
		String tsize = prop.getProperty("tilesize");
		if ( tsize != null ) {
			tileSize = Integer.parseInt(tsize);
		}
	}
	
	/**
	 * Liefert die Größe eines Tiles.
	 * 
	 * Tiles sind immer quadratisch.
	 * 
	 * @return die Größe in Pixeln
	 */
	public int getTileSize() {
		return tileSize;
	}
}
