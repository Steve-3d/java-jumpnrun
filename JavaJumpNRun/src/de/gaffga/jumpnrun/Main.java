package de.gaffga.jumpnrun;


/**
 * Die Main-Klasse - enthält ledglich die Main-Methode.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Main {

	/**
	 * Die Main-Methode des Spiels.
	 * 
	 * Das Hauptfenster wird erzeugt und angezeigt.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.start();
	}

}
