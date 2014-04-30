package de.gaffga.jumpnrun.effects;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import de.gaffga.jumpnrun.effects.imagemovestrategies.IImageMoveStrategy;
import de.gaffga.jumpnrun.map.Map;

/**
 * Klasse die den Effekt eines bewegten Bildes implementiert.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class ImageMoveEffect implements IEffect {

	/** Das zu bewegende Bild */
	private Image image = null;
	
	/** Die Strategie nach der das Bild bewegt wird */
	private IImageMoveStrategy moveStrategy = null;
	
	/** Die aktuelle Position */
	private Point currentPosition = null;
	
	/** Die Map auf der der Effekt abgespielt werden soll */
	private Map map = null;
	
	/**
	 * Konstruktor.
	 * 
	 * @param img das Bild das bewegt werden soll
	 * @param moveStrategy die Bewegungsstrategie des Bildes
	 */
	public ImageMoveEffect(Image img, Map map, IImageMoveStrategy moveStrategy) {
		this.image = img;
		this.moveStrategy = moveStrategy;
		this.map = map;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public boolean isFinished() {
		return moveStrategy.isFinished();
	}

	/** 
	 * {@inheritDoc}
	 */
	public void start() {
		moveStrategy.start();
		currentPosition = moveStrategy.getCurrentPosition();
	}

	/** 
	 * {@inheritDoc}
	 */
	public void step() {
		moveStrategy.step();
		currentPosition = moveStrategy.getCurrentPosition();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void draw(Image image) {
		Graphics g = image.getGraphics();
		Point screenP = map.getPosScreenPixelFromPosMapPixel(currentPosition);
		// Da beim Zeichnen der Bilder die linke obere und nicht die linke _untere_ Kante zählt (welche wir ja
		// hier haben) - muss noch die Höhe des Bildes von der Y-Koordinate abgezogen werden.
		g.drawImage(this.image, screenP.x, screenP.y - this.image.getHeight(null), null);
	}
}
