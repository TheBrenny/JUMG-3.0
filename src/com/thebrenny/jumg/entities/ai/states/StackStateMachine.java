package com.thebrenny.jumg.entities.ai.states;

import java.util.Stack;

import com.thebrenny.jumg.entities.Entity;

/**
 * StackStateMachine
 */
public class StackStateMachine<E extends Entity, S extends State<E>> extends DefaultStateMachine<E, S> {
	
	private Stack<S> stack;
	
	public StackStateMachine() {
		this(null, null, null);
	}
	public StackStateMachine(E owner) {
		this(owner, null, null);
	}
	public StackStateMachine(E owner, S initialState) {
		this(owner, initialState, null);
	}
	public StackStateMachine(E owner, S initialState, S globalState) {
		super(owner, initialState, globalState);
	}
	
	public void setInitialState(S state) {
		if(stack == null) stack = new Stack<S>();
		this.stack.clear();
		this.currentState = state;
	}
	
	@Override
	public S getPreviousState() {
		if(stack == null) return null;
		if(stack.size() == 0) return null;
		else return stack.peek();
	}
	@Override
	public void changeState(S newState) {
		this.changeState(newState, true);
	}
	public void changeState(S newState, boolean pushCurrentToStack) {
		if(pushCurrentToStack) this.stack.add(this.currentState);
		if(this.currentState != null) this.currentState.exit(this.owner);
		this.currentState = newState;
		this.currentState.enter(this.owner);
	}
	
	public boolean revertToPreviousState() {
		if(stack.size() == 0) return false;
		S prevState = stack.pop();
		changeState(prevState, false);
		return true;
	}
}
