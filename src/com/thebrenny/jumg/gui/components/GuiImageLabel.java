package com.thebrenny.jumg.gui.components;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GuiImageLabel extends GuiLabel {
	private static final long serialVersionUID = 1L;
	protected BufferedImage image;
	
	public GuiImageLabel(float x, float y, BufferedImage image) {
		super(x, y, null, null);
		this.image = image;
		super.resize(image == null ? 1 : image.getWidth(), image == null ? 1 : image.getHeight());
		super.allign(ALLIGN_CENTRE);
	}
	
	public float getLabelX() {
		float xOff = (float) (allignment[0].equalsIgnoreCase(ALLIGN_HORIZONTAL_RIGHT) ? getWidth() : allignment[0].equalsIgnoreCase(ALLIGN_HORIZONTAL_CENTRE) ? getWidth() / 2 : 0);
		return (float) getX() - xOff;
	}
	public float getLabelY() {
		float yOff = (float) (allignment[1].equalsIgnoreCase(ALLIGN_VERTICAL_BOTTOM) ? getHeight() : allignment[1].equalsIgnoreCase(ALLIGN_VERTICAL_CENTRE) ? getHeight() / 2 : 0);
		return (float) getY() - yOff;
	}
	public BufferedImage getImage() {
		return image;
	}
	public void render(Graphics2D g2d, long xOffset, long yOffset) {
		g2d.drawImage(getImage(), (int) getLabelX(), (int) getLabelY(), null);
	}
}