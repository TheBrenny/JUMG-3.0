package com.thebrenny.jumg.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.thebrenny.jumg.gui.components.Component;

public abstract class ScreenMenu extends Screen {
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
	
	public void updateBackground() {}
	public static BufferedImage defaultBackground() {
		return ScreenMenu.DEFAULT_BACKGROUND == null ? new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB) : ScreenMenu.DEFAULT_BACKGROUND;
	}
}