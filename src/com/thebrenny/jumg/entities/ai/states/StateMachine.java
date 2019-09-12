package com.thebrenny.jumg.entities.ai.states;

import java.awt.Graphics2D;

import com.thebrenny.jumg.entities.Entity;

public interface StateMachine<E extends Entity, S extends State<E>> {
	//@formatter:off
    public void tick();
	public void changeState(S newState);
	public boolean revertToPreviousState();
	public void setInitialState(S state);
	public void setGlobalState(S state);
	public S getCurrentState();
    public S getGlobalState();
	public S getPreviousState ();
	public boolean isInState(S state);
	public void renderDebug(Graphics2D g2d, long camX, long camY, int camW, int camH);
    //@formatter:on
}
