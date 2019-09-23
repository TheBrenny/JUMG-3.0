package com.thebrenny.jumg.entities.ai.states;

import java.awt.Graphics2D;

import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.entities.messaging.Message;

public interface State<E extends Entity> {
	public void enter(E entity);
	public void tick(E entity);
	public boolean messageReceived(Message message);
	public void exit(E entity);
	public void renderDebug(E entity, Graphics2D g2d, long camX, long camY, long camW, long camH);
}