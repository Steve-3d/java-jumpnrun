package de.gaffga.jumpnrun.score;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import de.gaffga.jumpnrun.I18n;
import de.gaffga.jumpnrun.resources.ResourceManager;
import de.gaffga.jumpnrun.resources.ResourceManagerException;

/**
 * Klasse die die aktuelle Punktzahlzeile anzeigt.
 * 
 * Aus Performance-Gründen wird die Punktzahl in einer Bitmap gecached.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class ScoreView implements IScoreObserver {

	/** Das Cache-Image */
	private Image image = null;
	
	/** Die Breite der Score-Zeile */
	private int width;
	
	/** Das Bild für die Anzeige der Restleben */
	private Image live = null;
	
	/**
	 * Konstruktor.
	 *
	 * @param comp auf dieser Componente soll die Punktzahl später gezeichnet werden können.
	 * @throws ResourceManagerException 
	 */
	public ScoreView(Component comp) throws ResourceManagerException {
		width = comp.getWidth();
		
		live = ResourceManager.getInstance().getImage("life.png");

		int h = live.getHeight(null)+12;
		
		image = new BufferedImage(width,h,BufferedImage.TYPE_INT_ARGB);
		
		// Initialisieren mit der 0-Punkte Score
		scoreChanged(new Score());
	}

	/**
	 * Liefert den String der die Punktzahl angibt.
	 * 
	 * @param score das Score-Objekt das analysiert wird
	 * @return der Score-Textstring
	 */
	protected String getScoreString(Score score) {
		String scoreText = I18n.getString("score") + " " + 
			String.format("%06d", ((Score)score).getScore() ) +
			"      " +
			I18n.getString("coins") + " " + 
			String.format("%02d", ((Score)score).getCoinsCollected()) +
			"      " +
			"Level " + String.format("%2d", ((Score)score).getLevel())
		;
		
		return scoreText;
	}
	
	/**
	 * Benachrichtung darüber, dass sich die Punkte geändert haben. Wir müssen
	 * daraufhin das gecachte Bild neu erzeugen.
	 * 
	 * @param scoreObs das observierte Score-Objekt das sich geändert hat
	 */
	public void scoreChanged(IScoreObservable scoreObs) {
		Score score = (Score)scoreObs;
		Graphics2D g = (Graphics2D)image.getGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Das Cache-Bild leeren
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.5f));
		g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));

		// Den neuen Punktetext hineinschreiben
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.setColor(Color.white);
		g.setFont(ResourceManager.getInstance().getScoreFont());
		String scoreText = getScoreString((Score)score);

		g.drawChars(scoreText.toCharArray(), 0, scoreText.length(), 15, image.getHeight(null)-4);

		for ( int i=0 ; i<score.getNumReserveLives() ; i++ ) {
			g.drawImage(live, width - (i+2)*(live.getWidth(null)+1), 10, null);
		}
	}
	
	/**
	 * Liefert das Image mit der Punktanzeige.
	 *
	 * @return das Image mit der Punktanzeige.
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Zeichnet die Punkte auf das angegebene Image.
	 * 
	 * @param img das Image auf das die Punkte gezeichnet werden sollen
	 */
	public void draw(Image img) {
		Graphics2D g = (Graphics2D)img.getGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.drawImage(this.image, 0, 0, null);
	}
}
