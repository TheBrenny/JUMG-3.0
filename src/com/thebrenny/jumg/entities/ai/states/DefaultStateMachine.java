package com.thebrenny.jumg.entities.ai.states;

import java.awt.Graphics2D;

import com.thebrenny.jumg.entities.Entity;

/*
 * Big thanks to libGDX for providing me with some saucey source!!!
 */
public class DefaultStateMachine<E extends Entity, S extends State<E>> implements StateMachine<E, S> {
	protected E owner;
	protected S currentState;
	protected S previousState;
	protected S globalState;
	
	public DefaultStateMachine() {
		this(null, null, null);
	}
	public DefaultStateMachine(E owner) {
		this(owner, null, null);
	}
	public DefaultStateMachine(E owner, S initialState) {
		this(owner, initialState, null);
	}
	public DefaultStateMachine(E owner, S initialState, S globalState) {
		this.owner = owner;
		this.setInitialState(initialState);
		this.setGlobalState(globalState);
	}
	
	public S getState() {
		return getState(true);
	}
	public S getState(boolean youMeanCurrentYeah) {
		return youMeanCurrentYeah ? getCurrentState() : getGlobalState();
	}
	public S getCurrentState() {
		return currentState;
	}
	public S getGlobalState() {
		return globalState;
	}
	public S getPreviousState() {
		return previousState;
	}

	public E getOwner() {
		return owner;
	}
	public void setOwner(E owner) {
		this.owner = owner;
	}
	
	public void setInitialState(S state) {
		this.previousState = null;
		this.currentState = state;
	}
	public void setGlobalState(S state) {
		this.globalState = state;
	}
	
	public void tick() {
		if(globalState != null) globalState.tick(owner);
		if(currentState != null) currentState.tick(owner);
	}
	
	public void changeState(S newState) {
		previousState = currentState;
		if(currentState != null) currentState.exit(owner);
		currentState = newState;
		if(currentState != null) currentState.enter(owner);
	}
	
	public boolean revertToPreviousState() {
		if(previousState == null) {
			return false;
		}
		
		changeState(previousState);
		return true;
	}

	public boolean isInState(S state) {
		return currentState == state;
	}
	public void renderDebug(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		if(currentState != null) currentState.renderDebug(owner, g2d, camX, camY, camW, camH);
	}
}
