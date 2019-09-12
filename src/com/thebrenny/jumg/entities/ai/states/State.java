package com.thebrenny.jumg.entities.ai.states;

import java.awt.Graphics2D;

import com.thebrenny.jumg.entities.Entity;

public interface State<E extends Entity> {
	public void enter(E entity);
	public void tick(E entity);
	public void exit(E entity);
	public void renderDebug(E entity, Graphics2D g2d, long camX, long camY, long camW, long camH);
}