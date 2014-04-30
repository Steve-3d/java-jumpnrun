package de.gaffga.jumpnrun.menu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import de.gaffga.jumpnrun.FontTools;
import de.gaffga.jumpnrun.controller.IGameController;
import de.gaffga.jumpnrun.game.GameStateException;
import de.gaffga.jumpnrun.resources.ResourceManager;

/**
 * Ein einzelnes Menu das Teil eines Menusystems ist.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Menu {

	/** Die Id des Menus */
	private int id = -1;
	
	/** Die Überschrift */
	private String caption = "";
	
	/** Unser MenuSystem */
	private MenuSystem menuSystem = null;
	
	/** Die Liste aller Menüeinträge */
	private ArrayList<MenuEntry> entries = null;
	
	/** Der aktuell markierte Eintrag */
	private int currentEntry = 0;
	
	/** Der Zeilenabstand */
	private int lineSpacing = 0;
	
	/** Die maximale Höhe des Texts */
	private int textHeight = 0;
	
	/**
	 * Konstruktor.
	 *
	 * @param system Referenz auf das MenuSystem zu dem wir gehören
	 * @param id die Id des Menus
	 * @param caption die Überschrift
	 */
	public Menu(MenuSystem system, int id, String caption) {
		this.menuSystem = system;
		this.id = id;
		this.caption = caption;
		entries = new ArrayList<MenuEntry>();
	}
	
	/**
	 * Liefert die Anzahl an Menüeinträgen.
	 * 
	 * @return die Anzahl Einträge dieses Menüs
	 */
	public int getNumEntries() {
		return entries.size();
	}
	
	/**
	 * Fügt einen neuen Menüeintrag hinzu.
	 */
	public void addEntry(MenuEntry entry) {
		entries.add(entry);
	}

	public String getCaption() {
		return caption;
	}

	public int getId() {
		return id;
	}

	public void step() {
		IGameController input = menuSystem.getInput();
		
		if ( input.wasKeyHit(KeyEvent.VK_RIGHT)) {
			MenuEntry entry = entries.get(currentEntry);
			if ( entry.isRange() ) {
				int val = (Integer)menuSystem.getMap().get(entry.getPropertyName());
				if ( val < entry.getRangeBis() ) {
					val++;
				}
				menuSystem.getMap().put(entry.getPropertyName(), val);
			}
		} else if ( input.wasKeyHit(KeyEvent.VK_LEFT)) {
			MenuEntry entry = entries.get(currentEntry);
			if ( entry.isRange() ) {
				int val = (Integer)menuSystem.getMap().get(entry.getPropertyName());
				if ( val > entry.getRangeVon() ) {
					val--;
				}
				menuSystem.getMap().put(entry.getPropertyName(), val);
			}
		}
	}
	
	/**
	 * Zeichnet das Menü.
	 * 
	 * @param graphics der Graphics-Kontext mit dem gezeichnet wird.
	 */
	public void paint(Graphics graphics) {
		
		Graphics2D graphics2d = (Graphics2D)graphics;

		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		if ( lineSpacing == 0 ) {			
			Rectangle rect = FontTools.getBoundingRect(graphics2d,ResourceManager.getInstance().getMenuFont(),"AqygX");
			
			textHeight = rect.height;
			lineSpacing = rect.height + (int)(rect.height * 0.5f);
		}
		
		int x = 70;
		int y = 70+lineSpacing;
		
		graphics2d.setFont(ResourceManager.getInstance().getMenuFont());
		
		graphics2d.setColor(Color.black);
		graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		graphics2d.setColor(Color.white);
		//g.setPaint(new GradientPaint(0,60,Color.black,0,menuSystem.getHeight()-90,Color.blue));
		graphics2d.fillRect(50, 60, menuSystem.getWidth()-100, menuSystem.getHeight()-90);
		graphics2d.setPaint(null);
		
		graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		graphics2d.setColor(Color.yellow);
		FontTools.renderHorizontalCentered(graphics2d, menuSystem.getWidth()/2, y, getCaption());
		y += lineSpacing*2;
		
		for ( int i=0 ; i<entries.size() ; i++ ) {
			MenuEntry entry = entries.get(i);
			
			if ( i == currentEntry ) {
				graphics2d.setColor(Color.red);
				graphics2d.fillRect(x-5, y-textHeight, 500, lineSpacing);
			}
			
			graphics2d.setColor(Color.DARK_GRAY);
			graphics2d.drawString(entry.getText(), x+2, y+2);
			graphics2d.setColor(Color.white);
			graphics2d.drawString(entry.getText(), x, y);
			
			HashMap<String,Object> map = menuSystem.getMap();
			if ( entry.isCheckbox() ) {
				if ( !map.containsKey(entry.getPropertyName())) {
					map.put(entry.getPropertyName(), false);
				}
				boolean b = (Boolean)map.get(entry.getPropertyName());
				
				if ( b ) {
					graphics2d.drawString("JA", 400, y);
				} else {
					graphics2d.drawString("NEIN", 400, y);
				}
			} else if ( entry.isRadiobutton() ) {
				if ( !map.containsKey(entry.getPropertyName())) {
					map.put(entry.getPropertyName(), entry.getValue());
				}
				
				int val = (Integer)map.get(entry.getPropertyName());
				
				int n = textHeight;
				int q = 3;
				int m = n-q;
				int o = n-2*q;
				if ( val == entry.getValue() ) {
					graphics2d.drawArc(400, y-n+q, n-1, n-1, 0, 360);
					graphics2d.fillArc(400+q, y-m+q, o, o, 0, 360);
				} else {
					graphics2d.drawArc(400, y-n+q, n-1, n-1, 0, 360);
				}
			} else if ( entry.isRange() ) {
				if ( !map.containsKey(entry.getPropertyName())) {
					map.put(entry.getPropertyName(), entry.getRangeVon());
				}
				
				int val = (Integer)map.get(entry.getPropertyName());
				graphics2d.drawString(""+val,400, y);
			}
			
			y+=lineSpacing;
		}
		
		graphics2d.setFont(ResourceManager.getInstance().getSmallFont());
		graphics2d.setColor(Color.white);
		graphics2d.drawString("\"Java JumpN Run\" Stefan Gaffga 2007", 10, menuSystem.getHeight()-10);
	}
	
	/**
	 * Bewegt die Markierung zum nächsten Eintrag.
	 */
	public void next() {
		int current = currentEntry;
		
		current = (current + 1) % entries.size();
		while ( current != currentEntry && !entries.get(current).isSelectable() ) {
			current = (current + 1) % entries.size();
		}
		
		currentEntry = current;
	}
	
	/**
	 * Bewegt die Markierung zum vorigen Eintrag.
	 */
	public void previous() {
		int current = currentEntry;
		
		current = (current + entries.size()-1) % entries.size();
		while ( current != currentEntry && !entries.get(current).isSelectable() ) {
			current = (current + entries.size()-1) % entries.size();
		}
		
		currentEntry = current;
	}
	
	/**
	 * Führt den aktuellen Eintrag aus.
	 * @throws MenuException 
	 * @throws GameStateException 
	 */
	public void executeCurrent() throws MenuException {
		MenuEntry entry = entries.get(currentEntry);
		
		if ( entry.isCheckbox() ) {
			HashMap<String, Object> map = menuSystem.getMap();
			boolean b = (Boolean)map.get(entry.getPropertyName());
			b = !b;
			map.put(entry.getPropertyName(), b);
		} else if ( entry.isRadiobutton() ) {
			HashMap<String, Object> map = menuSystem.getMap();
			map.put(entry.getPropertyName(), entry.getValue());
		} else {
			if ( entry.isMenuVerweis() ) {
				menuSystem.switchMenu(Integer.parseInt(entry.getCommand()));
			} else {
				menuSystem.fireActionPerformed(entry.getCommand());
			}
		}
	}

	public void setCurrentEntry(int i) {
		currentEntry = entries.size()-1;
		next();
	}
}
