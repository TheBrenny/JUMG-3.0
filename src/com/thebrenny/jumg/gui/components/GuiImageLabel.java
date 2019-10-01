package com.thebrenny.jumg.gui.components;

import java.awt.image.BufferedImage;

import com.thebrenny.jumg.util.Images;

public class GuiImageLabel extends GuiLabel {
	private static final long serialVersionUID = 1L;
	protected BufferedImage image;
	
	public GuiImageLabel(float x, float y, BufferedImage image) {
		super(x, y, null, null);
		this.image = image;
		super.resize(image == null ? 1 : image.getWidth(), image == null ? 1 : image.getHeight());
		//super.align(ALIGN_CENTRE); // This is silly. This was definitely me wanting a specific thing instead of generalising.
		this.fixBounds();
	}
	public GuiImageLabel(float x, float y, float width, float height, BufferedImage image) {
		this(x, y, image);
		super.resize(width, height);
		this.fixBounds();
	}
	public GuiImageLabel setImage(BufferedImage image) {
		this.image = image;
		this.requestNewImage();
		return this;
	}
	
	public float getMaxWidth() {
		return (float) this.getWidth();
	}
	public float getMaxHeight() {
		return (float) this.getHeight();
	}
	public float getLabelX() {
		float xOff = (float) (allignment[0].equalsIgnoreCase(ALIGN_HORIZONTAL_RIGHT) ? getWidth() : allignment[0].equalsIgnoreCase(ALIGN_HORIZONTAL_CENTRE) ? getWidth() / 2 : 0);
		return (float) getX() - xOff;
	}
	public float getLabelY() {
		float yOff = (float) (allignment[1].equalsIgnoreCase(ALIGN_VERTICAL_BOTTOM) ? getHeight() : allignment[1].equalsIgnoreCase(ALIGN_VERTICAL_CENTRE) ? getHeight() / 2 : 0);
		return (float) getY() - yOff;
	}
	public BufferedImage getNewImage() {
		return Images.getResizedImage(image, (int) getWidth(), (int) getHeight());
	}

	public GuiImageLabel resize(float width, float height) {
		super.setRect(this.x, this.y, width, height);
		this.requestNewImage();
		return this;
	}
}
