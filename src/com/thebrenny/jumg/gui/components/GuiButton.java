package com.thebrenny.jumg.gui.components;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.util.Images;

public class GuiButton extends Component {
	private static final long serialVersionUID = 1L;
	public static final BufferedImage BUTTON_MAP_IMAGE = Images.getImage("gui_buttons");
	public static final int GUI_MAP_SECTION_COUNT = 3;
	public static final int GUI_MAP_SECTION_SIZE = 11;
	public static final BufferedImage[][] GUI_MAP;
	
	public GuiLabel label;
	public BufferedImage biCache;
	
	public GuiButton(float x, float y, float width, float height, String str, Runnable run) {
		super(x, y, width, height, run);
		this.label = new GuiLabel((float) getWidth() / 2, (float) getHeight() / 2, str, GuiLabel.BUTTON_FONT).allign(GuiLabel.ALLIGN_CENTRE);
	}
	
	public Component resize(float width, float height) {
		super.resize(width, height);
		requestNewImage();
		return this;
	}
	
	public void doClick() {
		super.doClick();
		requestNewImage();
	}
	public Component setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		requestNewImage();
		return this;
	}
	
	public void requestNewImage() {
		this.biCache = null;
	}
	
	public BufferedImage getImage() {
		if(biCache == null) {
			BufferedImage bi = new BufferedImage((int) getWidth(), (int) getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();
			
			int xOff = this.pressed || !this.enabled ? GUI_MAP_SECTION_COUNT * 2 : this.hovering ? GUI_MAP_SECTION_COUNT * 1 : GUI_MAP_SECTION_COUNT * 0;
			int widthDiv = (int) getWidth() - GUI_MAP_SECTION_SIZE * 2;
			int heightDiv = (int) getHeight() - GUI_MAP_SECTION_SIZE * 2;
			
			for(int x = 0; x < GUI_MAP_SECTION_COUNT; x++) {
				for(int y = 0; y < GUI_MAP_SECTION_COUNT; y++) {
					//@formatter:off
					g2d.drawImage(
							GUI_MAP[x + xOff][y],
							(x == 0 ? 0 : GUI_MAP_SECTION_SIZE) + (x == 2 ? widthDiv : 0),
							(y == 0 ? 0 : GUI_MAP_SECTION_SIZE) + (y == 2 ? heightDiv : 0),
							x == 1 ? widthDiv : GUI_MAP_SECTION_SIZE,
							y == 1 ? heightDiv : GUI_MAP_SECTION_SIZE,
							null
					);
					//@formatter:on
				}
			}
			
			label.render(g2d, 0, 0);
			g2d.dispose();
			biCache = bi;
		}
		return biCache;
	}
	
	public void render(Graphics2D g2d, long camX, long camY) {
		g2d.drawImage(getImage(), (int) getX(), (int) getY(), null);
	}
	
	static {
		GUI_MAP = new BufferedImage[GUI_MAP_SECTION_COUNT * 3][GUI_MAP_SECTION_COUNT];
		for(int x = 0; x < GUI_MAP.length; x++) {
			for(int y = 0; y < GUI_MAP[0].length; y++) {
				GUI_MAP[x][y] = Images.getSubImage(BUTTON_MAP_IMAGE, GUI_MAP_SECTION_SIZE, x, y);
			}
		}
	}
}
