package com.thebrenny.jumg.hud;

import java.awt.Graphics2D;
import java.awt.Point;

import com.thebrenny.jumg.gui.components.GuiButton;

public class HudGuiButton extends HudElement implements IHudButton {
	protected GuiButton button;
	
	public HudGuiButton(int x, int y, int width, int height, String str, Runnable run) {
		super(x, y, width, height);
		button = new GuiButton(x, y, width, height, str, run);
	}
	
	public void mouseEvent(Point mousePoint, boolean clicked) {
		if(!button.isEnabled()) return;
		button.changeMe(button.contains(mousePoint), clicked, mousePoint);
	}
	public void onClick(Point mousePoint) {
		// nothing??
	}
	public void tick() {
		button.tick();
	}
	public void render(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		button.render(g2d, camX, camY);
	}
}
