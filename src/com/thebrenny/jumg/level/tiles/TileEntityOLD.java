package com.thebrenny.jumg.level.tiles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.level.Level;
import com.thebrenny.jumg.util.Angle;
import com.thebrenny.jumg.util.Images;
import com.thebrenny.jumg.util.StringUtil;

public abstract class TileEntityOLD {
	public static BufferedImage TILE_MAP = Images.getImage("tile_entity_map");
	public static int TILE_SIZE = 32;
	
	protected String uid;
	protected final int id;
	protected BufferedImage image;
	protected float x;
	protected float y;
	protected float anchorX;
	protected float anchorY;
	protected Angle angle;
	private Level level;
	
	public TileEntityOLD(int id, float x, float y, int mapX, int mapY) {
		this.uid = StringUtil.getNextUID(null);
		this.id = id;
		this.x = x;
		this.y = y;
		anchorX = TILE_SIZE / 2;
		anchorY = TILE_SIZE / 2;
		this.image = Images.getSubImage(TileEntityOLD.TILE_MAP, TileEntityOLD.TILE_SIZE, mapX, mapY);
		setAngle(new Angle(Angle.NORTH));
	}
	
	public TileEntityOLD setAnchor(Integer anchorX, Integer anchorY) {
		if(anchorX != null) this.anchorX = anchorX;
		if(anchorY != null) this.anchorY = anchorY;
		return this;
	}
	public TileEntityOLD setAngle(Angle angle) {
		this.angle = angle;
		return this;
	}
	public TileEntityOLD setLevel(Level level) {
		this.level = level;
		return this;
	}
	
	public abstract String getName();
	public int getID() {
		return this.id;
	}
	public float getX() {
		return this.x * TILE_SIZE;
	}
	public float getY() {
		return this.y * TILE_SIZE;
	}
	public float getTileX() {
		return this.x;
	}
	public float getTileY() {
		return this.y;
	}
	public float getAnchoredX() {
		return getX() + this.anchorX;
	}
	public float getAnchoredY() {
		return getY() + this.anchorY;
	}
	public float getAnchoredTileX() {
		return getAnchoredX() / TILE_SIZE;
	}
	public float getAnchoredTileY() {
		return getAnchoredY() / TILE_SIZE;
	}
	public Angle getAngle() {
		return this.angle;
	}
	public BufferedImage getImage() {
		return this.getAngle().getRotation(getRawImage(), anchorX, anchorY);
	}
	public BufferedImage getRawImage() {
		return this.image;
	}
	public Level getLevel() {
		return this.level;
	}
	
	public Tile getTile() {
		return getTileRelative(0, 0);
	}
	public Tile getTileRelative(int x, int y) {
		return level.getTileRelative((int) getTileX() + x, (int) getTileY() + y);
	}
	
	public abstract void tick();
	public void renderDebug(Graphics2D g2d, long camX, long camY, int camW, int camH) {}
	
	public static void setTileEntityMap(String tileMap, int tileSize) {
		TileEntityOLD.TILE_MAP = Images.getImage(tileMap);
		TileEntityOLD.TILE_SIZE = tileSize;
	}
}
