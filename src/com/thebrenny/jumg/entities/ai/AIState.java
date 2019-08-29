package com.thebrenny.jumg.entities.ai;

import java.util.HashMap;

import com.thebrenny.jumg.util.Logger;

public abstract class AIState {
	private static final HashMap<Integer, AIState> AI_STATES = new HashMap<Integer, AIState>();
	
	protected String name;
	protected int id;
	
	public AIState(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public abstract void action();
	public AIState nextState(Object ... objects) {
		return this;
	}
	
	public static boolean registerItem(AIState ai) {
		Logger.log("Registering item [{0}] with ID [{1}].", ai.name, ai.id);
		if(AI_STATES.get(ai.id) != null) Logger.log("OH NO! This item seems to be registered already! The ID is being used by [" + AI_STATES.get(ai.id) + "].");
		return AI_STATES.putIfAbsent(ai.id, ai) == null;
	}
	public static AIState getState(int id) {
		return AI_STATES.get(id);
	}
	public static AIState getState(String name) {
		for(AIState ai : AI_STATES.values())
			if(ai.name.equals(name)) return ai;
		return null;
	}
	
	/*
	 * You want to turn this into a FIFO-deque system. Pretty much a linked list
	 * that when the condition to cancel the action has been met, the the owner
	 * or the action is called to kill it somehow. I might need to psuedo/draw
	 * the layout...
	 */
	
}
