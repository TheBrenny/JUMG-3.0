package com.thebrenny.jumg.gui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class GuiListSelector extends Component {
	private static final long serialVersionUID = 1L;
	public GuiListItem[] items;
	protected boolean multiSelect;
	
	public GuiListSelector(float x, float y, float width, float height, boolean multiSelect, int capacity) {
		super(x, y, width, height);
		super.run = new Runnable() {
			public void run() {
				//GuiListSelector.this.itemClicked(Handler.mousePoint);
			}
		};
		items = new GuiListItem[capacity];
		this.multiSelect = multiSelect;
	}
	
	public GuiListSelector addToList(String ... items) {
		float itemWidth = (float) (this.getWidth() - 7);
		float itemHeight = (float) ((this.getHeight() - 7) / this.items.length);
		for(int i = 0; i < items.length; i++) {
			this.items[i] = new GuiListItem(0, itemHeight * i, itemWidth, itemHeight, items[i]);
		}
		this.requestNewImage();
		return this;
	}
	
	public void changeMe(boolean hover, boolean click, Point2D.Float mousePoint) {
		if(super.changeMe(hover, click, mousePoint)) itemClicked(mousePoint);
	}
	
	public void itemClicked(Point2D.Float mousePoint) {
		mousePoint.setLocation(mousePoint.x - getX(), mousePoint.y - getY());
		for(int index = 0; index < items.length; index++) {
			if(items[index] == null) continue;
			if(items[index].contains(mousePoint)) {
				selectItem(index);
				break;
			}
		}
		this.requestNewImage();
	}
	public void selectItem(int index) {
		if(this.multiSelect) items[index].selected(!items[index].isSelected());
		else {
			for(int i = 0; i < items.length; i++) {
				if(items[i] == null) continue;
				if(index == i) {
					items[i].selected(index == i);
					break;
				}
			}
		}
		this.requestNewImage();
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
		protected boolean selected = false;
		
		public GuiListItem(float x, float y, float width, float height, String string) {
			super(x, y, string);
			super.resize(width, height);
			this.allign(ALLIGN_HORIZONTAL_LEFT, ALLIGN_VERTICAL_CENTRE);
			this.setColor(Color.WHITE);
		}
		
		public void selected(boolean selected) {
			this.selected = selected;
		}
		public boolean isSelected() {
			return this.selected;
		}
		
		public float getMaxAllignedY() {
			float yOff = getAllignment()[1].equalsIgnoreCase(ALLIGN_VERTICAL_BOTTOM) ? 1.0F : getAllignment()[1].equalsIgnoreCase(ALLIGN_VERTICAL_CENTRE) ? 0.5F : 0.0F;
			return (float) (getY() + (getMaxHeight() - getSingleHeight()) * yOff);
		}
		public void drawMe(Graphics2D g2d, int xOffset, int yOffset) {
			g2d.setColor(Color.CYAN);
			g2d.fillRect((int) getX() + xOffset, (int) getY() + yOffset, (int) this.getWidth(), (int) this.getHeight());
		}
		public void render(Graphics2D g2d, int xOffset, int yOffset) {
			if(selected) drawMe(g2d, xOffset, yOffset);
			super.render(g2d, xOffset, yOffset);
		}
	}
}
