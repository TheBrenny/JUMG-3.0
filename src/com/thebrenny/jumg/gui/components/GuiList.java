package com.thebrenny.jumg.gui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GuiList extends Component {
	private static final long serialVersionUID = 1L;
	protected GuiListItem[] items;
	/**
	 * This counts how many items are actually in the array. When we shift items
	 * right in {@link #requestNewImage()} this tells us when we'll hit the
	 * infinite loop of shifting nulls.
	 */
	protected int itemCount = 0;
	
	// TODO: Make this scrollable!!
	
	public GuiList(float x, float y, float width, float height, int capacity) {
		super(x, y, width, height);
		items = new GuiListItem[capacity];
	}
	
	public GuiList addToList(String ... items) {
		float itemWidth = (float) (this.getWidth() - 7);
		float itemHeight = (float) ((this.getHeight() - 7) / this.items.length);
		for(int i = 0; i < items.length && itemCount < this.items.length; i++) {
			this.items[itemCount] = new GuiListItem(4, 4+itemHeight * itemCount, itemWidth, itemHeight, items[i]);
			itemCount++;
		}
		this.requestNewImage();
		return this;
	}
	public GuiList removeFromList(String ... items) {
		for(int i = 0; i < items.length; i++) {
			for(int j = 0; j < this.items.length; j++) {
				if(items[i] != null && this.items[j] != null && this.items[j].getString(0).equals(items[i])) {
					this.items[i] = null;
					itemCount--;
				}
			}
		}
		this.requestNewImage();
		return this;
	}
	
	public boolean hasItem(String value) {
		return indexOf(value) != -1;
	}
	public int indexOf(String value) {
		for(int i = 0; i < itemCount; i++) {
			if(value.equals(this.items[i].getString(0))) return i;
		}
		return -1;
	}
	public GuiListItem getItem(int index) {
		if(index < 0 || index >= items.length) throw new IndexOutOfBoundsException();
		return items[index];
	}
	public GuiListItem removeItem(int index) {
		GuiListItem item = getItem(index);
		removeItem(item);
		return item;
	}
	public void removeItem(GuiListItem item) {
		int index = -1;
		for(int i = 0; i < itemCount && index == -1; i++) {
			if(item.equals(this.items[i])) index = i;
		}
		
		if(index == -1) throw new IndexOutOfBoundsException();
		
		items[index] = null;
		itemCount--;
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
			l.render(g2d, 0, 0);
		}
		
		g2d.dispose();
		return bi;
	}
	
	public void requestNewImage() {
		for(int i = 0; i < this.items.length - 1 && i < itemCount; i++) {
			if(this.items[i] == null) {
				for(int j = 0; j < this.items.length - 1; j++) {
					this.items[j] = this.items[j + 1];
				}
				this.items[this.items.length - 1] = null;
				i--;
			}
		}
		super.requestNewImage();
	}
	
	public class GuiListItem extends GuiLabel {
		private static final long serialVersionUID = 1L;
		
		public GuiListItem(float x, float y, float width, float height, String string) {
			super(x, y, string);
			super.resize(width, height);
			this.align(ALIGN_HORIZONTAL_LEFT, ALIGN_VERTICAL_CENTRE);
			this.setColor(Color.WHITE);
		}
		
		public void render(Graphics2D g2d, int xOffset, int yOffset) {
			super.render(g2d, xOffset, yOffset);
		}

		public void requestNewImage() {
			super.requestNewImage();
			GuiList.this.requestNewImage();
		}
	}
}
