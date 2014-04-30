package de.gaffga.jumpnrun.effects;

import java.awt.Image;
import java.util.LinkedList;
import java.util.List;

/**
 * Verwaltet alle ablaufenden Effekte.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class EffectManager {

	/** Die Liste aller aktiven Effekte */
	private List<IEffect> effects = null;
	
	/**
	 * Konstruktor.
	 */
	public EffectManager() {
		effects = new LinkedList<IEffect>();
	}
	
	/**
	 * Fügt einen neuen Effekt der Liste hinzu.
	 * 
	 * @param effekt der neue Effekt
	 */
	public void addEffect(IEffect effekt) {
		effects.add(effekt);
		effekt.start();
	}
	
	/**
	 * Führt einen Schritt für alle Effekte aus.
	 * 
	 */
	public void step() {
		for ( IEffect effect : effects ) {
			effect.step();
		}
		
		// Herausfinden welche Effekte fertig sind und entfernen
		boolean removed = true;
		while ( removed ) {
			removed=false;
			for ( IEffect effect : effects ) {
				if (effect.isFinished() ) {
					effects.remove(effect);
					removed=true;
					break;
				}
			}
		}
	}
	
	/**
	 * Zeichnet alle Effekte auf ein Image.
	 * 
	 */
	public void draw(Image image) {
		for ( IEffect effect : effects ) {
			effect.draw(image);
		}
	}

	/**
	 * Löscht alle Einträge.
	 */
	public void flush() {
		effects.clear();
	}
}
