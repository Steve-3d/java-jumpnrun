package de.gaffga.jumpnrun;

import java.awt.Point;

/**
 * 2D-Position mit float-Typen.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class FloatPosition {

	/** Die X-Position */
	public float x;
	
	/** Die Y-Position */
	public float y;
	
	/**
	 * Konstruktor.
	 */
	public FloatPosition() {
		x=0.0f;
		y=0.0f;
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 */
	public FloatPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/** 
	 * Erzeugt eine neue FloatPosition aus einem Point-Objekt.
	 * 
	 * @param pos das Point-Objekt das zu konvertieren ist
	 */
	public FloatPosition(Point pos) {
		this.x = pos.x;
		this.y = pos.y;
	}
	
	/**
	 * Konstruktor.
	 * 
	 * @param currentPos die zu kopierende Position
	 */
	public FloatPosition(FloatPosition currentPos) {
		this.x = currentPos.x;
		this.y = currentPos.y;
	}
}
