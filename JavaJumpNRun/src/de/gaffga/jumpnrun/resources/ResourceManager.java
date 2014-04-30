package de.gaffga.jumpnrun.resources;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JPanel;

import de.gaffga.jumpnrun.controller.DemoPlayback;
import de.gaffga.jumpnrun.game.LevelList;
import de.gaffga.jumpnrun.map.MapData;
import de.gaffga.jumpnrun.map.MapDataException;

/**
 * Verwaltung der Daten die benötigt werden.
 * 
 * Der ResourceManager dient dazu Daten wie Bilder zu laden und zu verwalten. Da der
 * ResourceManager auch das doppelte Laden von Daten verhindert und daher die bereits
 * geladenen Daten cacht, darf es nur genau eine Instanz von ihm geben. Dies wird
 * sichergestellt indem der ResourceManager als Singleton implementiert wird. 
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class ResourceManager {

	/** Referenz auf die einzige vorhandene Instanz */
	private static ResourceManager instance = null;

	/** Map mit bereits geladenen Images */
	private HashMap<String, Image> images = null;
	
	/** Map mit geladenen Properties */
	private HashMap<String, Properties> properties = null;
	
	/** Map mit geladenen MapData Objekten */
	private HashMap<String, MapData> mapData = null;
	
	/** Das DummyBild */
	private BufferedImage dummyImage = null;
	
	/** Die große Schriftart (für wichtige Bildschirmmeldung hauptsächlich) */
	private Font largeFont = null;
	
	/** Die kleine Schriftart */
	private Font smallFont = null;
	
	/** Die Schriftart für Menüs */
	private Font menuFont = null;
	
	/** Die Schriftart für die Punkteanzeige */
	private Font scoreFont = null;
	
	/**
	 * Konstruktor - privat damit niemand sonst eine Instanz erzeugen kann. 
	 * @throws ResourceManagerException 
	 */
	private ResourceManager() {
		images = new HashMap<String, Image>();
		properties = new HashMap<String, Properties>();
		mapData = new HashMap<String, MapData>();
	}

	/**
	 * Liefert die einzige Instanz des ResourceManagers zurück.
	 * 
	 * @return Referenz auf den einzigen vorhandenen ResourceManager
	 */
	public static synchronized ResourceManager getInstance() {
		if ( instance == null ) {
			instance = new ResourceManager();
		}
		
		return instance;
	}
	
	/**
	 * Liest aus der angegebenen Datei die Properties.
	 */
	public Properties getProperties(String name) throws ResourceManagerException {
		
		// Im Cache nach bereits geladenen Properties suchen
		if ( properties.containsKey(name) ) {
			return properties.get(name);
		}
		
		Properties prop = new Properties();

		ClassLoader loader = ResourceManager.class.getClassLoader();
		InputStream inStream = loader.getResourceAsStream(name);
		if ( inStream==null ) {
			throw new ResourceManagerException("Die Properties-Datei "+name+" konnte nicht gefunden werden!");
		}
		
		try {
			prop.load(inStream);
		} catch (IOException e) {
			throw new ResourceManagerException("Die Properties-Datei "+name+" konnte nicht geladen werden!", e);
		}
		
		return prop;
	}
	
	/**
	 * Liest das Bild mit dem angegebenen Dateinamen ein oder gibt es direkt zurück falls es
	 * bereits zuvor geladen worden ist.
	 * 
	 * Die Methode gibt in jedem Fall ein Bild zurück. Falls ein Ladefehler auftreten sollte
	 * wird eine ResourceManagerException geworfen.
	 * 
	 * @param name der Dateiname des Bildes - relativ zum Klassenpfad
	 * @return das Bildobjekt
	 * @throws ResourceManagerException 
	 */
	public Image getImage(String name) { // throws ResourceManagerException {
		
		// Im Cache nach dem Bild sehen und falls es dies dort gibt direkt zurückgeben
		if ( images.containsKey(name) ) {
			return images.get(name);
		}
		
		ClassLoader loader = ResourceManager.class.getClassLoader();
		URL imageUrl = loader.getResource(name);
		if ( imageUrl == null ) {
			return getDummyImage();
			//throw new ResourceManagerException("Die Bilddatei "+name+" konnte nicht gefunden werden!");
		}

		Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);
		if ( image == null ) {
			return getDummyImage();
//			throw new ResourceManagerException("Die Bilddatei "+name+" konnte nicht gelesen werden!");
		}
		
		MediaTracker mediaTracker = new MediaTracker(new JPanel());
		mediaTracker.addImage(image, 0);
		try
		{
			mediaTracker.waitForID(0);
		}
		catch (InterruptedException ie)
		{
			return getDummyImage();
//			throw new ResourceManagerException("Das Bild "+name+" konnte nicht vollständig geladen werden.", ie);
		}

		// Das Bild ist neu und wurde erfolgreich geladen: In die Map eintragen
		images.put(name, image);
		
		return image;
	}
	
	/**
	 * Liest Mapdaten aus einer map-Datei.
	 * 
	 * @param name Der Name der zu ladenden Map.
	 * @throws ResourceManagerException 
	 */
	public MapData getMapData(String name) throws ResourceManagerException {
		// Im Cache nach dem Bild sehen und falls es dies dort gibt direkt zurückgeben
		if ( mapData.containsKey(name) ) {
			return mapData.get(name);
		}
		
		ClassLoader loader = ResourceManager.class.getClassLoader();
		InputStream inStream = loader.getResourceAsStream(name);
		if ( inStream==null ) {
			throw new ResourceManagerException("Fehler beim Lesen der Map "+name);
		}
		
		MapData map;
		try {
			map = new MapData(inStream);
		} catch (MapDataException e) {
			throw new ResourceManagerException("Fehler beim Lesen der Map "+name,e);
		}

		mapData.put(name, map);
		
		return map;
		
	}

	/**
	 * Erzeugt eine LevelList indem die Levels eingeladen werden.
	 * 
	 * @return die erzeugte LevelListe
	 * @throws ResourceManagerException 
	 * @throws IOException 
	 */
	public LevelList getLevelList() throws ResourceManagerException {
		ClassLoader loader = ResourceManager.class.getClassLoader();
		InputStream inStream = loader.getResourceAsStream("levels.list");
		if ( inStream==null ) {
			throw new ResourceManagerException("Fehler beim Lesen der Datei 'levels.list'");
		}
		
		try {
			return new LevelList(inStream);
		} catch (IOException e) {
			throw new ResourceManagerException("Fehler beim Laden der LevelList 'levels.list'", e);
		}
	}
	
	/**
	 * Erzeugt ein DemoPlayback-Objekt.
	 * 
	 * @param name der Dateiname des zu ladenden DemoPlayback-Objekts
	 * @throws ResourceManagerException 
	 */
	public DemoPlayback getDemoPlayback(String name) throws ResourceManagerException {
		FileInputStream inStream;
		File file = new File(name);
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			throw new ResourceManagerException("Fehler beim Lesen der Datei '"+name+"'", e1);
		}
		
		try {
			return new DemoPlayback(inStream);
		} catch (IOException e) {
			throw new ResourceManagerException("Fehler beim Lesen der Datei '"+name+"'", e);
		}
	}

	/**
	 * Liefert (und erzeugt falls nötig) ein Bild das als Platzhalter 
	 * für nicht vorhandene Bilder verwendet wird.
	 * 
	 * Falls das Lesen eines Bildes fehlschlägt wird dieses zurückgegeben.
	 * 
	 * @return das Dummybild
	 */
	public Image getDummyImage() {
		if ( dummyImage==null ) {
			dummyImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
			
			Graphics2D g = (Graphics2D)dummyImage.getGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,32,32);
			g.setColor(Color.yellow);
			g.setFont(ResourceManager.getInstance().getSmallFont());
			
			FontRenderContext context = g.getFontMetrics().getFontRenderContext();
			TextLayout textLayout = new TextLayout("?", g.getFont(), context);
			Rectangle textRect = textLayout.getPixelBounds(context, 0, 0);
			
			// Den gesamten Bereich des Bildes mit Fragezeichen füllen
			for ( int x=-textRect.width/2 ; x<40 ; x+=(textRect.width+1) ) {
				for ( int y=-textRect.height/2 ; y<40 ; y+=(textRect.height+1) ) {
					g.drawString("?", x, y);
				}
			}
		}
		
		return dummyImage;
	}

	/**
	 * Liefert das Font-Objekt für große Schriften.
	 * 
	 * @return das Font-Objekt für große Schriften
	 */
	public Font getLargeFont() {
		if ( largeFont==null ) {
			largeFont = new Font("Sans Serif", Font.BOLD, 34);
		}
		
		return largeFont;
	}

	/**
	 * Liefert ein Font-Objekt für kleine Schriften.
	 * 
	 * @return das Font-Objekt für kleine Schriften.
	 */
	public Font getSmallFont() {
		if ( smallFont==null ) {
			smallFont = new Font("Sans Serif", Font.PLAIN, 12);
		}
		return smallFont;
	}

	/**
	 * Liefert das Font-Objekt für die Menüs.
	 * 
	 * @return das Font-Objekt für die Menüs.
	 */
	public Font getMenuFont() {
		if ( menuFont==null ) {
			menuFont = new Font("Sans Serif", Font.PLAIN, 18);
		}
		return menuFont;
	}

	/**
	 * Liefert das Font-Objekt für die Punkteanzeige.
	 * 
	 * @return das Font-Objekt für die Punkteanzeige
	 */
	public Font getScoreFont() {
		if ( scoreFont==null ) {
			scoreFont = new Font("Sans Serif", Font.PLAIN, 18);
		}
		return scoreFont;
	}
}
