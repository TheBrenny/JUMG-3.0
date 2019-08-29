package com.thebrenny.jumg.entities;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.level.Level;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.util.Angle;
import com.thebrenny.jumg.util.Images;
import com.thebrenny.jumg.util.MathUtil;
import com.thebrenny.jumg.util.StringUtil;
import com.thebrenny.jumg.util.VectorUtil.Ray;

public abstract class Entity {
	public static BufferedImage ENTITY_MAP = Images.getImage("entity_map");
	public static int ENTITY_SIZE = 32;
	
	protected final String name;
	protected final int id;
	protected Rectangle2D.Float boundingBox;
	protected float anchorX;
	protected float anchorY;
	protected int mapX;
	protected int mapY;
	protected BufferedImage image;
	protected Angle angle;
	protected Level level;
	
	public Entity(String name, int id, float x, float y, int mapX, int mapY) {
		if(name == null) name = "genUID";
		if(name.startsWith("genUID")) {
			if(name.contains(":")) name = name.substring(name.indexOf(":") + 1);
			else if(name.equals("genUID")) name = null;
			name = StringUtil.getNextUID(name);
		}
		
		this.name = name;
		this.id = id;
		this.boundingBox = new Rectangle2D.Float(x * Tile.TILE_SIZE, y * Tile.TILE_SIZE, Entity.ENTITY_SIZE, Entity.ENTITY_SIZE);
		this.anchorX = ENTITY_SIZE / 2;
		this.anchorY = ENTITY_SIZE / 2;
		this.mapX = mapX;
		this.mapY = mapY;
		setAngle(new Angle(Angle.NORTH));
	}
	
	public Entity setAngle(float angle) {
		this.angle.setAngle(angle);
		return this;
	}
	public Entity setAngle(Angle angle) {
		this.angle = angle;
		return this;
	}
	public Entity setSize(Float width, Float height) {
		if(width != null) this.boundingBox.width = width;
		if(height != null) this.boundingBox.height = height;
		return this;
	}
	public Entity setAnchor(Float xOffset, Float yOffset) {
		if(xOffset != null) this.anchorX = xOffset;
		if(yOffset != null) this.anchorY = yOffset;
		return this;
	}
	public Entity setLevel(Level level) {
		this.level = level;
		return this;
	}
	
	public String getName() {
		return this.name;
	}
	public int getID() {
		return this.id;
	}
	
	public float getX() {
		return (float) this.boundingBox.getX();
	}
	public float getY() {
		return (float) this.boundingBox.getY();
	}
	public float getAnchoredX() {
		return this.getX() + this.anchorX;
	}
	public float getAnchoredY() {
		return this.getY() + this.anchorY;
	}
	public float getTileX() {
		return this.getX() / (float) Tile.TILE_SIZE;
	}
	public float getTileY() {
		return this.getY() / (float) Tile.TILE_SIZE;
	}
	public float getAnchoredTileX() {
		return this.getAnchoredX() / (float) Tile.TILE_SIZE;
	}
	public float getAnchoredTileY() {
		return this.getAnchoredY() / (float) Tile.TILE_SIZE;
	}
	public float getWidth() {
		return (float) this.boundingBox.getWidth();
	}
	public float getHeight() {
		return (float) this.boundingBox.getHeight();
	}
	public Rectangle2D.Float getBoundingBox() {
		return this.boundingBox;
	}
	public Angle getAngle() {
		return this.angle;
	}
	public Level getLevel() {
		return this.level;
	}
	public BufferedImage getImage() {
		return this.getAngle().getRotation(getRawImage(), this.anchorX, this.anchorY);
	}
	public BufferedImage getRawImage() {
		if(this.image == null) this.image = Images.getSubImage(Entity.ENTITY_MAP, Entity.ENTITY_SIZE, mapX, mapY);
		return this.image;
	}
	public void requestImageUpdate() {
		this.image = null;
	}
	
	public Tile getTileOn() {
		return getRelativeTile(0, 0);
	}
	public Tile getRelativeTile(float x, float y) {
		float tx = getAnchoredTileX() + x;
		float ty = getAnchoredTileY() + y;
		return level.getTileRelative((int) Math.floor(tx), (int) Math.floor(ty));
	}
	
	public boolean canSee(float x, float y, float w, float h, float distance) {
		Point2D.Float myCoord = new Point2D.Float(this.getAnchoredTileX(), this.getAnchoredTileY());
		Point2D.Float test = new Point2D.Float();
		
		test.setLocation(x, y);
		Ray ray = getLevel().castRay(new Ray(myCoord, Angle.getAngle(this.getAnchoredTileX(), this.getAnchoredTileY(), test.x, test.y), distance));
		if(MathUtil.distanceSqrd(ray.getLocation(), test) < MathUtil.distanceSqrd(ray.getLocation(), ray.getEndLocation())) return true; // needs to change so it tests if the distance to the goal is <= ray's end distance 
		
		test.setLocation(x + w / Tile.TILE_SIZE, y);
		ray = getLevel().castRay(new Ray(myCoord, Angle.getAngle(this.getAnchoredTileX(), this.getAnchoredTileY(), test.x, test.y), distance));
		if(MathUtil.distanceSqrd(ray.getLocation(), test) < MathUtil.distanceSqrd(ray.getLocation(), ray.getEndLocation())) return true;
		
		test.setLocation(x + w / Tile.TILE_SIZE, y + h / Tile.TILE_SIZE);
		ray = getLevel().castRay(new Ray(myCoord, Angle.getAngle(this.getAnchoredTileX(), this.getAnchoredTileY(), test.x, test.y), distance));
		if(MathUtil.distanceSqrd(ray.getLocation(), test) < MathUtil.distanceSqrd(ray.getLocation(), ray.getEndLocation())) return true;
		
		test.setLocation(x, y + h / Tile.TILE_SIZE);
		ray = getLevel().castRay(new Ray(myCoord, Angle.getAngle(this.getAnchoredTileX(), this.getAnchoredTileY(), test.x, test.y), distance));
		if(MathUtil.distanceSqrd(ray.getLocation(), test) < MathUtil.distanceSqrd(ray.getLocation(), ray.getEndLocation())) return true;
		
		return false;
	}
	public boolean canSeeTile(float x, float y, float distance) {
		return canSee(x, y, Tile.TILE_SIZE, Tile.TILE_SIZE, distance);
	}
	public boolean canSeeEntity(Entity e, float distance) {
		return canSee(e.getTileX(), e.getTileY(), e.getWidth(), e.getHeight(), distance);
	}
	
	public abstract void tick();
	public void render(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		g2d.drawImage(getImage(), (int) (getX() - camX), (int) (getY() - camY), null);
	}
	public void renderDebug(Graphics2D g2d, long camX, long camY, int camW, int camH) {}
	
	public static void setEntityMap(String entityMap, int entitySize) {
		Entity.ENTITY_MAP = Images.getImage(entityMap);
		Entity.ENTITY_SIZE = entitySize;
	}
}
