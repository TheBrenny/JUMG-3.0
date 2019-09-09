package com.thebrenny.jumg.gui.components;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class Component extends Rectangle2D.Float {
	protected Runnable run;
	protected boolean enabled = true;
	protected boolean hovering;
	protected boolean clicked;
	protected boolean pressed;
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
	
	public void changeMe(boolean hover, boolean clicked, Point2D mousePoint) {
		if(enabled) {
			this.hovering = hover;
			this.clicked = hover && clicked;
			
			if(run != null) {
				doClick();
			}
		}
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
	
	public void tick() {}
	public abstract void render(Graphics2D g2d, long xOffset, long yOffset);
}
