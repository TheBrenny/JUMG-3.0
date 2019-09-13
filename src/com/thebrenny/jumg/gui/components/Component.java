package com.thebrenny.jumg.gui.components;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Component extends Rectangle2D.Float {
	private static final long serialVersionUID = 1L;
	protected Runnable run;
	protected boolean enabled = true;
	protected boolean hovering;
	protected boolean clicked;
	protected boolean pressed;
	
	private BufferedImage image;
	// TODO: Make it so this has a "shouldUpdate" attribute, so we don't constantly recreate the image for no change.
	
	public Component(float x, float y) {
		this(x, y, 0, 0, null);
	}
	public Component(float x, float y, float width, float height) {
		this(x, y, width, height, null);
	}
	public Component(float x, float y, float width, float height, Runnable run) {
		super(x, y, width, height);
		this.run = run;
	}
	
	public boolean changeMe(boolean hover, boolean clicked, Point2D mousePoint) {
		if(enabled) {
			this.hovering = hover;
			this.clicked = hover && clicked;
			
			if(run != null) {
				doClick();
			}
			this.requestNewImage();
			return true;
		}
		return false;
	}
	public void doClick() {
		if(this.clicked) pressed = true;
		
		if(pressed && hovering && !clicked) {
			run.run();
			pressed = false;
		} else if(!hovering) {
			pressed = false;
		}
	}
	
	public Component setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public boolean isHovering() {
		return this.hovering;
	}
	public boolean isClicked() {
		return this.clicked;
	}
	public boolean isPressed() {
		return this.pressed;
	}
	
	public Component resize(float width, float height) {
		super.setRect(this.x, this.y, width, height);
		return this;
	}
	public Component move(float x, float y) {
		super.setRect(x, y, this.width, this.height);
		return this;
	}
	
	public void tick() {}
	public void render(Graphics2D g2d, long xOffset, long yOffset) {
		g2d.drawImage(getImage(), (int) (getX() - xOffset), (int) (getY() - yOffset), null);
	}
	public BufferedImage getImage() {
		if(this.image == null) this.image = getNewImage();
		return this.image;
	}
	public abstract BufferedImage getNewImage();
	public void requestNewImage() {
		this.image = null;
	}
}
