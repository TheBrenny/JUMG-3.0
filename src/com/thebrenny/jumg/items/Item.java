package com.thebrenny.jumg.items;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.thebrenny.jumg.util.Images;
import com.thebrenny.jumg.util.Logger;
import com.thebrenny.jumg.util.StringUtil;

public abstract class Item {
	private static final HashMap<Integer, Item> ITEMS = new HashMap<Integer, Item>();
	public static BufferedImage ITEM_MAP = Images.getImage("item_map");
	public static int ITEM_SIZE = 32;
	
	protected final String name;
	protected final int id;
	protected int mapX;
	protected int mapY;
	protected int mapW;
	protected int mapH;
	protected BufferedImage imageCache;
	protected float anchorX;
	protected float anchorY;
	@Deprecated
	protected int inventoryWidth = 1;
	@Deprecated
	protected int inventoryHeight = 1;
	
	public Item(String name, int id, int mapX, int mapY) {
		this.name = name;
		this.id = id;
		this.mapX = mapX;
		this.mapY = mapY;
		this.mapW = ITEM_SIZE;
		this.mapH = ITEM_SIZE;
		this.anchorX = getMapWidth() / 2;
		this.anchorY = getMapHeight() / 2;
	}
	public Item setMapDimensions(Integer width, Integer height) {
		if(width != null) this.mapW = width;
		if(height != null) this.mapH = height;
		requestImageUpdate();
		return this;
	}
	public Item setAnchor(Float anchorX, Float anchorY) {
		if(anchorX != null) this.anchorX = anchorX;
		if(anchorY != null) this.anchorY = anchorY;
		requestImageUpdate();
		return this;
	}
	@Deprecated
	public Item setInventorySize(Integer width, Integer height) {
		if(width != null) this.inventoryWidth = width;
		if(height != null) this.inventoryHeight = height;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public String getDisplayName() {
		return StringUtil.normalizeCase(this.name, true).replaceAll("_", " ");
	}
	public int getID() {
		return id;
	}
	public int getMapX() {
		return mapX;
	}
	public int getMapY() {
		return mapY;
	}
	public int getMapWidth() {
		return mapW;
	}
	public int getMapHeight() {
		return mapH;
	}
	@Deprecated
	public int getInventoryWidth() {
		return inventoryWidth;
	}
	@Deprecated
	public int getInventoryHeight() {
		return inventoryHeight;
	}
	public BufferedImage getImage() {
		if(imageCache == null) {
			this.imageCache = Images.getSubImage(ITEM_MAP, ITEM_SIZE, ITEM_SIZE, mapX, mapY, mapW, mapH);
		}
		return imageCache;
	}
	
	public void requestImageUpdate() {
		this.imageCache = null;
	}
	
	public static boolean registerItem(Item i) {
		Logger.log("Registering item [{0}] with ID [{1}].", i.name, i.id);
		if(ITEMS.get(i.id) != null) Logger.log("OH NO! This item seems to be registered already! The ID is being used by [" + ITEMS.get(i.id) + "].");
		return ITEMS.putIfAbsent(i.id, i) == null;
	}
	public static Item getItem(int id) {
		return ITEMS.get(id);
	}
	public static Item getItem(String name) {
		for(Item i : ITEMS.values())
			if(i.name.equals(name)) return i;
		return null;
	}
	public static void setItemMap(String itemMap, int itemSize) {
		Item.ITEM_MAP = Images.getImage(itemMap);
		Item.ITEM_SIZE = itemSize;
	}
}
