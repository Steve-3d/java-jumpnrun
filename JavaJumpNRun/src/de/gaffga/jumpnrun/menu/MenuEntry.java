package de.gaffga.jumpnrun.menu;

public class MenuEntry {
	
	/** Flag ob wir nur eine Leerzeile sind */
	private boolean separator = false;
	
	/** Flag ob wir ausgewählt werden können */
	private boolean selectable = false;
	
	/** Flag ob wir eine Checkbox sein sollen */
	private boolean checkbox = false;
	
	/** Flag ob wir ein Radiobutton sein sollen */
	private boolean radiobutton = false;

	/** Flag ob wir einen Bereich einstellen sollen */
	private boolean range = false;
	
	/** Die Untergrenze für den Bereich */
	private int rangeVon = 0;
	
	/** Die Obergrenze für den Bereich */
	private int rangeBis = 100;
	
	/** Das Kommando das wir ausführen sollen wenn wir bestätigt werden */
	private String command = null;

	/** Der Text mit dem wir uns auf dem Bildschirm darstellen */
	private String text = "";
	
	/** Der Name des Properties für Checkbox und Radiobutton */
	private String propertyName = null;
	
	/** Der Wert der bei der Auswahl eines Radiobuttons gesetzt werden soll */
	private int value = -1;

	/**
	 * Parst den angegebenen Eintrag und setzt den inneren Zustand so wie
	 * im Eintrag beschrieben.
	 * 
	 * @param entry der zu parsende und hinzuzufügende Eintrag
	 */
	public MenuEntry(String entry) {
		selectable = false;
		separator = false;
		checkbox = false;
		
		if ( entry.equals("") || entry.equals("-") ) {
			separator = true;
		} else {
			int index = entry.indexOf(',');
			if ( index == -1 ) {
				// Kein Komma enthalten
			} else {
				// Mit Komma -> wir haben ein Kommando und sind daher aufrufbar
				selectable = true;
				
				String command = entry.substring(0, index);
				String rest = entry.substring(index+1);
				
				this.command = command;
				
				// Der Rest ist der Text des Eintrags
				text = rest;
				
				// Untersuchen des Kommandos
				if ( command.startsWith("CHECKBOX")) {
					checkbox = true;
					propertyName = command.split("[()]")[1];
				} else if ( command.startsWith("RADIOBUTTON")) {
					radiobutton = true;
					String[] parts = command.split("[()=]"); 
					propertyName = parts[1];
					value = Integer.parseInt(parts[2]);
				} else if ( command.startsWith("RANGE")) {
					range = true;
					String[] parts = command.split("[()=/]"); 
					propertyName = parts[1];
					rangeVon = Integer.parseInt(parts[2]);
					rangeBis = Integer.parseInt(parts[3]);
				}
			}
		}
	}

	public boolean isCheckbox() {
		return checkbox;
	}

	public String getCommand() {
		return command;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public boolean isRadiobutton() {
		return radiobutton;
	}

	public boolean isSelectable() {
		return selectable;
	}
	
	public boolean isRange() {
		return range;
	}
	
	public int getRangeVon() {
		return rangeVon;
	}

	public int getRangeBis() {
		return rangeBis;
	}
	
	public boolean isSeparator() {
		return separator;
	}

	public String getText() {
		return text;
	}

	public int getValue() {
		return value;
	}

	public boolean isMenuVerweis() {
		try {
			Integer.parseInt(command);
			return true;
		} catch ( NumberFormatException nfe ) {
			return false;
		}
	}
	
	
}
