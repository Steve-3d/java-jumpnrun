package de.gaffga.jumpnrun.menu;


/**
 * Interface über das Objekte die Möglichkeit bekommen ausgewählte Menüeintrage
 * übermittelt zu bekommen.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public interface IMenuActionListener {

	public void actionPerformed(String actionName) throws MenuException;
	
}
