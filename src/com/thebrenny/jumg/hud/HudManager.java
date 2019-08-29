package com.thebrenny.jumg.hud;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;

import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.level.Level;

public class HudManager {
	protected HashMap<String, HudElement> elements;
	protected Entity primaryEntity;
	protected Level level;
	protected int height;
	protected int width;
	
	public HudManager(int width, int height) {
		elements = new HashMap<String, HudElement>();
		this.width = width;
		this.height = height;
	}
	
	public HudManager addHudElement(String name, HudElement he) {
		he.setEntityIfNull(this.getPrimaryEntity());
		elements.put(name, he);
		return this;
	}
	
	public HudManager setPrimaryEntity(Entity e) {
		this.primaryEntity = e;
		if(e.getLevel() != null) setLevel(e.getLevel());
		
		for(HudElement he : getElements().values())
			he.setEntityIfNull(e);
		
		return this;
	}
	
	public HudManager setLevel(Level level) {
		this.level = level;
		return this;
	}
	
	public HudElement getHudElement(String name) {
		return getElements().get(name);
	}
	
	public synchronized HashMap<String, HudElement> getElements() {
		return this.elements;
	}
	
	public boolean isMenuOpen() {
		for(HudElement he : getElements().values()) {
			if(he instanceof HudMenu) if(((HudMenu) he).isOpen()) return true;
		}
		return false;
	}
	
	public Entity getPrimaryEntity() {
		return this.primaryEntity;
	}
	
	public void mouseEvent(Point mousePoint, boolean mouseDown) {
		for(HudElement he : getElements().values()) {
			if(he instanceof HudMenu && ((HudMenu) he).isOpen()) {
				((HudMenu) he).mouseEvent(mousePoint, mouseDown);
			}
		}
	}
	
	public void tick() {
		for(HudElement he : getElements().values()) {
			if(he instanceof HudMenu) {
				if(((HudMenu) he).isOpen()) he.tick();
			} else he.tick();
		}
	}
	public void render(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		for(HudElement he : getElements().values()) {
			if(he instanceof HudMenu) {
				if(((HudMenu) he).isOpen()) ((HudMenu) he).render(g2d, 0, 0, camW, camH);
			} else he.render(g2d, 0, 0, camW, camH);
		}
	}
}
