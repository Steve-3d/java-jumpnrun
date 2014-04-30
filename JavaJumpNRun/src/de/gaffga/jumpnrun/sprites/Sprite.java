package de.gaffga.jumpnrun.sprites;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import de.gaffga.jumpnrun.resources.ResourceManager;
import de.gaffga.jumpnrun.resources.ResourceManagerException;

/**
 * Ein einzelnes Sprite das animiert und bewegt werden kann.
 * 
 * Die Darstellung des Sprites übernimmt ausschließlich das Painter-Objekt.
 * 
 * @author Stefan Gaffga <stefan@gaffga.de>
 */
public class Sprite {

	/** Das Bild mit dem Spritebildern */
	private Image image = null;
	
	/** Breite des Sprites in Pixel */
	private int width;
	
	/** Höhe des Sprites in Pixel */
	private int height;
	
	/** Die Hitbox */
	private Rectangle hitbox = null;
	
	/** Map mit allen vorhandenen Animationen */
	private HashMap<String, SpriteAnimation> animations = null;
	
	/** Referenz auf die aktuell aktive Animation (null = keine) */
	private SpriteAnimation currentAnimation = null;
	
	/** Name dieses Sprites */
	private String name = null;
	
	/** Sichtbarflag */
	private boolean visible = false;
	
	/** Die aktuelle Position in Map-Pixel-Koordinaten */
	private Point position = null;
	
	/** Das letzte Bild aus der Animation (dies ist das was gezeigt wird wenn die Animation gestoppt wird) */
	private SpriteAnimationFrame lastFrame = null;
	
	/**
	 * Konstruktor.
	 * 
	 * @param name Name der Spritedefinitionsdatei
	 * @throws SpriteException 
	 */
	public Sprite(String name) throws SpriteException {
		animations = new HashMap<String, SpriteAnimation>();
		this.name = name;
		this.position = new Point(0,0);
		readSprite(name);
	}
	
	/**
	 * Setzt eine neue Position.
	 * 
	 * @param pos die neue Position
	 */
	public void setPosition(Point pos) {
		position = pos;
	}
	
	/** 
	 * Liefert die aktuelle Position.
	 * 
	 * @return die aktuelle Position
	 */
	public Point getPosition() {
		return position;
	}
	
	/**
	 * Setzt eine aktive Animation - d.h. spielt sie nun ab.
	 * 
	 * Die Animation wird zurückgesetzt und damit von Beginn an abgespielt falls
	 * die Animation nicht bereits die aktive ist! In letzterem Fall wird nichts
	 * verändert.
	 * 
	 * @param name Name der abzuspielenden Animation
	 */
	public void setCurrentAnimation(String name) {
		if ( name==null ) {
			currentAnimation = null;
		}
		if ( currentAnimation != animations.get(name)) {
			currentAnimation = animations.get(name);
			currentAnimation.reset();
		}
	}
	
	/**
	 * Liefert das aktuell gültige Frame.
	 * 
	 * @throws SpriteException 
	 */
	public SpriteAnimationFrame getCurrentFrame() throws SpriteException {
		if ( currentAnimation==null ) {
			// Falls die Animationen abgeschaltet sind liefern wir das zuletzt
			// genutzte Frame zurück
			return lastFrame;
		}
		
		lastFrame =  currentAnimation.getCurrentFrame();
		
		return lastFrame;
	}
	
	/**
	 * Führt einen Schritt für die aktuelle Animation durch.
	 */
	public void step() {
		if ( currentAnimation != null ) {
			currentAnimation.step();
		}
	}
	
	/**
	 * Fügt eine neue Animation der Liste hinzu.
	 * 
	 * @param name der Name der Animation
	 * @param anim die neue Animation
	 */
	public void addAnimation(String name, SpriteAnimation anim) {
		animations.put(name, anim);
	}
	
	/**
	 * Liest das Sprite ein.
	 * 
	 * @param name das zu lesende Sprite
	 * @throws SpriteException 
	 */
	protected void readSprite(String name) throws SpriteException {
		try {
			Properties props = ResourceManager.getInstance().getProperties(name);
			String imageFile = props.getProperty("image");
			if ( imageFile != null ) {
				image = ResourceManager.getInstance().getImage(imageFile);
			} else {
				throw new SpriteException("Sprite-Image '"+name+"' konnte nicht geladen werden!");
			}
			
			String width = props.getProperty("width");
			if ( width != null ) {
				this.width = Integer.parseInt(width);
			} else {
				throw new SpriteException("Die width-Angabe des Sprites '"+name+"' fehlt!");
			}

			String height = props.getProperty("height");
			if ( height != null ) {
				this.height = Integer.parseInt(height);
			} else {
				throw new SpriteException("Die height-Angabe des Sprites '"+name+"' fehlt!");
			}
			
			String hitbox = props.getProperty("hitbox");
			if ( hitbox==null ) {
				// Falls keine Hitbox da ist wird das gesamte Sprite verwendet
				this.hitbox = new Rectangle(0,0,this.width,this.height);
			} else {
				String[] elements = hitbox.split(",");
				this.hitbox = new Rectangle(Integer.parseInt(elements[0]),
						Integer.parseInt(elements[1]),
						Integer.parseInt(elements[2]),
						Integer.parseInt(elements[3]));
			}

			// Einlesen der Animationen aus den Properties
			readAnimation(props, SpriteAnimation.WALK_RIGHT);
			readAnimation(props, SpriteAnimation.WALK_LEFT);
			readAnimation(props, SpriteAnimation.IDLE_RIGHT);
			readAnimation(props, SpriteAnimation.IDLE_LEFT);
			readAnimation(props, SpriteAnimation.JUMP_RAISE_RIGHT);
			readAnimation(props, SpriteAnimation.JUMP_FALL_RIGHT);
			readAnimation(props, SpriteAnimation.JUMP_RAISE_LEFT);
			readAnimation(props, SpriteAnimation.JUMP_FALL_LEFT);
			readAnimation(props, SpriteAnimation.DANCE);
			readAnimation(props, SpriteAnimation.DEAD);
			
		} catch (ResourceManagerException e) {
			throw new SpriteException("Fehler beim Initialisieren des Sprites '"+name+"'", e);
		}
	}
	
	/** 
	 * Liest eine bestimmte Animation ein.
	 * 
	 * @param name der Bame der zu lesenden Animation
	 */
	protected void readAnimation(Properties props, String name) {
		String animString = props.getProperty(name);
		if ( animString!=null ) {  
			SpriteAnimation anim = parseAnimationString(animString);
			addAnimation(name, anim);
		} else {
			// Diese Animation ist nicht vorhanden
			Logger.getLogger(this.getClass().getName()).warning("Sprite: '"+this.name+"': Animation '"+name+"' wurde nicht gefunden.");
		}
	}
	
	/**
	 * Parst den Animationsstring und erstellt daraus ein Animationsobjekt.
	 * 
	 * @param animString der Animationsbeschreibungs-String
	 * @return das erzeugte Animationsobjekt
	 */
	protected SpriteAnimation parseAnimationString(String animString) {
		String[] parts = animString.split("/");
		
		SpriteAnimation animation = new SpriteAnimation();
		
		// Die Defaultdauer
		int duration = 200;
		
		for ( String part : parts ) {
			String inner = part.substring(1, part.length()-1);
			String[] animParts = inner.split(",");
			int row = Integer.parseInt(animParts[0]);
			int col = Integer.parseInt(animParts[1]);
			if ( animParts.length==3 ) {
				duration = Integer.parseInt(animParts[2]);
			}
			
			int x = col * width;
			int y = row * height;
			
			SpriteAnimationFrame frame = new SpriteAnimationFrame(x,y,duration);
			animation.addFrame(frame);
		}
		
		return animation;
	}
	
	/**
	 * Liefert die Hitbox.
	 * 
	 * @return die Hitbox
	 */
	public Rectangle getHitbox() {
		return hitbox;
	}
	
	/**
	 * Liefert das Image-Objekt.
	 * 
	 * @return des Image-Objekt
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Liefert die Breite des Sprites.
	 * 
	 * @return die Breite in Pixeln
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Liefert die Höhe des Sprites.
	 * 
	 * @return die Höhe in Pixeln
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Liefert den Namen des Spritest.
	 * 
	 * @return der Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Liefert den Sichtbarkeitszustand.
	 * 
	 * @return sichtbar ja/nein
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Setzt das Sichtbarkeitsflag.
	 * 
	 * @param vis Visibleflag 
	 */
	public void setVisible(boolean vis) {
		visible=vis;
	}
	
	/**
	 * Zeichnet das Sprite mit dem aktuellen Frame
	 * auf ein Image.
	 * 
	 * @param pos die Position an die gezeichnet werden soll
	 * @param img die Image auf die gezeichnet werden soll
	 * @throws SpriteException 
	 */
	public void draw(Point pos, Image img) throws SpriteException {
		Graphics g = img.getGraphics();
		Rectangle r = new Rectangle();
		SpriteAnimationFrame frame;
		frame = getCurrentFrame();
		r.x = frame.getX();
		r.y = frame.getY();
		r.width = getWidth();
		r.height = getHeight();
		
		g.drawImage(this.image,	pos.x, pos.y, 
				pos.x+getWidth(), pos.y+getHeight(),
				r.x,r.y,r.x+r.width,r.y+r.height, null);
	}

	/**
	 * Prüft ob zwei Sprites kollidieren. Dies ist dann der Fall wenn deren
	 * Hitboxen sich schneiden.
	 *  
	 * @param sprite das andere Sprite 
	 * @return true falls eine Kollision besteht, sonst false
	 */
	public boolean checkCollision(Sprite sprite) {
		Rectangle r1 = new Rectangle(getHitbox());
		Rectangle r2 = new Rectangle(sprite.getHitbox());
		
		r1.x += getPosition().x;
		r1.y += getPosition().y;
		
		r2.x += sprite.getPosition().x;
		r2.y += sprite.getPosition().y;
		
		return (r1.intersects(r2));
	}
}
