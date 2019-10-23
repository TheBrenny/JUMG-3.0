package com.thebrenny.jumg.gui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

// FIXME: Fix the addToList and removeFromList overrides!
public class GuiListSelector extends GuiList {
	private static final long serialVersionUID = 1L;
	protected boolean multiSelect;
	
	public GuiListSelector(float x, float y, float width, float height, int capacity, boolean multiSelect) {
		super(x, y, width, height, capacity);
		super.run = new Runnable() {
			public void run() {
				//GuiListSelector.this.itemClicked(Handler.mousePoint);
			}
		};
		this.multiSelect = multiSelect;
	}
	
	public GuiListSelector addToList(String ... items) {
		float itemWidth = (float) (this.getWidth() - 7);
		float itemHeight = (float) ((this.getHeight() - 7) / this.items.length);
		for(int i = 0; i < items.length; i++) {
			this.items[i] = new GuiListSelectorItem(0, itemHeight * i, itemWidth, itemHeight, items[i]);
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
		if(!(this.items[index] instanceof GuiListSelectorItem)) return;
		GuiListSelectorItem item = (GuiListSelectorItem) this.items[index];
		if(this.multiSelect) item.selected(!item.isSelected());
		else {
			for(int i = 0; i < items.length; i++) {
				if(items[i] == null || !(items[i] instanceof GuiListSelectorItem)) continue;
				((GuiListSelectorItem) items[i]).selected(index == i);
			}
		}
		this.requestNewImage();
	}

	public int[] getSelectedIndexes() {
		int[] indexes = new int[0];
		for(int i = 0; i < items.length; i++) {
			if(items[i] == null || !(items[i] instanceof GuiListSelectorItem)) continue;
			if(((GuiListSelectorItem) items[i]).isSelected()) {
				indexes = Arrays.copyOf(indexes, indexes.length+1);
				indexes[indexes.length-1] = i;
			}
		}
		return indexes;
	}

	public GuiListSelectorItem[] getSelectedItems() {
		int[] indexes = getSelectedIndexes();
		GuiListSelectorItem[] items = new GuiListSelectorItem[indexes.length];
		for(int i = 0; i < indexes.length; i++) {
			items[i] = (GuiListSelectorItem) getItem(i);
		}
		return items;
	}
	
	public class GuiListSelectorItem extends GuiList.GuiListItem {
		private static final long serialVersionUID = 1L;
		protected boolean selected = false;
		
		public GuiListSelectorItem(float x, float y, float width, float height, String string) {
			super(x, y, width, height, string);
		}
		
		public void selected(boolean selected) {
			this.selected = selected;
		}
		public boolean isSelected() {
			return this.selected;
		}
		
		public void highlightMe(Graphics2D g2d, int xOffset, int yOffset) {
			g2d.setColor(Color.CYAN);
			g2d.fillRect((int) getX() + xOffset, (int) getY() + yOffset, (int) this.getWidth(), (int) this.getHeight());
		}
		public void render(Graphics2D g2d, int xOffset, int yOffset) {
			if(selected) highlightMe(g2d, xOffset, yOffset);
			super.render(g2d, xOffset, yOffset);
		}
	}
}
