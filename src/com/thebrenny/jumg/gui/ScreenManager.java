package com.thebrenny.jumg.gui;

import java.util.Stack;

import com.thebrenny.jumg.util.Logger;

public class ScreenManager {
	private Stack<Screen> screenStack;
	private Screen screen;
	
	public ScreenManager() {
		this.screenStack = new Stack<Screen>();
	}

	public Screen currentScreen() {
		return screen;
	}

	public void setScreen(Screen screen) {
		if(screen == null) {
			Logger.log("OH NO! The screen you passed was seen as null!");
			return;
		}
		Logger.log("Switching screens: [{0}] -> [{1}]", (Object) (this.screen == null ? "null" : this.screen.getClass().getSimpleName()), screen.getClass().getSimpleName());
		this.screen = screen;
	}
	
	public Screen screenClear() {
		Screen s = screenStack.elementAt(0);
		screenStack.removeAllElements();
		return s;
	}
	public void screenBack() {
		if(!screenStack.empty()) setScreen(this.screenStack.pop());
		else System.err.println("The screen stack is empty!");
	}
	public void screenForward(Screen screen) {
		if(screen == null) {
			Logger.log("OH NO! The screen you passed was seen as null!");
			return;
		}
		screenStack.push(this.screen);
		this.setScreen(screen);
	}

	public void update() {
		this.screen.update();
	}
}
