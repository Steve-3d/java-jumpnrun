package de.gaffga.jumpnrun;

import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import de.gaffga.jumpnrun.controller.IGameController;
import de.gaffga.jumpnrun.controller.KeyboardInput;
import de.gaffga.jumpnrun.game.Game;
import de.gaffga.jumpnrun.game.GameException;
import de.gaffga.jumpnrun.resources.ResourceManagerException;

/**
 * Das Hauptfenster des Spiels.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class MainFrame extends JFrame implements WindowListener {

	/** serialID */
	private static final long serialVersionUID = 1L;
	
	/** Das Objekt das die Tastatureinaben entgegennimmt */
	private IGameController input = null;
	
	/** Das Spielobjekt */
	private Game game = null;
	
	/**
	 * Konstruktor.
	 */
	public MainFrame() {
		super(I18n.getString("frame_title"));

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(640,480);
		setVisible(true);
		setResizable(false);
		addWindowListener(this);
		
		input = new KeyboardInput();
		addKeyListener(input);
//		((KeyboardInput)input).setRecording(true);
		
/*
		try {
			DemoPlayback dp = ResourceManager.getInstance().getDemoPlayback("test.demo");
			input = new KeyboardInputAdapter(dp);
			dp.start();
		} catch (ResourceManagerException e1) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Fehler beim Erzeugen der DemoPlayback-Klasse", e1);
			// Fenster wieder schließen
			dispose();
		}
	*/	
		
		try {
			game = new Game(input, getContentPane());
		} catch (GameException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Fehler beim Erzeugen der Game-Klasse", e);
			// Fenster wieder schließen
			dispose();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Fehler beim Erzeugen der Game-Klasse", e);
			// Fenster wieder schließen
			dispose();
		} catch (ResourceManagerException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Fehler beim Erzeugen der Game-Klasse", e);
			// Fenster wieder schließen
			dispose();
		}
	}
	
	/**
	 * Startet das gesamte Spiel.
	 * @throws GameException 
	 */
	public void start() {
		try {
			game.start();
			// Wenn das Spiel zu ende ist das Fenster schließen
			dispose();
		} catch (GameException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Fehler im Spiel",e);
		}
	}
	
	/**
	 * Überladen der paint()-Methode da das Zeichnen anders als gewöhnlich abläuft
	 */
	public void paint(Graphics g) {
		/* nichts zeichnen und vor allem den Hintergrund nicht löschen */
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		if ( input.isRecording() ) {
			try {
				input.saveRecordedEvents("test.demo");
			} catch (IOException e1) {
				System.err.println("Fehler beim Schreiben der Demo-Datei!");
				e1.printStackTrace();
			}
		}
		
		game.exit();
		dispose();
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}