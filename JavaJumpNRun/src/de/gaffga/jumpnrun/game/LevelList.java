package de.gaffga.jumpnrun.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Speicherort für alle Levels und die Reihenfolge in der diese
 * gespielt werden.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class LevelList {

	/** Liste mit den Level-Dateinamen */
	private ArrayList<String> levelList = null;
	
	/**
	 * Konstruktor.
	 */
	public LevelList() {
		levelList = new ArrayList<String>();
	}
	
	/**
	 * Erzeugt eine neue LevelList aus einem InputStream heraus.
	 * @throws IOException 
	 */
	public LevelList(InputStream stream) throws IOException {
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);

		levelList = new ArrayList<String>();
		
		String line = null;
		while ( (line=br.readLine()) != null ) {
			if ( !line.trim().equals("") ) {
				if ( !line.trim().substring(0,1).equals("#")) {
					add(line);
				}
			}
		}
	}
	
	/**
	 * Fügt einen neuen Level hinzu.
	 * 
	 * @param name der Dateiname des Levels
	 */
	public void add(String name) {
		levelList.add(name);
	}
	
	/**
	 * Liefert die Anzahl an Levels.
	 * 
	 * @return die Levelanzahl
	 */
	public int getLevelCount() {
		return levelList.size();
	}
	
	/**
	 * Liefert einen bestimmten Levelnamen.
	 * 
	 * @param index der Index (ab 0)
	 * @return der Name des gewünschten Levels
	 */
	public String get(int index) {
		return levelList.get(index % levelList.size());
	}
}
