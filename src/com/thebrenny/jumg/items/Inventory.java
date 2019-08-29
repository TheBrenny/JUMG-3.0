package com.thebrenny.jumg.items;

import com.thebrenny.jumg.util.Logger;

public class Inventory {
	protected IHasInventory owner;
	protected int width;
	protected int height;
	protected ItemStack[][] items;
	
	public Inventory(IHasInventory owner, int width, int height) {
		this.owner = owner;
		this.width = width;
		this.height = height;
		this.items = Inventory.createEmptyInventory(width, height);
	}
	public Inventory setInventory(ItemStack[][] items) {
		int w = items.length;
		int h = items[0].length;
		if(w != width || h != height) Logger.log("Be careful! You're trying to set an inventory with a different size: ({0}, {1}) != ({2}, {3}).", w, h, width, height);
		this.items = items;
		return this;
	}
	
	public boolean contains(ItemStack item) {
		int count = item.getCount();
		ItemStack is;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				is = getItems()[x][y];
				if(is.getItem() == item.getItem()) count -= is.getCount();
				if(count <= 0) return true;
			}
		}
		return false;
	}
	
	/**
	 * Attempts to add an ItemStack to the first acceptable slot. A slot is
	 * considered acceptable if:
	 * <ul>
	 * <li>the ItemStack in the slot is of the same Item, but not a maximum
	 * stack, or</li>
	 * <li>the slot is empty.</li>
	 * </ul>
	 * This method modifies the passed ItemStack and also returns the amount of
	 * items remaining in the stack, or -1 if the operation failed.
	 * 
	 * @param is
	 * @return The amount of items that can't be added to the inventory during
	 *         the operation.
	 */
	public int addItem(ItemStack is) {
		for(int x = 0; x < getWidth() && is.getCount() > 0; x++) {
			for(int y = 0; y < getHeight() && is.getCount() > 0; y++) {
				if(getItems()[x][y] == null) {
					getItems()[x][y] = is.cloneStack();
					getItems()[x][y].setCount(0);
				}
				getItems()[x][y].merge(is);
			}
		}
		
		return is.getCount();
	}
	/**
	 * Attempts to add an ItemStack to the inventory at location (x, y) and
	 * alters the passed ItemStack if successful. Returns the amount of items
	 * remaining in the stack, or -1 if the operation failed.
	 * 
	 * @param is
	 * @param x
	 * @param y
	 * @return
	 */
	public int addItem(ItemStack is, int x, int y) {
		if(x == -1 || y == -1) return addItem(is);
		
		ItemStack current = getItems()[x][y];
		current.merge(is);
		return is.getCount();
	}
	
	public boolean removeItem(ItemStack is) {
		if(!contains(is)) return false;
		
		for(int x = 0; x <= getWidth() && is.getCount() > 0; x++) {
			for(int y = 0; y <= getHeight() && is.getCount() > 0; y++) {
				if(getItems()[x][y] == null) continue;
				
				if(is.getItem() == getItems()[x][y].getItem()) is.setCount(getItems()[x][y].consume(is.getCount()));
				if(is.getCount() <= 0) return true;
			}
		}
		
		Logger.log("Uh oh... This is strange... " + is.getCount());
		return false; // we know we will achieve the mission, so this shouldn't really ever be hit...
	}
	
	/**
	 * Clones the inventory list by creating a new array and new ItemStacks.
	 * Theoretically, alterations to the original Inventory should then not be
	 * made to the cloned inventory. Useful for testing and resetting.
	 * 
	 * @return The cloned {@code Inventory} of {@code this}.
	 */
	public ItemStack[][] cloneInventory() {
		ItemStack[][] clone = new ItemStack[getItems().length][getItems()[0].length];
		
		for(int x = 0; x < clone.length; x++) {
			for(int y = 0; y < clone[0].length; y++) {
				//try {
				ItemStack i = getItems()[x][y];
				clone[x][y] = i.cloneStack();
				//clone[x][y] = getItems()[x][y].getClass().getConstructor(getItems()[x][y].getClass()).newInstance(getItems()[x][y]);
				//} catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				//	e.printStackTrace();
				//}
			}
		}
		
		return clone;
	}
	
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	public synchronized ItemStack[][] getItems() {
		return this.items;
	}
	
	public static ItemStack[][] createEmptyInventory(int width, int height) {
		ItemStack[][] inv = new ItemStack[width][height];
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				inv[x][y] = null;
		return inv;
	}
}
