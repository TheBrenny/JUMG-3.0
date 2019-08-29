package com.thebrenny.jumg.level.structure;

import java.awt.Rectangle;

import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.level.tiles.TileEntity;
import com.thebrenny.jumg.util.Angle;
import com.thebrenny.jumg.util.Logger;
import com.thebrenny.jumg.util.StringUtil;

public abstract class Structure {
	protected static final Entity[] NO_ENTITIES = new Entity[0];
	protected static final TileEntity[] NO_TILE_ENTITIES = new TileEntity[0];
	
	protected final String uid;
	protected final String name;
	protected Rectangle boundingBox;
	protected int anchorOffsetX;
	protected int anchorOffsetY;
	protected Angle angle;
	
	public Structure(String name, int x, int y, int width, int height) {
		this.uid = StringUtil.getNextUID(name);
		this.name = name;
		this.boundingBox = new Rectangle(x, y, width, height);
		this.anchorOffsetX = 0;
		this.anchorOffsetY = 0;
		this.angle = new Angle(Angle.NORTH);
	}
	
	public Structure setAnchorOffset(Integer xOffset, Integer yOffset) {
		this.boundingBox.x += this.anchorOffsetX;
		this.boundingBox.y += this.anchorOffsetY;
		
		if(xOffset != null) this.anchorOffsetX = xOffset;
		if(yOffset != null) this.anchorOffsetY = yOffset;
		
		this.boundingBox.x -= this.anchorOffsetX;
		this.boundingBox.y -= this.anchorOffsetY;
		return this;
	}
	public Structure setAngle(Angle angle) {
		if(angle.getAngle() % 90.0F != 0.0F) {
			Logger.log("Structure [{0}] has a strange angle. It wasn't divisible by 90. This has been fixed.", (Object) getClass().getSimpleName());
			angle.snapAngleTo(90.0F);
		}
		this.angle = angle;
		return this;
	}
	
	public String getUID() {
		return this.uid;
	}
	public String getName() {
		return this.name;
	}
	public int getX() {
		return this.boundingBox.x;
	}
	public int getY() {
		return this.boundingBox.y;
	}
	public int getAnchoredX() {
		return this.getX() + this.anchorOffsetX;
	}
	public int getAnchoredY() {
		return this.getY() + this.anchorOffsetY;
	}
	public int getWidth() {
		return this.boundingBox.width;
	}
	public int getHeight() {
		return this.boundingBox.height;
	}
	public Rectangle getBoundingBox() {
		 return this.boundingBox;
	}
	public Angle getAngle() {
		return this.angle;
	}
	
	public Tile getTile(int x, int y) {
		return getTiles()[x][y];
	}
	public Tile getRelativeTile(int x, int y) {
		return getTile(x + anchorOffsetX, y + anchorOffsetY);
	}
	
	public abstract Tile[][] getTiles();
	public abstract TileEntity[] getTileEntities();
	public abstract Entity[] getEntities();
}
