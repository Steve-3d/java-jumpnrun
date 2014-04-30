package de.gaffga.jumpnrun.effects.imagemovestrategies;

import java.awt.Point;

import de.gaffga.jumpnrun.FpsMeter;


/**
 * Zeigt ein Bild für eine gewisse Zeit über einer Position an.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class StaticImageMove implements IImageMoveStrategy {

	/** Die aktuelle Position */
	private Point currentPosition = null;
	
	/** Die summierte Zeit */
	private long time;
	
	/** Die Endzeit */
	private long endTime;

	/**
	 * Konstruktor.
	 * 
	 * @param time die Anzahl an Millisekunden die das Bild gezeigt werden soll
	 * @param pos die Position an der das Bild stehen soll
	 */
	public StaticImageMove(int time, Point pos) {
		this.endTime = time;
		this.currentPosition = pos;
	}
	
	public Point getCurrentPosition() {
		return currentPosition;
	}

	public boolean isFinished() {
		return (endTime < time);
	}

	public void start() {
		time = 0;
	}

	public void step() {
		time += (1000.0 * FpsMeter.getInstance().getSpeedFactor());
	}
}
