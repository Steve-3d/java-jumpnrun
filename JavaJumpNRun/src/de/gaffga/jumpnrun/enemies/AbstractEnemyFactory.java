package de.gaffga.jumpnrun.enemies;

import java.awt.Point;

import de.gaffga.jumpnrun.sprites.Sprite;

/**
 * Die abstrakte Fabrik zum Erzeugen von Feinden.
 * 
 * Dies ist die Basisklasse für die konkreten Fabriken die Feinde
 * entsprechend des eingestellten Schwierigkeitslevels und 
 * Levels im Spiel erstellen.
 * 
 * Die Feinde werden in höheren Leveln schwieriger und je
 * nach gewählter Schwierigkeitsstufe sind sie zu Beginn schon
 * schwerer.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public abstract class AbstractEnemyFactory {

	/**
	 * Erzeugt ein neues Feind-Objekt gemäß dem angegebenen 
	 * Spiellevel.
	 * 
	 * @param level der aktuelle Level
	 * @param pos die Startposition des Feindes
	 * @param sprite das zu verwendende Sprite zur Darstellung
	 */
	public abstract Enemy createEnemy(int level, Point pos, Sprite sprite);
}
