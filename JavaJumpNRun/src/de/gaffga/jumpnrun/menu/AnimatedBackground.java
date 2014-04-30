package de.gaffga.jumpnrun.menu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Rendert einen bewegten Hintergrund.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class AnimatedBackground {

	/** Die Breite des Bildes */
	private int width;
	
	/** Die Höhe des Bildes */
	private int height;
	
	/** Liste mit Rechtecken die sich bewegen */
	private ArrayList<Rectangle2D.Float> rects = null;
	
	/** Liste mit den Bewegungsrichtungen */
	private ArrayList<Point2D.Float> dirs = null;
	
	/**
	 * Erzeugt einen neuen animierten Hintergrund in der gewünschten Größe.
	 * 
	 * @param width
	 * @param height
	 */
	public AnimatedBackground(int width, int height) {
		this.width = width;
		this.height = height;
		rects = new ArrayList<Rectangle2D.Float>();
		dirs = new ArrayList<Point2D.Float>();
		
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		
		// 20 Rechtecke und ihre Bewegungsrichtung erstellen
		for ( int i=0 ; i<20 ; i++ ) {
			int w = 350;
			int h = 100;
			int x = Math.abs(rand.nextInt()) % (width+w) - w/2;
			int y = Math.abs(rand.nextInt()) % (height+h) - h/2;
			rects.add(new Rectangle2D.Float(x,y,w,h));
			
			float dx = rand.nextFloat() * 4.0f;
			float dy = rand.nextFloat() * 1.0f - 0.5f;
			dirs.add(new Point2D.Float(dx,dy));
		}
	}
	
	public void paint(Graphics2D g) {
		Color bottomColor = new Color(0,0,32);
		Color topColor = new Color(128,128,255);
		Color rectColor = new Color(255,255,255);

		GradientPaint gp = new GradientPaint(0,0, topColor, 0,height,bottomColor);
		g.setPaint(gp);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.fillRect(0,0,width,height);
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
		g.setColor(rectColor);
		for ( int i=0 ; i<rects.size() ; i++ ) {
			Rectangle2D.Float rect = rects.get(i);
			Point2D.Float dir = dirs.get(i);
			
			g.fillRect((int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
			rect.x+=dir.getX();
			rect.y+=dir.getY();
			
			if ( rect.x > width ) {
				rect.x = -rect.width;
			}
			if ( rect.x < -rect.width ) {
				rect.x = width;
			}
			if ( rect.y > height ) {
				rect.y = -rect.height;
			}
			if ( rect.y < -rect.height ) {
				rect.y = height;
			}
		}
	}
}
