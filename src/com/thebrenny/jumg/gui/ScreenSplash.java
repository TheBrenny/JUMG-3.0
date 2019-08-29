package com.thebrenny.jumg.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.util.Images;

public class ScreenSplash extends Screen {
	BufferedImage splash = Images.getImage("splash");
	
	public void tick() {}
	public void render(Graphics2D g2d) {
		g2d.drawImage(splash, (getWidth() - splash.getWidth()) / 2, (getHeight() - splash.getHeight()) / 2, null);
	}
}