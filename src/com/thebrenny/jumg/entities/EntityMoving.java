package com.thebrenny.jumg.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.thebrenny.jumg.entities.ai.pathfinding.Node;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.util.Angle;
import com.thebrenny.jumg.util.Angle.AngleSpeed;
import com.thebrenny.jumg.util.VectorUtil.Ray;

public abstract class EntityMoving extends Entity {
	protected AngleSpeed nextMove;
	protected AngleSpeed lastMove;
	protected float currentSpeed;
	protected float maxSpeed;
	
	public EntityMoving(String name, int id, float x, float y, int tileMapX, int tileMapY, float speed) {
		super(name, id, x, y, tileMapX, tileMapY);
		this.currentSpeed = 0;
		this.maxSpeed = speed;
		this.lastMove = this.getAngle().getAngleSpeed(0);
		this.nextMove = this.getAngle().getAngleSpeed(0);
	}
	
	public float getSpeed() {
		return this.currentSpeed;
	}
	public float getMaxSpeed() {
		return this.maxSpeed;
	}
	public AngleSpeed getLastMove() {
		return this.lastMove;
	}
	public AngleSpeed getNextMove() {
		return this.nextMove;
	}
	
	public void addMovementMove() {
		addMove(this.getAngle().getAngleSpeed(this.maxSpeed));
	}
	public void addMove(AngleSpeed speed) {
		this.nextMove.addAngleSpeed(speed);
	}
	public void addMovementTo(Node node) {
		this.setAngle(Angle.getAngle(this.getAnchoredTileLocation(), node.getPoint()));
		addMovementMove();
	}
	public void move() {
		this.nextMove = collisionTest(this.nextMove);
		
		this.boundingBox.x += (this.nextMove.getXSpeed() * Tile.TILE_SIZE - 0.0001F * (this.nextMove.getXSpeed() < 0 ? -1 : 1));
		this.boundingBox.y += (this.nextMove.getYSpeed() * Tile.TILE_SIZE - 0.0001F * (this.nextMove.getYSpeed() < 0 ? -1 : 1));
		
		this.lastMove = new AngleSpeed(this.nextMove);
		this.nextMove = this.getAngle().getAngleSpeed(0);
	}
	public AngleSpeed collisionTest(AngleSpeed as) {
		Ray ray = new Ray(new Point2D.Float(this.getAnchoredTileX(), this.getAnchoredTileY()), as);
		ray = getLevel().castRay(ray);
		as = AngleSpeed.getAngleSpeed(ray);
		
		return as;
	}
	
	public void renderDebug(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		int vectorScale = 100;
		AngleSpeed as = getLastMove();
		g2d.setColor(Color.GREEN);
		g2d.drawLine((int) (getAnchoredX() - camX), (int) (getAnchoredY() - camY), (int) (getAnchoredX() + as.getXSpeed() * vectorScale - camX), (int) (getAnchoredY() + as.getYSpeed() * vectorScale - camY));
	}
}