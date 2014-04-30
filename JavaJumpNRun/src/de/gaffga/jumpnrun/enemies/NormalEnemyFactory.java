package de.gaffga.jumpnrun.enemies;

import java.awt.Point;

import de.gaffga.jumpnrun.sprites.Sprite;

/**
 * Dies ist die konkrete Fabrik für Feinde im Schwierigkeitsgrad
 * "normal".
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class NormalEnemyFactory extends AbstractEnemyFactory {

	/**
	 * Erzeugt einen neuen, normal schweren Feind.
	 * 
	 * @param level der aktuelle Level im Spiel
	 * @param pos die Startposition
	 * @param sprite das Sprite zur Darstellung
	 */
	@Override
	public Enemy createEnemy(int level, Point pos, Sprite sprite) {
		Enemy enemy = new Enemy(pos,sprite);
		
		float speed = 1.0f + level*0.1f;
		if ( speed > 3.0f ) speed=3.0f;
		
		enemy.setSpeed(speed);
		
		return enemy;
	}

}
