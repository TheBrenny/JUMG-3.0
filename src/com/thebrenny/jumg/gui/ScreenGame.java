package com.thebrenny.jumg.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.hud.HudManager;
import com.thebrenny.jumg.level.Level;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.level.tiles.TileEntity;
import com.thebrenny.jumg.util.Angle.AngleSpeed;
import com.thebrenny.jumg.util.MathUtil;

public class ScreenGame extends Screen {
	protected Level level;
	protected HudManager hud;
	protected Entity entityToFollow;
	
	public ScreenGame(Level level) {
		this(level, new HudManager(getWidth(), getHeight()));
	}
	public ScreenGame(Level level, HudManager hudManager) {
		this.level = level;
		this.hud = hudManager;
	}
	
	public Level getLevel() {
		return this.level;
	}
	
	public void moveCamera() {
		if(entityToFollow != null) {
			long camX = (long) (entityToFollow.getAnchoredX() - (getWidth() / 2));
			long camY = (long) (entityToFollow.getAnchoredY() - (getHeight() / 2));
			long camMoveX = (long) ((camX - Screen.getCameraX()) * Screen.getCameraSpeed());
			long camMoveY = (long) ((camY - Screen.getCameraY()) * Screen.getCameraSpeed());
			setCameraX((long) MathUtil.clamp(-getMapWidth() / 2, getCameraX() + camMoveX, (getMapWidth() / 2) - getWidth()));
			setCameraY((long) MathUtil.clamp(-getMapHeight() / 2, getCameraY() + camMoveY, (getMapHeight() / 2) - getHeight()));
		}
	}
	
	public long getMapWidth() {
		return (long) (this.getLevel().getMapWidth() * Tile.TILE_SIZE);
	}
	public long getMapHeight() {
		return (long) (this.getLevel().getMapHeight() * Tile.TILE_SIZE);
	}
	
	public void setEntityToFollow(Entity e) {
		this.entityToFollow = e;
		hud.setPrimaryEntity(e);
	}
	
	public boolean isMenuOpen() {
		return hud.isMenuOpen();
	}
	
	public void mouseEvent(Point mousePoint, boolean mouseDown) {
		hud.mouseEvent(mousePoint, mouseDown);
	}
	
	public void tick() {
		level.tick();
		hud.tick();
	}
	public void render(Graphics2D g2d) {
		level.render(g2d, Screen.getCameraX(), Screen.getCameraY(), getWidth(), getHeight());
		hud.render(g2d, Screen.getCameraX(), Screen.getCameraY(), getWidth(), getHeight());
	}
	public int renderDebug(Graphics2D g2d, int line) {
		line = super.renderDebug(g2d, line);
		line = renderDebugEntity(g2d, line);
		line = renderDebugTileEntity(g2d, line);
		return line;
	}
	public int renderDebugEntity(Graphics2D g2d, int line) {
		Graphics2D g2d2 = (Graphics2D) g2d.create();
		g2d2.setFont(new Font("Consolas", Font.PLAIN, 10));
		FontMetrics mets = g2d2.getFontMetrics();
		
		int eX, eY;
		final long cx = Screen.getCameraX();
		final long cy = Screen.getCameraY();
		AngleSpeed as;
		Rectangle2D bb;
		Graphics2D eg2d;
		
		Entity[] ents = level.getEntitiesCache();
		for(Entity e : ents) {
			if(canCameraSeeEntity(e)) {
				// draw bounding box
				bb = e.getBoundingBox();
				g2d2.drawRect((int) (bb.getX() - cx), (int) (bb.getY() - cy), (int) bb.getWidth(), (int) bb.getHeight());
				
				// draw angle
				as = e.getAngle().getAngleSpeed(Entity.ENTITY_SIZE / 2);
				eX = (int) e.getAnchoredX();
				eY = (int) e.getAnchoredY();
				g2d2.drawLine((int) (eX - cx), (int) (eY - cy), (int) (eX + as.getXSpeed() - cx), (int) (eY + as.getYSpeed() - cy));
				
				// draw name
				g2d2.drawString(e.getName(), (int) (eX - (mets.stringWidth(e.getName()) / 2) - cx), (int) (bb.getY() + bb.getHeight() + mets.getMaxAscent() - cy));
				
				// its own data
				e.renderDebug((eg2d = (Graphics2D) g2d.create()), cx, cy, getWidth(), getHeight());
				eg2d.dispose();
			}
		}
		
		g2d2.dispose();
		return line;
	}
	public int renderDebugTileEntity(Graphics2D g2d, int line) {
		Graphics2D g2d2 = (Graphics2D) g2d.create();
		g2d2.setFont(new Font("Consolas", Font.PLAIN, 10));
		FontMetrics mets = g2d2.getFontMetrics();
		
		int eX, eY;
		final long cx = Screen.getCameraX();
		final long cy = Screen.getCameraY();
		AngleSpeed as;
		Graphics2D eg2d;
		
		TileEntity[] tents = level.getTileEntitiesCache();
		for(TileEntity te : tents) {
			if(canCameraSeeTile(te.getTileX(), te.getTileY())) {
				eX = (int) te.getX();
				eY = (int) te.getY();
				
				// draw bounding box
				g2d2.drawRect((int) (eX - cx), (int) (eY - cy), (int) te.getWidth(), (int) te.getHeight());
				
				// draw angle
				as = te.getAngle().getAngleSpeed(Tile.TILE_SIZE / 2);
				g2d2.drawLine((int) (te.getAnchoredX() - cx), (int) (te.getAnchoredY() - cy), (int) (te.getAnchoredX() + as.getXSpeed() - cx), (int) (te.getAnchoredY() + as.getYSpeed() - cy));
				
				// draw name
				g2d2.drawString(te.getName(), (int) (eX - (mets.stringWidth(te.getName()) / 2) - cx), (int) (eY + te.getHeight() + mets.getMaxAscent() - cy));
				
				// its own data
				te.renderDebug((eg2d = (Graphics2D) g2d.create()), cx, cy, getWidth(), getHeight());
				eg2d.dispose();
			}
		}
		
		g2d2.dispose();
		return line;
	}
}
