package com.thebrenny.jumg.level.tiles;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.thebrenny.jumg.util.Images;
import com.thebrenny.jumg.util.Logger;
import com.thebrenny.jumg.util.StringUtil;

public class Tile implements ITile {
	private static final HashMap<Integer, Tile> TILES = new HashMap<Integer, Tile>();
	public static BufferedImage TILE_MAP = Images.getImage("tile_map");
	public static int TILE_SIZE = 32;
	
	protected final String name;
	protected final int id;
	protected BufferedImage image;
	protected final int tileX;
	protected final int tileY;
	protected boolean isSolid;
	
	public Tile(String name, int id, int tileX, int tileY) {
		this.name = name;
		this.id = id;
		this.tileX = tileX;
		this.tileY = tileY;
		this.image = Images.getSubImage(Tile.TILE_MAP, Tile.TILE_SIZE, this.tileX, this.tileY);
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
	public BufferedImage getImage() {
		return image;
	}
	public boolean isSolid() {
		return isSolid;
	}
	
	public Tile setSolid(boolean solid) {
		this.isSolid = solid;
		return this;
	}
	
	public String toString() {
		return StringUtil.insert("{0}[name: {1}, id: {2}, solid: {3}]", getClass().getName(), this.getName(), this.getID(), this.isSolid());
	}
	
	public static boolean registerTile(Tile t) {
		Logger.log("Registering tile [{0}] with id [{1}].", t.name, t.id);
		if(TILES.get(t.id) != null) Logger.log("OH NO! This tile seems to be registered already! The ID is being used by [" + TILES.get(t.id).name + "].");
		return TILES.putIfAbsent(t.id, t) == null;
	}
	public static Tile getTile(int id) {
		return TILES.get(id);
	}
	public static Tile getTile(String name) {
		for(Tile t : TILES.values())
			if(t.name.equals(name)) return t;
		return null;
	}
	public static void setTileMap(String tileMap, int tileSize) {
		Tile.TILE_MAP = Images.getImage(tileMap);
		Tile.TILE_SIZE = tileSize;
	}
}
