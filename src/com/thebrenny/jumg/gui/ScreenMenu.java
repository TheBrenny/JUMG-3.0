package com.thebrenny.jumg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.thebrenny.jumg.gui.components.Component;

public abstract class ScreenMenu extends Screen {
	public static final short DRAW_IMAGE_FIT_MIN = 0b100000;
	public static final short DRAW_IMAGE_FIT_MAX = 0b110000;
	public static final short DRAW_IMAGE_STRETCH = 0b010000;
	public static final short DRAW_IMAGE_HORI_LEFT = 0b001000;
	public static final short DRAW_IMAGE_HORI_RIGHT = 0b000100;
	public static final short DRAW_IMAGE_HORI_CENTER = 0b001100;
	public static final short DRAW_IMAGE_VERTI_UP = 0b000010;
	public static final short DRAW_IMAGE_VERTI_DOWN = 0b000001;
	public static final short DRAW_IMAGE_VERTI_CENTER = 0b000011;
	
	private static final BufferedImage DEFAULT_BACKGROUND = ScreenMenu.defaultBackground();
	protected ArrayList<Component> components;
	protected BufferedImage backgroundImage;
	
	public ScreenMenu() {
		this(ScreenMenu.defaultBackground());
	}
	
	public ScreenMenu(BufferedImage image) {
		super();
		this.backgroundImage = image;
		this.components = new ArrayList<Component>();
	}
	
	public void update() {
		updateBackground();
		updateComponents();
		tick();
	}
	public void draw(Graphics2D g2d) {
		renderBackground(g2d);
		renderComponents(g2d);
		render(g2d);
	}
	
	public boolean addComponent(Component c) {
		return components.add(c);
	}
	public void updateComponents() {
		for(Component c : components) {
			c.tick();
		}
	}
	public abstract void tick();
	public void renderBackground(Graphics2D g2d) {
		g2d.drawImage(this.backgroundImage, 0, 0, Screen.getWidth(), Screen.getHeight(), null);
	}
	public abstract void render(Graphics2D g2d);
	public void renderComponents(Graphics2D g2d) {
		for(Component c : components) {
			c.render(g2d, 0, 0);
		}
	}
	
	public void mouseEvent(Point mousePoint, boolean clicked) {
		for(Component c : components) {
			if(!c.isEnabled()) continue;
			c.changeMe(c.contains(mousePoint), clicked, mousePoint);
		}
	}
	
	public void updateBackground() {
	}
	public static BufferedImage defaultBackground() {
		return ScreenMenu.DEFAULT_BACKGROUND == null ? new BufferedImage(Screen.getWidth(), Screen.getHeight(), BufferedImage.TYPE_INT_ARGB) : ScreenMenu.DEFAULT_BACKGROUND;
	}
	public static BufferedImage setDefaultBackgroundColor(Color c) {
		BufferedImage b = defaultBackground();
		Graphics2D g = (Graphics2D) b.getGraphics();
		g.setColor(c);
		g.fillRect(0, 0, Screen.getWidth(), Screen.getHeight());
		g.dispose();
		return b;
	}
	public static BufferedImage setDefaultBackgroundImage(BufferedImage img) {
		return setDefaultBackgroundImage(img, DRAW_IMAGE_FIT_MIN | DRAW_IMAGE_HORI_CENTER | DRAW_IMAGE_VERTI_CENTER);
	}
	public static BufferedImage setDefaultBackgroundImage(BufferedImage img, int bitmask) {
		BufferedImage b = defaultBackground();
		Graphics2D g = (Graphics2D) b.getGraphics();
		int x = 0;
		int y = 0;
		int width = img.getWidth();
		int height = img.getHeight();
		Float mp = null;
		
		// swithc to ifs
		if((bitmask & DRAW_IMAGE_FIT_MAX) == DRAW_IMAGE_FIT_MAX) {
			mp = Math.max((float) (width - Screen.getWidth()) / width, (float) (height - Screen.getHeight()) / height);
		}
		if((bitmask & DRAW_IMAGE_FIT_MIN) == DRAW_IMAGE_FIT_MIN) {
			mp = mp == null ? Math.min((float) (width - Screen.getWidth()) / width, (float) (height - Screen.getHeight()) / height) : mp;
			width -= (mp * width);
			height -= (mp * height);
		} else if((bitmask & DRAW_IMAGE_STRETCH) == DRAW_IMAGE_STRETCH) {
			width = Screen.getWidth();
			height = Screen.getHeight();
		}
		
		if((bitmask & DRAW_IMAGE_HORI_CENTER) == DRAW_IMAGE_HORI_CENTER) x = (Screen.getWidth() - width) / 2;
		else if((bitmask & DRAW_IMAGE_HORI_LEFT) == DRAW_IMAGE_HORI_LEFT) x = 0;
		else if((bitmask & DRAW_IMAGE_HORI_RIGHT) == DRAW_IMAGE_HORI_RIGHT) x = (Screen.getWidth() - width);
		
		if((bitmask & DRAW_IMAGE_VERTI_CENTER) == DRAW_IMAGE_VERTI_CENTER) y = (Screen.getHeight() - height) / 2;
		else if((bitmask & DRAW_IMAGE_VERTI_UP) == DRAW_IMAGE_VERTI_UP) y = 0;
		else if((bitmask & DRAW_IMAGE_VERTI_DOWN) == DRAW_IMAGE_VERTI_DOWN) y = (Screen.getHeight() - height);

		g.drawImage(img, x, y, width, height, null);
		
		g.dispose();
		return b;
	}
	
}
