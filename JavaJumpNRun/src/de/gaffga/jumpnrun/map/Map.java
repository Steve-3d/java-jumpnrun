package de.gaffga.jumpnrun.map;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import de.gaffga.jumpnrun.FloatPosition;
import de.gaffga.jumpnrun.resources.ResourceManager;
import de.gaffga.jumpnrun.resources.ResourceManagerException;
import de.gaffga.jumpnrun.tiles.Tile;
import de.gaffga.jumpnrun.tiles.TileFactory;
import de.gaffga.jumpnrun.tiles.TileStatus;
import de.gaffga.jumpnrun.tiles.TouchingTile;

/**
 * Eine Map die aus Tiles besteht.
 * 
 * Beim Erzeugen wird die Map aus der Mapdefinitionsdatei erzeugt. Der Zustand
 * der Map wird getrennt von ihrem Aussehen gespeichert. 
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Map implements IMapDataChangeObserver {

	/** Eine Kollision mit der linken Kante fand statt */	
	public final static int COLLISION_WEST = 1;
	
	/** Eine Kollision mit der rechten Kante fand statt */	
	public final static int COLLISION_EAST = 2;
	
	/** Eine Kollision mit der oberen Kante fand statt */	
	public final static int COLLISION_NORTH = 4;

	/** Eine Kollision mit der unteren Kante fand statt */	
	public final static int COLLISION_SOUTH = 8;
	
	/** Die Inhalte der Map */
	private MapData mapData = null;

	/** Das Image der gesamten Map */
	private Image mapImage = null;
	
	/** Die Zustandsinformationen für die Map */
	private TileStatus[][] tileStatus = null;
	
	/** Die linke untere Ecke des sichtbaren Mapausschnitts */
	private Point mapViewPosition = null;
	
	/** Die Breite des sichtbaren Bereichs auf dem Bildschirm */
	private int screenPixelWidth;
	
	/** Die Höhe des sichtbaren Bereichs auf dem Bildschirm */
	private int screenPixelHeight;
	
	/** Die Breite der Map in Pixeln */
	private int mapPixelWidth;
	
	/** Die Höher der Map in Pixeln */
	private int mapPixelHeight;
		
	/**
	 * Konstruktor für eine neue Map.
	 * 
	 * Die Daten der Map werden über den ResourceManager gelesen.
	 *
	 * @param name der Dateiname der zu ladenden Map
	 * @param component die AWT-Komponente auf die die Map gezeichnet werden soll
	 * @throws MapException 
	 * @throws ResourceManagerException 
	 */
	public Map(String name, Component component) throws MapException {
		
		try {
			mapData = new MapData(ResourceManager.getInstance().getMapData(name));
			tileStatus = new TileStatus[mapData.getWidth()][mapData.getHeight()];
			for ( int x=0 ; x<mapData.getWidth() ; x++ ) {
				for ( int y=0 ; y<mapData.getHeight() ; y++ ) {
					tileStatus[x][y] = new TileStatus();
				}
			}
		} catch (ResourceManagerException e) {
			throw new MapException("Die Map " + name + " konnte nicht geladen werden!",e);
		}
		
		this.mapViewPosition = new Point();
		this.screenPixelWidth = component.getWidth();
		this.screenPixelHeight = component.getHeight();
		int tilesize = TileFactory.getInstance().getTileSize();
		this.mapPixelWidth = mapData.getWidth() * tilesize;
		this.mapPixelHeight = mapData.getHeight() * tilesize;
		
		// Das Map-Image (Ein Image mit der gesamten Map) erzeugen
		GraphicsConfiguration gc = component.getGraphicsConfiguration();
		mapImage = gc.createCompatibleImage(mapData.getWidth()*tilesize, mapData.getHeight()*tilesize);
		initMapImage();

		// Wir müssen unsere Back-Surface bei Änderungen an der Map aktualisieren
		mapData.addMapChangedListener(this);
	}
	
	/**
	 * Erzeugt ein Bild der gesamten Map im mapImage.
	 */
	protected void initMapImage() {
		Graphics g = mapImage.getGraphics();
		Image backgroundTileImage = TileFactory.getInstance().getBackgroundTile().getImage();
		Point point = new Point();
		int tilesize = TileFactory.getInstance().getTileSize();
			
		// Alle Tiles der Map durchlaufen
		for ( int x=0 ; x<getWidth() ; x++ ) {
			for ( int y=0 ; y<getHeight() ; y++ ) {
				
				// Das entsprechende Tile ermitteln
				Tile tile = getTileAt(x, y);
				
				// Die Position in Pixeln errechnen
				point.x = x * tilesize;
				point.y = (getHeight() - y - 1) * tilesize;
				
				// Falls das Tile einen Hintergrund braucht diesen als erstes zeichnen
				if ( tile.isUsebackground() ) {
					g.drawImage(backgroundTileImage, point.x, point.y, null);
				}
					
				if ( tile.isVisible() ) {
					// Das Tile nun zeichnen
					Image image = tile.getImage();
					g.drawImage(image, point.x, point.y, null);
				}
			}
		}
	}
	
	/**
	 * Liefert das Tile-Objekt für eine bestimmte Position.
	 * 
	 * @param x die X-Koordinate
	 * @param y die Y-Koordinate
	 * @return das Tile-Objekt
	 */
	public Tile getTileAt(int x, int y) {
		return TileFactory.getInstance().getTile(mapData.getTile(x, y));
	}
	
	/**
	 * Tauscht ein Tile auf der Map aus.
	 * 
	 * @param x die X-Koordinate
	 * @param y die Y-Koordinate
	 * @param c der neue Tile-Code
	 */
	public void setTileAt(int x, int y, char c) {
		mapData.setTile(x, y, c);
	}
	
	/**
	 * Liefert die Höhe der Map
	 * 
	 * @return die Höhe der Map in Tiles
	 */
	public int getHeight() {
		return mapData.getHeight();
	}
	
	/**
	 * Liefert die Breite der Map
	 * 
	 * @return die Breite der Map in Tiles
	 */
	public int getWidth() {
		return mapData.getWidth();
	}
	
	/**
	 * Sucht das Tile das als Spieler-Startposition markiert ist und 
	 * liefert seine Position in Pixelkoordinaten zurück.
	 * 
	 * @return die Position des Spielerstartpunkts in MapTile-Koordinaten
	 */
	public Point getStartPosition() {
		for ( int x=0 ; x<getWidth() ; x++ ) {
			for ( int y=0 ; y<getHeight() ; y++ ) {
				Tile tile = getTileAt(x,y);
				if ( tile.isStart() ) {
					return new Point(x, y);
				}
			}
		}
		
		return null;
	}

	/**
	 * Wrapper-Methode um checkMove mit einer Hitbox aufrufen zu können.
	 * 
	 * @param objekt das Objekt das auf Kollision getestet werden soll
	 * @param pos1 die Startposition
	 * @param pos2 die Zielposition
	 * @param hitbox die Hitbox
	 */
	public int checkMove(ICollidable objekt, FloatPosition pos1, FloatPosition pos2, Rectangle hitbox) {
		FloatPosition posa = new FloatPosition(pos1);
		
		posa.x += hitbox.x;
		posa.y += hitbox.y;
		
		pos2.x += hitbox.x;
		pos2.y += hitbox.y;
		
		int rc = checkMove(objekt, posa, pos2, hitbox.width, hitbox.height);
		
		pos2.x -= hitbox.x;
		pos2.y -= hitbox.y;
		
		return rc;
	}
	/**
	 * Prüft ob die Bewegung von currentPos nach newPos ohne Kollision möglich ist.
	 * 
	 * Falls eine Kollision mit einem blockierenden Map-Element festgestellt wird,
	 * wird die Bewegung entsprechend angeglichen (Sliding) oder vollständig gestoppt.
	 * Die übergebene newPos wird dann entsprechend modifiziert!
	 * 
	 * Die Methode geht davon aus, dass zum Startpunkt (an der Position "currentPos") keine
	 * Kollision vorliegt! Sollte dies doch so sein, so ist das Verhalten der Methode
	 * nicht definiert.
	 * 
	 * XXX Collision: Wenn man gegen eine Mauer mit Loch springt rutscht man durch den Boden!
	 * XXX Collision: Wenn die Bewegung mehrer Tiles auf einmal umfasst funktioniert die Erkennung nicht. 
	 *
	 * @param objekt das Objekt das auf Kollision getestet werden soll
	 * @param currentPos Startpunkt der Bewegung 
	 * @param newPos Zielpunkt der Bewegung
	 * @param width Breite des zu bewegenden Elements
	 * @param height Höhe des zu bewegenden Elements
	 * @return Flags welche Kollision aufgetreten ist
	 */
	public int checkMove(ICollidable objekt, FloatPosition currentPos, FloatPosition newPos, int width, int height) {
		Tile tile;
		int tilesize = TileFactory.getInstance().getTileSize();
		int collision = 0;
		
		// Falls die neue Position um ungültigen Bereich liegt direkt ablehnen
		if ( newPos.x < 0 ) {
			newPos.x=0;
		}
		if ( newPos.x > getWidth()*tilesize - width - 1) {
			newPos.x=getWidth()*tilesize - width - 1;
		}
		if ( newPos.y < 0 ) {
			newPos.y=0;
		}
		if ( newPos.y > getHeight()*tilesize - height - 1 ) {
			newPos.y=getHeight()*tilesize - height - 1;
		}
		
		// Herausfinden welchen Bereich in Tile-Koordinaten die currentPos einnimmt
		int curX0 = (int)currentPos.x / tilesize;
		int curY0 = (int)currentPos.y / tilesize;
		int curX1 = (int)(currentPos.x + width-1) / tilesize;
		int curY1 = (int)(currentPos.y + height-1) / tilesize;
		
		// Herausfinden welchen Bereich in Tile-Koordinaten die newPos einnimmt
		int newX0 = (int)newPos.x / tilesize;
		int newY0 = (int)newPos.y / tilesize;
		int newX1 = (int)(newPos.x + width-1) / tilesize;
		int newY1 = (int)(newPos.y + height-1) / tilesize;
		
		// Falls beide Bereich identisch sind liegt keine Kollision vor (da die Ausgangsposition)
		// per Definition kollisionsfrei war
		if ( curX0==newX0 && curY0==newY0 && curX1==newX1 && curY1==newY1 ) {
			// Keine Kollision
			return 0;
		}

		// den horizontalen Bewegungsanteil prüfen
		for ( int i=curY0 ; i<=curY1 ; i++ ) {
			// Bei einer Bewegung nach rechts stossen wir mit der rechten Kante als erstes an
			tile = getTileAt(newX1, i);
			if ( objekt.isBlockedBy(tile) ) {
				// Eine Kollision wurde festgestellt! Die horizontale Bewegung kann nicht durch-
				// geführt werden

				// Soweit bewegen wie möglich 
				newPos.x = newX1 * tilesize - width;
				collision |= COLLISION_EAST;
				break;
			}
			
			// Bei einer Bewegung nach links stossen wir mit der linken Kante als erstes an
			tile = getTileAt(newX0, i);
			if ( objekt.isBlockedBy(tile) ) {
				// Eine Kollision wurde festgestellt! Die horizontale Bewegung kann nicht durch-
				// geführt werden

				// Soweit bewegen wie möglich 
				newPos.x = newX0 * tilesize + tilesize; 
				collision |= COLLISION_WEST;
				break;
			}
		}
		
		// den vertikalen Bewegungsanteil prüfen
		for ( int i=curX0 ; i<=curX1 ; i++ ) {
			// Bei einer Bewegung nach oben stossen wir mit der oberen Kante als erstes an
			tile = getTileAt(i, newY1);
			if ( objekt.isBlockedBy(tile) ) {
				// Eine Kollision wurde festgestellt! Die vertikale Bewegung kann nicht durch-
				// geführt werden

				// Soweit bewegen wie möglich 
				newPos.y = newY1 * tilesize - height;
				collision |= COLLISION_NORTH;
				break;
			}

			// Bei einer Bewegung nach unten stossen wir mit der unteren Kante als erstes an
			tile = getTileAt(i, newY0);
			if ( objekt.isBlockedBy(tile) ) {
				// Eine Kollision wurde festgestellt! Die vertikale Bewegung kann nicht durch-
				// geführt werden

				// Soweit bewegen wie möglich 
				newPos.y = newY0 * tilesize + tilesize;
				collision |= COLLISION_SOUTH;
				break;
			}
		}
		
		return collision;
	}

	/**
	 * Liefert eine Liste von Tiles die die angegebene Hitbox gerade berührt.
	 * 
	 * @param pos die aktuelle Position des zu testenden Objekts
	 * @param hitbox die zu testende Hitbox
	 * @return Liste mit Tiles die der Spieler gerade berührt
	 */
	public List<TouchingTile> getTouchingTiles(Point pos, Rectangle hitbox) {
		LinkedList<TouchingTile> tiles = new LinkedList<TouchingTile>();
		
		// Alle Tile-Koordinaten herausfinden die die Hitbox berührt
		int x0 = pos.x + hitbox.x;
		int y0 = pos.y + hitbox.y;
		int x1 = x0 + hitbox.width;
		int y1 = y0 + hitbox.height;
		
		int tilesize = TileFactory.getInstance().getTileSize();
		
		int tilex0 = x0 / tilesize;
		int tiley0 = y0 / tilesize;
		int tilex1 = x1 / tilesize;
		int tiley1 = y1 / tilesize;
		
		for ( int x = tilex0 ; x<=tilex1 ; x++ ) {
			for ( int y = tiley0 ; y<=tiley1 ; y++ ) {
				Tile tile = getTileAt(x, y);
				tiles.add(new TouchingTile(tile, new Point(x,y)));
			}
		}
		
		return tiles;
	}
	
	/**
	 * Liefert das enthaltene MapData-Objekt.
	 * 
	 * @return das MapData-Objekt
	 */
	public MapData getMapData() {
		return mapData;
	}
	
	/**
	 * Zeichnet den sichtbaren Mapausschnitt.
	 * 
	 * @param image auf dieses Image wird gezeichnet
	 */
	public void draw(Image image) {
		Graphics g2 = image.getGraphics();
		
		g2.drawImage(mapImage, 
				0, 0, 
				screenPixelWidth, screenPixelHeight, 
				mapViewPosition.x, mapPixelHeight-screenPixelHeight - mapViewPosition.y, 
				mapViewPosition.x+screenPixelWidth, mapPixelHeight-screenPixelHeight - mapViewPosition.y+screenPixelHeight, null);
	}
	
	/**
	 * Wenn sich ein Tile auf der Map geändert hat müssen wir unsere 
	 * Hintergrundmap anpassen. Dies ist z.B. dann der Fall wenn der
	 * Spieler ein Item eingesammelt hat - dann müssen wir dies natürlich
	 * von der Map entfernen.
	 * 
	 * @param x die X-Koordinate der Änderung (in Tiles)
	 * @param y die Y-Koordinate der Änderung (in Tiles)
	 */
	
	public void mapDataChanged(int x, int y) {
		Image backgroundTileImage = TileFactory.getInstance().getBackgroundTile().getImage();
		Graphics g = mapImage.getGraphics();
		Point point = new Point();
		int tilesize = TileFactory.getInstance().getTileSize();
				
		// Das entsprechende Tile ermitteln
		Tile tile = getTileAt(x, y);

		// Die Position in Pixeln errechnen
		point.x = x * tilesize;
		point.y = (getHeight() - y - 1) * tilesize;
		
		// Falls das Tile einen Hintergrund braucht diesen als erstes zeichnen
		if ( tile.isUsebackground() ) {
			g.drawImage(backgroundTileImage, point.x, point.y, null);
		}
		
		// Das Tile nun zeichnen
		Image image = tile.getImage();
		g.drawImage(image, point.x, point.y, null);
	}
	
	/**
	 * Rechnet die Map-Pixel-Koordinaten in Screen-Pixel-Koordinaten um.
	 * 
	 * @param point Quellkoordinate
	 * @return Ergebniskoordinate
	 */
	public Point getPosScreenPixelFromPosMapPixel(Point point) {
		Point result = new Point();
		
		result.x = point.x - mapViewPosition.x;
		result.y = screenPixelHeight - point.y + mapViewPosition.y;
		
		return result;
	}
	
	/**
	 * Rechnet die Koordinaten eines Tiles in Map-Koordinaten in
	 * Screen-Pixel-Koordinaten um.
	 * 
	 * @param point Quellkoordinate
	 * @return Ergebniskoordinate
	 */
	public Point getPosScreenPixelFromPosMapTiles(Point point) {
		Point result = new Point();
		int tilesize = TileFactory.getInstance().getTileSize();
		
		result.x = point.x * tilesize - mapViewPosition.x;
		result.y = screenPixelHeight - point.y * tilesize - mapViewPosition.y;
		
		return result;
	}
	
	/**
	 * Rechnet die Position auf dem sichtbaren Screen in Pixeln in die Position auf der Map
	 * in Pixeln um.
	 * 
	 * @param point Quellkoordinate
	 * @return Ergebniskoordinate
	 */
	public Point getPosMapPixelFromPosScreenPixel(Point point) {
		Point result = new Point();
		
		result.x = point.x + mapViewPosition.x;
		result.y = mapViewPosition.y + screenPixelHeight - point.y;
		
		return result;
	}
	
	/**
	 * Rechnet die Position auf dem sichtbaren Screen in Pixeln in die Position auf der Map
	 * in Tiles um.
	 * 
	 * @param point Quellkoordinate
	 * @return Ergebniskoordinate
	 */
	public Point getPosMapTilesFromPosScreenPixel(Point point) {
		Point result = getPosMapPixelFromPosScreenPixel(point);
		int tilesize = TileFactory.getInstance().getTileSize();
		
		result.x /= tilesize;
		result.y /= tilesize;
		
		return result;
	}

	/**
	 * Liefert die Position des aktuell sichtbaren Mapausschnitts.
	 *  
	 * @return die aktuelle Position des sichtbaren Map-Ausschnitts
	 */
	public Point getMapViewPosition() {
		return mapViewPosition;
	}

	/**
	 * Setzt den aktuell sichtbaren Mapausschnitt.
	 * 
	 * @param newPos die neue Position des Ausschnitts
	 */
	public void scrollToMapPixelPos(Point newPos) {
		this.scrollToMapPixelPos(new FloatPosition(newPos));
	}
	
	/** 
	 * Setzt direkt den gewünschten Map-Viewausschnitt.
	 * 
	 * @param pos die zu verwendende mapViewPosition
	 */
	public void setMapViewPosition(Point pos) {
		mapViewPosition = pos;
	}
	
	/**
	 * Ermittelt aus einer Position innerhalb der Map den dafür zu verwendenden Mapausschnitt.
	 * 
	 * @param newPos die Position innerhalb der Map zu der der Ausschnitt ermittelt werden soll
	 * @return die empfohlene MapViewPosition
	 */
	public FloatPosition getMapViewPositionFromPosition(FloatPosition newPos) {
		FloatPosition neuPos = new FloatPosition(mapViewPosition.x, mapViewPosition.y);
		
		// Falls der Spieler weiter als 2/3 der Bildbreite nach rechts gelaufen ist folgt 
		// ihm der Bildausschnitt
		if ( newPos.x - mapViewPosition.x > screenPixelWidth *2.0/3.0 ) {
			neuPos.x = (int) (newPos.x - screenPixelWidth *2.0/3.0); 
		}
		
		// Falls der Spieler weiter als 1/3 der Bildbreite nach links gelaufen ist folgt 
		// ihm der Bildausschnitt
		if ( newPos.x - mapViewPosition.x < screenPixelWidth /3.0 ) {
			neuPos.x = (int) (newPos.x - screenPixelWidth /3.0); 
		}
		
		// Das gleiche nun für die y-Position:
		if ( newPos.y - mapViewPosition.y > screenPixelHeight *2.0/3.0 ) {
			neuPos.y = (int) (newPos.y - screenPixelHeight*2.0/3.0); 
		}
		if ( newPos.y - mapViewPosition.y < screenPixelHeight/3.0 ) {
			neuPos.y = (int) (newPos.y - screenPixelHeight/3.0); 
		}
		
		// Sicherstellen dass der Ausschnitt im erlaubten Bereich bleibt
		if ( neuPos.x < 0 ) {
			neuPos.x=0;
		}
		if ( neuPos.x > mapPixelWidth - screenPixelWidth ) { 
			neuPos.x = mapPixelWidth - screenPixelWidth;
		}
		if ( neuPos.y < 0 ) {
			neuPos.y=0;
		}
		if ( neuPos.y > mapPixelHeight - screenPixelHeight ) { 
			neuPos.y = mapPixelHeight - screenPixelHeight;
		}
		
		return neuPos;
	}
	
	/**
	 * Setzt den aktuell sichtbaren Mapausschnitt anhand der Position die sichtbar
	 * sein soll.
	 * 
	 * @param newPos die Position die sichtbar sein soll
	 */
	public void scrollToMapPixelPos(FloatPosition newPos) {
		FloatPosition fp = getMapViewPositionFromPosition(newPos);
		
		mapViewPosition = new Point((int)fp.x, (int)fp.y);
	}
	
	/**
	 * Rechnet die Koordinaten eines Tiles in Map-Koordinaten in
	 * Map-Pixel-Koordinaten um.
	 * 
	 * @param point Quellkoordinate
	 * @return Ergebniskoordinate
	 */
	public Point getPosMapPixelFromPosMapTiles(Point point) {
		Point result = new Point();
		int tilesize = TileFactory.getInstance().getTileSize();
		
		result.x = point.x * tilesize;
		result.y = point.y * tilesize; 
		
		return result;
	}

	/**
	 * Rechnet die Koordinaten eines Tiles in Map-Koordinaten in
	 * Map-Pixel-Koordinaten um.
	 * 
	 * @param x Die X-Koordinate
	 * @param y Die Y-Koordinate
	 * @return Ergebniskoordinate
	 */
	public Point getPosMapPixelFromPosMapTiles(int x, int y) {
		Point result = new Point();
		int tilesize = TileFactory.getInstance().getTileSize();
		
		result.x = x * tilesize;
		result.y = y * tilesize; 
		
		return result;
	}

	/**
	 * Liefert die Breite der Map in Pixeln.
	 * 
	 * @return die Breite der Map in Pixeln
	 */
	public int getPixelWidth() {
		return mapPixelWidth;
	}

	/**
	 * Liefert die Höhe der Map in Pixeln.
	 * 
	 * @return die Höhe der Map in Pixeln
	 */
	public int getPixelHeight() {
		return mapPixelHeight;
	}
	
	/**
	 * Liefert die Breite des Screens in Pixeln.
	 * 
	 * @return die Breite des Screen in Pixeln
	 */
	public int getScreenPixelWidth() {
		return screenPixelWidth;
	}

	/**
	 * Liefert die Höhe des Screens in Pixeln.
	 * 
	 * @return die Höhe des Screens in Pixeln
	 */
	public int getScreenPixelHeight() {
		return screenPixelHeight;
	}
}
