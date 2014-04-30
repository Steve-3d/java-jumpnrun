package de.gaffga.jumpnrun.enemies;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.gaffga.jumpnrun.map.Map;

/**
 * Verwalter für alle Enemies.
 * 
 * Es werden alle Feinde hier zentral gespeichert und über eine Methode können
 * alle Feinde mit einem mal weiterbewegt werden.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class EnemyManager implements Iterable<Enemy> {

	/** Liste aller Enemies */
	private List<Enemy> enemies = null;
	
	/**
	 * Konstruktor.
	 */
	public EnemyManager() {
		enemies = new LinkedList<Enemy>();
	}
	
	/** 
	 * Ein neues Enemy hinzufügen.
	 * 
	 * @param enemy das neue Enemy
	 */
	public void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}
	
	/**
	 * Führt einen Schritt aller Feinde aus.
	 */
	public void step(Map map) {
		for ( Enemy enemy : enemies ) {
			enemy.step(map);
		}
	}

	/**
	 * Iterator für die Enemies.
	 */
	public Iterator<Enemy> iterator() {
		return enemies.iterator();
	}

	/**
	 * Löscht alle Einträge.
	 */
	public void flush() {
		enemies.clear();
	}
}
