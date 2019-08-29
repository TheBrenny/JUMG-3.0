package com.thebrenny.jumg.hud;

import java.awt.Graphics2D;

import com.thebrenny.jumg.entities.Entity;

public abstract class HudElement {
	protected Entity entity;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	public HudElement(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public HudElement setEntity(Entity e) {
		this.entity = e;
		return this;
	}
	public HudElement setEntityIfNull(Entity e) {
		return getEntity() == null ? setEntity(e) : this;
	}
	
	public Entity getEntity() {
		return this.entity;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public abstract void tick();
	public abstract void render(Graphics2D g2d, long camX, long camY, int camW, int camH);
}
