package de.gaffga.jumpnrun;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Messen der FPS (frames per second) um eine einheitliche Spielgeschwindigkeit
 * auf allen System zu erreichen.
 * 
 * Diese Klasse ist die einzige im gesamten Spiel die <strong>nicht</strong> die 
 * Timer-Klasse für Zeitmessungen benutzt. Der Grund hierfür ist der, dass die FPS
 * etwas "träge" sind und durch den Einsatz der Pause-Funktion sich ein "hakeln"
 * im Spiel bemerkbar machen würde.
 * 
 * Diese Klasse ist als Singleton implementiert.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class FpsMeter {

	/** Zeitpunkt der letzten Messung */
	private long lastTime;
	
	/** Aktueller FPS-Messwert */
	private float fps;
	
	/** Der Fps-Graph zur Anzeige wie stark die FPS schwanken */
	private Image fpsGraph = null;
	
	/** Zeitpunkt zu dem der letzte Meßwert in den FPS-Graphen gezeichnet wurde */
	private long lastGraph;
	
	/** Letzte Y-Koordinate im Graphen */
	private int lasty;
	
	/** Die Breite des Y-Achsen Beschriftungsbereiches */
	private int beschriftungYAchseWidth;
	
	/** Die Höhe der Überschriftszeile */
	private int captionHeight;
	
	/** Die einzige FpsMeter-Instanz */
	private static FpsMeter instance = null;
	
	/**
	 * Erzeugt ein neues FpsMeter-Objekt.
	 */
	private FpsMeter() {
		lastTime = System.nanoTime();
		lastGraph = System.currentTimeMillis();
		beschriftungYAchseWidth = 20;
		captionHeight = 10;
		lasty = -1;
		fps = 20; // irgendein halbwegs realistischer Wert (wichtig dass er >0 ist)
		fpsGraph = new BufferedImage(200, 60, BufferedImage.TYPE_INT_ARGB);

		// Den Graphen löschen
		Graphics2D g = (Graphics2D) fpsGraph.getGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.setColor(Color.black);
		g.fillRect(0,0,200,50+captionHeight);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

		// Die Beschriftung der Y-Achse zeichnen
		g.setColor(Color.black);
		g.fillRect(0,captionHeight,beschriftungYAchseWidth,50);
		
		// Die Überschrift über alles
		g.setColor(Color.yellow);
		String caption = "FPSMeter";
		g.drawChars(caption.toCharArray(), 0, caption.length(), 0, 10);
		
		// Die Marke "30 FPS"
		g.setColor(Color.green);
		String t1 = "30";
		g.drawChars(t1.toCharArray(), 0, t1.length(), 0, 39+captionHeight);

		// Die Marke "60 FPS"
		g.setColor(Color.white);
		String t2 = "60";
		g.drawChars(t2.toCharArray(), 0, t2.length(), 0, 23+captionHeight);
		
		// Die Linien
		g.setColor(Color.gray);
		g.drawLine(beschriftungYAchseWidth, 34+captionHeight, 199, 34+captionHeight);
		g.drawLine(beschriftungYAchseWidth, 19+captionHeight, 199, 19+captionHeight);
		g.drawLine(beschriftungYAchseWidth-1,captionHeight,beschriftungYAchseWidth-1,49+captionHeight);
	}
	
	/**
	 * Liefert die einzige FpsMeter-Instanz.
	 * 
	 * @return die Singleton-Instanz 
	 */
	public static FpsMeter getInstance() {
		if ( instance==null ) {
			instance = new FpsMeter();
		}
		
		return instance;
	}
	
	/**
	 * Info an uns das ein neues Frame gerendert wurde.
	 */
	public void notifyNewFrame() {
		long jetzt = System.nanoTime();

		float frameTime_ms = (jetzt-lastTime) / 1e6f;
		float curFps = 1000.0f / frameTime_ms;
		
		// Die Frames per Second sind ein gleitender Mittelwert
		fps = (99.0f*fps + curFps) / 100.0f;
		
		lastTime = jetzt;
	}
	
	/**
	 * Liefert den fpsGraphen.
	 * 
	 * @return das Bild mit dem Graphen als Inhalt
	 */
	public Image getGraph() {
		long jetzt = System.currentTimeMillis();
		
		// Soviele Pixel wird der Graph nach jeder Messung nach links verschoben
		int dy = 4;
		
		if ( jetzt-lastGraph > 500 ) {
			// Jede halbe Sekunde einen Messwert in den Graphen zeichnen
			lastGraph = jetzt;
			
			// Den Graphen nach links rücken
			Graphics2D g = (Graphics2D) fpsGraph.getGraphics();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g.copyArea(beschriftungYAchseWidth+dy,captionHeight,200-beschriftungYAchseWidth,50,-dy,0);
			int y = 49 - (int)fps/2;
			if ( y<=0 ) y=0;
			if ( y>=49 ) y=49;
			
			y+=captionHeight;

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

			// Das freigewordene Stück löschen
			g.setColor(Color.black);
			g.fillRect(199-dy+1, 0+captionHeight, 199,49);

			// Die Y-Hilfsachsen in das neue Stück zeichnen
			g.setColor(Color.gray);
			g.drawLine(199-dy+1, 34+captionHeight, 199, 34+captionHeight);
			g.drawLine(199-dy+1, 19+captionHeight, 199, 19+captionHeight);

			g.setColor(Color.black);
			g.fillRect(100, 0, 100, 20);
			g.setColor(Color.yellow);
			String fpsValue = "FPS: " + fps;			
			g.drawChars(fpsValue.toCharArray(), 0, fpsValue.length(), 130, 10);

			if ( fps < 30 ) {
				g.setColor(Color.red);
			} else if ( fps < 60 ) {
				g.setColor(Color.green);
			} else {
				g.setColor(Color.white);
			}
			if ( lasty>=0 ) {
				g.drawLine(199-dy, lasty, 199,y);
			}
			lasty=y;
		}
		
		return fpsGraph;
	}
	
	/**
	 * Liefert den aktuellen Messwert
	 */
	public float getFps() {
		return fps;
	}
	
	/**
	 * Liefert einen Faktor der dafür verwendet werden kann das Spiel in 
	 * eine konstante Geschwindigkeit zu bringen.
	 */
	public float getSpeedFactor() {
		return 1.0f / (float)fps;
	}
	
	/**
	 * Liefert einen Schätzwert wieviel Sekunden seit dem letzten Frame
	 * vergangen sind. Eine genaue Messung würde die Genauigkeit des
	 * Zeitgebers überfordern.
	 * 
	 * @return die Sekunden seit dem letzten Frame
	 */
	public float getLastFrameTime() {
		return 1.0f / (float)fps;
	}
}
