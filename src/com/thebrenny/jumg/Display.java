package com.thebrenny.jumg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.thebrenny.jumg.gui.ScreenManager;
import com.thebrenny.jumg.gui.ScreenSplash;
import com.thebrenny.jumg.util.Logger;

public class Display extends JPanel {
	private static final long serialVersionUID = 6375792498178708388L;
	
	protected ScreenManager screenManager;
	
	public Display(Dimension size) {
		Logger.startSection("displayInit", "Initialising display class.");
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		screenManager = new ScreenManager();
		screenManager.setScreen(new ScreenSplash());
		Logger.endSection("displayInit", "Display class initialised.");
	}
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		Graphics2D g2d = (Graphics2D) g;
		draw(g2d);
		g2d.dispose();
	}
	public void draw(Graphics2D g2d) {
		if(screenManager.currentScreen() != null) screenManager.currentScreen().draw(g2d);
	}
	
	public ScreenManager screenMan() {
		return this.screenManager;
	}
}