package com.thebrenny.jumg.hud;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.thebrenny.jumg.util.Images;

public abstract class HudMenu extends HudElement {
	public static final BufferedImage GUI_MAP_IMAGE = Images.getImage("hud_menu_map");
	public static final int GUI_MAP_MULTIPLIER = 33;
	public static final int GUI_MAP_SECTION_COUNT = 3;
	public static final int GUI_MAP_SECTION_SIZE = GUI_MAP_MULTIPLIER / GUI_MAP_SECTION_COUNT;
	public static final BufferedImage[][] GUI_MAP;
	
	protected boolean isOpen = false;
	protected HudElement[] elements;
	protected BufferedImage backgroundCache;
	
	public HudMenu(int x, int y, int width, int height) {
		super(x, y, width, height);
		setElements();
	}
	
	public HudMenu setElements(HudElement ... elements) {
		this.elements = elements;
		return this;
	}
	public HudMenu addElement(HudElement element) {
		elements = Arrays.copyOf(elements, elements.length + 1);
		this.elements[this.elements.length - 1] = element;
		return this;
	}
	
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
		if(isOpen) onOpen();
		else onClose();
	}
	public boolean isOpen() {
		return this.isOpen;
	}
	
	protected void onOpen() {}
	protected void onClose() {}
	
	public void tick() {
		for(HudElement he : elements) {
			he.tick();
		}
	}
	
	public void mouseEvent(Point mousePoint, boolean mouseDown) {
		for(HudElement he : elements) {
			if(he instanceof IHudButton) {
				((IHudButton) he).mouseEvent(mousePoint, mouseDown);
			}
		}
	}
	
	public void requestNewImage() {
		this.backgroundCache = null;
	}
	public BufferedImage getImage() {
		if(this.backgroundCache == null) {
			BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();
			
			int widthDiv = (int) getWidth() - GUI_MAP_SECTION_SIZE * 2;
			int heightDiv = (int) getHeight() - GUI_MAP_SECTION_SIZE * 2;
			
			for(int x = 0; x < GUI_MAP.length; x++) {
				for(int y = 0; y < GUI_MAP[x].length; y++) {
					//@formatter:off
					g2d.drawImage(
							GUI_MAP[x][y],
							(x == 0 ? 0 : GUI_MAP_SECTION_SIZE) + (x == 2 ? widthDiv : 0),
							(y == 0 ? 0 : GUI_MAP_SECTION_SIZE) + (y == 2 ? heightDiv : 0),
							x == 1 ? widthDiv : GUI_MAP_SECTION_SIZE,
							y == 1 ? heightDiv : GUI_MAP_SECTION_SIZE,
							null
					);
					//@formatter:on
				}
			}
			
			g2d.dispose();
			this.backgroundCache = bi;
		}
		return this.backgroundCache;
	}
	
	public void render(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		g2d.drawImage(getImage(), getX(), getY(), null);
		for(HudElement he : elements) {
			he.render(g2d, 0, 0, camW, camH);
		}
	}
	
	static {
		GUI_MAP = new BufferedImage[3][3];
		for(int x = 0; x < GUI_MAP.length; x++) {
			for(int y = 0; y < GUI_MAP[0].length; y++) {
				GUI_MAP[x][y] = Images.getSubImage(GUI_MAP_IMAGE, GUI_MAP_SECTION_SIZE, x, y);
			}
		}
	}
}
