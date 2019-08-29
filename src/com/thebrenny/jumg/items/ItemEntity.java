package com.thebrenny.jumg.items;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.entities.EntityMoving;
import com.thebrenny.jumg.util.Angle;
import com.thebrenny.jumg.util.MathUtil;
import com.thebrenny.jumg.util.TimeUtil;

public class ItemEntity extends EntityMoving {
	private static final float WAVE = (float) Math.PI * 2;
	public static final float BOBBING_TIMER_SHIFT = 0.01F * WAVE;
	public static final float BOBBING_OFFSET = Entity.ENTITY_SIZE / 8;
	public static final float ENTITY_MAGNET_RADIUS = 7F;
	public static final float ENTITY_MAGNET_FACTOR = 10F;
	public static final float ENTITY_MAGNET_MULTIPLIER = 5F;
	public static final float ENTITY_DECELERATE_SPEED = 0.07F;
	public static final long NO_PICKUP_TIMER = 1000L; // this is in milli seconds!!
	
	protected ItemStack itemStack;
	protected float bobbingTimer;
	protected float decelerate;
	protected long dropTime;
	
	public ItemEntity(ItemStack itemStack, float x, float y) {
		super("genUID:" + itemStack.getItem().getName(), itemStack.getItem().getID(), x, y, 0, 0, 3);
		this.decelerate = ENTITY_DECELERATE_SPEED;
		this.itemStack = itemStack;
		setSize((float) itemStack.getItem().getMapWidth(), (float) itemStack.getItem().getMapHeight());
		setAnchor(getWidth() / 2, getHeight() / 2);
		this.currentSpeed = 0;
		this.dropTime = 0;
	}
	
	public ItemEntity setAngle(Angle angle) {
		return (ItemEntity) super.setAngle(angle);
	}
	
	public ItemEntity dropItem() {
		this.dropTime = TimeUtil.getEpoch();
		this.currentSpeed = this.maxSpeed;
		return this;
	}
	public boolean canPickUp() {
		return TimeUtil.getElapsed(this.dropTime) >= NO_PICKUP_TIMER;
	}
	
	public void tick() {
		bobbingTimer = MathUtil.wrap(0, bobbingTimer + BOBBING_TIMER_SHIFT, WAVE);
		
		if(getSpeed() > 0) {
			this.currentSpeed -= decelerate;
			addMove(getAngle().getAngleSpeed(getSpeed()));
			move();
		}
	}
	public void renderDebug(Graphics2D g2d, long camX, long camY, int camW, int camH) {}
	
	public float getY() {
		return (float) (super.getY() + Math.sin(bobbingTimer) * BOBBING_OFFSET);
	}
	public ItemStack getItemStack() {
		return this.itemStack;
	}
	public BufferedImage getImage() {
		return getRawImage();
	}
	public BufferedImage getRawImage() {
		return itemStack.getItem().getImage();
	}
}
