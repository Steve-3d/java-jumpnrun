package de.gaffga.jumpnrun.enemies;

import java.awt.Point;

import de.gaffga.jumpnrun.sprites.Sprite;

/**
 * Dies ist die konkrete Fabrik für Feinde im Schwierigkeitsgrad
 * "schwierig".
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class HardEnemyFactory extends AbstractEnemyFactory {

	/**
	 * Erzeugt einen neuen, schwierigen Feind.
	 * 
	 * @param level der aktuelle Level im Spiel
	 * @param pos die Startposition
	 * @param sprite das Sprite zur Darstellung
	 */
	@Override
	public Enemy createEnemy(int level, Point pos, Sprite sprite) {
		Enemy enemy = new Enemy(pos,sprite);
		
		enemy.setSpeed(3.0f);
		
		return enemy;
	}

}
