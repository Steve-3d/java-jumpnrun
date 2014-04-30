package de.gaffga.jumpnrun.effects;

import java.awt.Image;
import java.awt.Point;

import de.gaffga.jumpnrun.effects.imagemovestrategies.IImageMoveStrategy;
import de.gaffga.jumpnrun.sprites.Sprite;

/**
 * Effect der ein Sprite anhand einer Bewegungsstrategie bewegt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class SpriteMoveEffect implements IEffect {

	/** Das zu bewegende Sprite */
	private Sprite sprite = null;
	
	/** Die Strategie nach der das Bild bewegt wird */
	private IImageMoveStrategy moveStrategy = null;
	
	/** Die aktuelle Position */
	private Point currentPosition = null;
	
	/**
	 * Konstruktor.
	 * 
	 * @param sprite das Bild das bewegt werden soll
	 */
	public SpriteMoveEffect(Sprite sprite, IImageMoveStrategy moveStrategy) {
		this.sprite = sprite;
		this.moveStrategy = moveStrategy;
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
		sprite.setPosition(currentPosition);
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void draw(Image image) {
		// Da Sprites durch den SpriteManager gezeichnet werden haben wir hier
		// nichts zu tun
	}

}
