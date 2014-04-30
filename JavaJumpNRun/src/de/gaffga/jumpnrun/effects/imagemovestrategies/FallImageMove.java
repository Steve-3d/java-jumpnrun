package de.gaffga.jumpnrun.effects.imagemovestrategies;

import java.awt.Point;

import de.gaffga.jumpnrun.FloatPosition;
import de.gaffga.jumpnrun.FpsMeter;

/**
 * Strategie für den ImageMover der ein fallendes Image erzeugt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 *
 */
public class FallImageMove implements IImageMoveStrategy {

	/** Die aktuelle Position */
	private FloatPosition currentPosition = null;
	
	/** Die Startposition */
	private FloatPosition startPosition = null;
	
	/** Die aktuelle Bewegungsrichtung X */
	private float velX;
	
	/** Die aktuelle Bewegungsrichtung Y */
	private float velY;
	
	/** Flag ob wir fertig sind. */
	private boolean finished;
	
	/** Y-Koordinate ab der das Bild als "herausgefallen" gilt */
	private float destinationY;
	
	/**
	 * Konstruktor.
	 *
	 * @param start Die Start-Koordinaten der Bewegung in MapPixeln.
	 */
	public FallImageMove(Point start, float destinationY) {
		startPosition = new FloatPosition(start.x,start.y);
		currentPosition = startPosition;
		this.destinationY = destinationY;
	}
	
	public Point getCurrentPosition() {
		return new Point((int)currentPosition.x, (int)currentPosition.y);
	}

	public boolean isFinished() {
		return finished;
	}

	public void start() {
		currentPosition = startPosition;
		velX = (float) (200.0f * Math.random() - 100.0f);
		velY = 400.0f;
		finished = false;
	}

	public void step() {
		currentPosition.x += FpsMeter.getInstance().getSpeedFactor() * velX;
		currentPosition.y += FpsMeter.getInstance().getSpeedFactor() * velY;
		
		velY -= 1500.0f * FpsMeter.getInstance().getSpeedFactor();
		
		if ( currentPosition.y <= destinationY ) {
			finished=true;
		}
	}
}
