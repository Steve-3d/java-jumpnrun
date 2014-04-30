package de.gaffga.jumpnrun.menu;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import de.gaffga.jumpnrun.DecoratedText;
import de.gaffga.jumpnrun.controller.IGameController;
import de.gaffga.jumpnrun.resources.ResourceManagerException;

/**
 * Menu das mit der Tastatur bedient werden kann.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class MenuSystem {

	/** Das Objekt über das wir die Tastatureingaben erhalten */
	private IGameController input = null;

	/** Flag ob das Menu sichtbar ist */
	private boolean visible = false;
	
	/** Die Komponente auf der das Menü gezeichnet werden soll */
	private Component component = null;
	
	/** Liste mit allen vorhandenen Menus */
	private ArrayList<Menu> menus = null;
	
	/** Id des aktiven Menüs */
	private int currentMenu = 0;

	/** Die Breite der verfügbaren Fläche in Pixeln */
	private int width;
	
	/** Die Höhe der verfügbaren Fläche in Pixeln */
	private int height;
	
	/** Die Map enthält die Daten für Checkboxen und Radiobuttons */
	private HashMap<String, Object> map = null;

	/** Die Liste mit allen Listeners */
	private ArrayList<IMenuActionListener> listeners = null;
	
	/** Das Hintergrundbild des Menüs */
	private AnimatedBackground animatedBackground = null;

	/** Der Titel */
	private DecoratedText titleText = null;
	
	/** Soll ein Hintergrund gezeichnet werden? */
	private boolean drawBackground = true;
	
	/** Die Gesamtüberschrift */
	private String caption = "Java Jump'N Run";
	
	/**
	 * Konstruktor.
	 * 
	 * @param input Über dieses Objekt bekommen wir die Tastaturevents
	 * @param component auf dieser Komponente zeichnen wir das Menü
	 * @param map die Map mit den Einstellungswerten des Menüs
	 * @throws ResourceManagerException 
	 */
	public MenuSystem(IGameController input, Component component, HashMap<String, Object> map) throws ResourceManagerException {
		this.input  = input;
		this.visible = false;
		menus = new ArrayList<Menu>();
		listeners = new ArrayList<IMenuActionListener>();
		this.width = component.getWidth();
		this.height = component.getHeight();
		this.component = component;
		this.map = map;
		animatedBackground = new AnimatedBackground(width,height);
		titleText = new DecoratedText(component, caption);
	}
	
	/**
	 * Schaltet die Sichtbarkeit des Menus um.
	 * 
	 * @param visible true um das Menu anzeigen zu lassen, false um es zu verstecken
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * Liest eine Menüdefinition ein.
	 * @throws IOException 
	 */
	public void readMenu(InputStream is) throws IOException {
		Properties prop = new Properties();
		prop.load(is);
		
		readMenu(prop, 0);
	}
	
	/**
	 * Liest eine Menüdefinition ein.
	 * @throws IOException 
	 */
	public void readMenu(Properties prop) throws IOException {
		readMenu(prop, 0);
	}
	
	/**
	 * Liest aus dem Properties-Objekt das Menu mit der angegebenen Id ein.
	 * 
	 * @param prop
	 * @param menuId
	 */
	public void readMenu(Properties prop, int menuId) {
		
		if ( prop.containsKey("caption")) {
			caption = prop.getProperty("caption");
			titleText = new DecoratedText(component, caption);
		}
		
		ArrayList<Integer> verweise = new ArrayList<Integer>();
		String entryName = "menu_";
		String caption = prop.getProperty(entryName+menuId);
		int menuIndex = 0;
		Menu menu = new Menu(this, menuId, caption);
		String entry = prop.getProperty(entryName+menuId+"_"+menuIndex);
		while ( entry != null ) {
			MenuEntry menuEntry = new MenuEntry(entry);
			if ( menuEntry.isMenuVerweis() ) {
				int v = Integer.parseInt(menuEntry.getCommand());
				if ( !verweise.contains(v) ) verweise.add(v);
			}
			
			if ( menuEntry.isCheckbox() && !map.containsKey(menuEntry.getPropertyName())) {
				map.put(menuEntry.getPropertyName(), false);
			}
			
			if ( menuEntry.isRadiobutton() && !map.containsKey(menuEntry.getPropertyName())) {
				map.put(menuEntry.getPropertyName(), menuEntry.getValue());
			}
			
			menu.addEntry(menuEntry);
			menuIndex++;
			entry = prop.getProperty(entryName+menuId+"_"+menuIndex);
		}
		if ( menu.getNumEntries() > 0 ) {
			menus.add(menu);
		}
		
		for ( int v : verweise ) {
			if ( getMenuById(v)==null ) {
				readMenu(prop,v);
			}
		}
	}
	
	/**
	 * Liefert das Menu-Objekt anhand der Id.
	 * 
	 * @param id die Id des gesuchten Menus
	 * @return das Menu oder null falls keines gefunden wurde
	 */
	public Menu getMenuById(int id) {
		for ( Menu menu : menus ) {
			if ( menu.getId() == id ) {
				return menu;
			}
		}
		return null;
	}
	
	public void step() throws MenuException {
		if ( input.wasKeyHit(KeyEvent.VK_DOWN)) {
			getMenuById(currentMenu).next();
		} else if ( input.wasKeyHit(KeyEvent.VK_UP)) {
			getMenuById(currentMenu).previous();
		} else if ( input.wasKeyHit(KeyEvent.VK_ENTER)) {
			getMenuById(currentMenu).executeCurrent();
		} else {
			getMenuById(currentMenu).step();
		}
	}
	
	/**
	 * Zeichnet das aktive Menü falls es sichtbar ist.
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Menu menu = getMenuById(currentMenu);
		if ( menu != null && visible ) {
			if ( drawBackground ) {
				animatedBackground.paint(g2);
			}
			
			// Das Menu mit Transparenz zeichnen
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f));
			menu.paint(g2);
			
			titleText.paint(g2, width/2, 30);
		}
	}
	
	/**
	 * Ändert das Zeichnen des Hintergrunds.
	 * 
	 * @param zeichnen true falls der Hintergrund gezeichnet werden soll, sonst false
	 */
	public void setDrawBackground(boolean zeichnen) {
		drawBackground = zeichnen;
	}
	
	/**
	 * Liefert die HashMap.
	 */
	public HashMap<String, Object> getMap() {
		return map;
	}

	public void switchMenu(int i) {
		currentMenu = i;
		getMenuById(currentMenu).setCurrentEntry(0);
	}
	
	public int getWidth() {
		return width;
	}

	public void addActionListener(IMenuActionListener listener) {
		listeners.add(listener);
	}

	public void fireActionPerformed(String command) throws MenuException {
		for ( IMenuActionListener listener : listeners ) {
			listener.actionPerformed(command);
		}
	}
	
	public IGameController getInput() {
		return input;
	}

	public int getHeight() {
		return height;
	}
}
