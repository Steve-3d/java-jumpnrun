package de.gaffga.jumpnrun.tiles;

/**
 * Der Zustand eines Tiles der während des Spiels dann je nach Ereignis angepasst
 * wird. 
 * 
 * Tiles befinden sich beim Start der Map im unberührten Zustand (VIRGIN). Die anderen
 * möglichen Zustände hängen von den Eigenschaften des Tiles ab - einsammelbare Tiles
 * können als Status COLLECTED erhalten.
 *  
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class TileStatus {

	/** der aktuelle Zustand des Tiles */
	int status;
	
	/** Der unberührte, initiale Zustand */
	public final static int VIRGIN = 0;
	
	/** Wenn der Spieler ein COLLECTABLE Tile berührt hat */
	public final static int COLLECTED = 1;
	
	/** Wenn der Spieler das Tile berührt hat */
	public final static int TOUCHED = 2;
	
	/**
	 * Erzeugt einen neuen TileStatus im Ursprungszustand.
	 *
	 */
	public TileStatus() {
		status = VIRGIN;
	}
	
	public boolean isVirgin() {
		return (status & VIRGIN) != 0;
	}
	
	public boolean isCollected() {
		return (status & COLLECTED) != 0;
	}
	
	public boolean isTouched() {
		return (status & TOUCHED) != 0;
	}

	public void setCollected() {
		status |= COLLECTED;
	}
	
	public void setTouched() {
		status |= TOUCHED;
	}
}

