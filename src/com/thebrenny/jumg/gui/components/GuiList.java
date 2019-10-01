package com.thebrenny.jumg.gui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GuiList extends Component {
	private static final long serialVersionUID = 1L;
	public GuiListItem[] items;
	
	// TODO: Make this scrollable!!
	
	public GuiList(float x, float y, float width, float height, int capacity) {
		super(x, y, width, height);
		items = new GuiListItem[capacity];
	}
	
	public GuiList addToList(String ... items) {
		float itemWidth = (float) (this.getWidth() - 7);
		float itemHeight = (float) ((this.getHeight() - 7) / this.items.length);
		for(int i = 0; i < items.length; i++) {
			this.items[i] = new GuiListItem(0, itemHeight * i, itemWidth, itemHeight, items[i]);
		}
		this.requestNewImage();
		return this;
	}
	
	public GuiListItem getItem(int index) {
		if(index < 0 || index >= items.length) throw new IndexOutOfBoundsException();
		return items[index];
	}
	
	public BufferedImage getNewImage() {
		BufferedImage bi = new BufferedImage((int) getWidth(), (int) getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		
		g2d.setColor(new Color(70, 70, 70));
		g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.fillRect(2, 2, bi.getWidth() - 5, bi.getHeight() - 5);
		
		for(GuiListItem l : this.items) {
			if(l == null) continue;
			l.render(g2d, 2, 2);
		}
		
		g2d.dispose();
		return bi;
	}
	
	public class GuiListItem extends GuiLabel {
		private static final long serialVersionUID = 1L;
		
		public GuiListItem(float x, float y, float width, float height, String string) {
			super(x, y, string);
			super.resize(width, height);
			this.align(ALIGN_HORIZONTAL_LEFT, ALIGN_VERTICAL_CENTRE);
			this.setColor(Color.WHITE);
		}
		
		public float getMaxAllignedY() {
			float yOff = getAllignment()[1].equalsIgnoreCase(ALIGN_VERTICAL_BOTTOM) ? 1.0F : getAllignment()[1].equalsIgnoreCase(ALIGN_VERTICAL_CENTRE) ? 0.5F : 0.0F;
			return (float) (getY() + (getMaxHeight() - getSingleHeight()) * yOff);
		}
		public void render(Graphics2D g2d, int xOffset, int yOffset) {
			super.render(g2d, xOffset, yOffset);
		}
	}
}
