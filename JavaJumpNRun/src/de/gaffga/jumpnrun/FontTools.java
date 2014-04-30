package de.gaffga.jumpnrun;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

public class FontTools {

	/**
	 * Berechnet das Rechteck das den angegebenen Text in der angegebenen Schriftart
	 * umrandet. Das Rechteck hat die Koordinaten die passen wenn der Text an die
	 * Position (0,0) gerendert würde.
	 * 
	 * @param graphics der Graphics-Kontext
	 * @param font die zu verwendende Schriftart
	 * @param text der zu messende Text
	 * @return der rechteckige Bereich der den Text umramt
	 */
	public static Rectangle getBoundingRect(Graphics graphics, Font font, String text) {
		FontRenderContext frc = graphics.getFontMetrics().getFontRenderContext();
		TextLayout tl = new TextLayout(text, font, frc);
		return tl.getPixelBounds(frc, 0, 0);
	}
	
	/**
	 * Gibt einen Text horizontal zentriert aus.
	 */
	public static void renderHorizontalCentered(Graphics graphics, int x, int y, String text) {
		Rectangle rect = getBoundingRect(graphics, graphics.getFont(), text);
		
		graphics.drawString(text, x-rect.width/2, y);
	}
}
