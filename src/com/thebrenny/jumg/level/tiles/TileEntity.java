package com.thebrenny.jumg.level.tiles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.util.Angle;
import com.thebrenny.jumg.util.Images;

public abstract class TileEntity extends Entity {
	public static BufferedImage TILE_MAP = Images.getImage("tile_entity_map");
	public static int TILE_SIZE = 32;
	
	public TileEntity(String name, int id, float x, float y, int mapX, int mapY) {
		super(name, id, x, y, mapX, mapY);
		anchorX = TILE_SIZE / 2;
		anchorY = TILE_SIZE / 2;
		setAngle(new Angle(Angle.NORTH));
	}
	
	public BufferedImage getRawImage() {
		if(this.image == null) this.image = Images.getSubImage(TileEntity.TILE_MAP, TileEntity.TILE_SIZE, mapX, mapY);
		return this.image;
	}
	
	public abstract void tick();
	public void render(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		g2d.drawImage(getImage(), (int) (getX() - camX), (int) (getY() - camY), null);
	}
	public void renderDebug(Graphics2D g2d, long camX, long camY, int camW, int camH) {}
	
	public static void setTileEntityMap(String tileMap, int tileSize) {
		TileEntity.TILE_MAP = Images.getImage(tileMap);
		TileEntity.TILE_SIZE = tileSize;
	}
}
