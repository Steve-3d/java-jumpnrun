package de.gaffga.jumpnrun;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import de.gaffga.jumpnrun.resources.ResourceManager;

/**
 * Zeichnet dekorativen Text.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class DecoratedText {

	private Image image = null;
	
	/**
	 * Erzeugt einen dekorierten Schriftzug der danach mit der paint-Methode schnell auf ein
	 * Image gezeichnet werden kann.
	 * 
	 * @param dest auf diese Komponente soll der Schriftzug später gezeichnet werden
	 * @param text dieser Text soll ausgegeben werden
	 */
	public DecoratedText(Component dest, String text) {
		int abstand=6;
		Graphics2D g = (Graphics2D)dest.getGraphics();

		Rectangle rect = FontTools.getBoundingRect(g, ResourceManager.getInstance().getLargeFont(), text);
		rect.width += abstand*2;
		rect.height += abstand*2;
		
		image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphicsImage = (Graphics2D)image.getGraphics();
		graphicsImage.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,1.0f));
		graphicsImage.setColor(Color.black);
		graphicsImage.fillRect(0, 0, rect.width, rect.height);
		
		graphicsImage.setColor(Color.white);
		graphicsImage.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		
		TextLayout tl = new TextLayout(text, ResourceManager.getInstance().getLargeFont(), 
				g.getFontRenderContext());
		AffineTransform at = new AffineTransform();
		at.translate(abstand, rect.height-abstand);
		Shape clipShape = tl.getOutline(at);

		graphicsImage.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphicsImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphicsImage.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		graphicsImage.setColor(Color.BLUE);
		graphicsImage.setStroke(new BasicStroke(8.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		GradientPaint gradientTextBorder = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f,10.0f, Color.black, true);
		graphicsImage.setPaint(gradientTextBorder);
		graphicsImage.draw(clipShape);
		
		graphicsImage.setTransform(at);
		graphicsImage.setFont(ResourceManager.getInstance().getLargeFont());
		graphicsImage.setColor(Color.yellow);
		GradientPaint gradientText = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f,10.0f, Color.yellow, true);
		graphicsImage.setPaint(gradientText);
		graphicsImage.drawString(text, 0, 0);
	}
	
	/**
	 * Zeichnet den dekorierten Text zentriert um die angegebenen Koordinaten.
	 * 
	 * @param img auf dieses Image soll der Text gezeichnet werden
	 * @param x die X-Koordinate
	 * @param y die Y-Koordinate
	 */
	public void paint(Image img, int x, int y) {
		Graphics2D g = (Graphics2D)img.getGraphics();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		g.drawImage(image, x-image.getWidth(null)/2, y-image.getHeight(null)/2, null);
	}
	
	/**
	 * Zeichnet den dekorierten Text zentriert um die angegebenen Koordinaten.
	 * 
	 * @param g auf diesen Graphics-Kontext soll gezeichnet werdne
	 * @param x die X-Koordinate
	 * @param y die Y-Koordinate
	 */
	public void paint(Graphics2D g, int x, int y) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		g.drawImage(image, x-image.getWidth(null)/2, y-image.getHeight(null)/2, null);
	}
}

