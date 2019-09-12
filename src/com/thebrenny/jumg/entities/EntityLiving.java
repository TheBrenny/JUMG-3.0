package com.thebrenny.jumg.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.entities.ai.states.State;
import com.thebrenny.jumg.entities.ai.states.StateMachine;

public abstract class EntityLiving extends EntityMoving implements IHealable {
	protected StateMachine<? extends EntityLiving, ? extends State<? extends EntityLiving>> brain;
	protected float health;
	protected float maxHealth;
	protected float hbCacheHealth;
	protected BufferedImage hbCache;
	
	public EntityLiving(String name, int id, float x, float y, int tileMapX, int tileMapY, float speed, float health) {
		super(name, id, x, y, tileMapX, tileMapY, speed);
		this.health = this.maxHealth = health;
	}
	public EntityLiving setBrain(StateMachine<? extends EntityLiving, ? extends State<? extends EntityLiving>> brain) {
		this.brain = brain;
		return this;
	}
	public StateMachine<? extends EntityLiving, ? extends State<? extends EntityLiving>> getBrain() {
		return this.brain;
	}
	
	public float getHealth() {
		return this.health;
	}
	public float getMaxHealth() {
		return this.maxHealth;
	}
	public float heal(float amount) {
		return (this.health += amount);
	}
	public float hurt(float amount) {
		return heal(-amount);
	}
	public void kill() {
		hurt(this.health);
		this.onDeath();
	}
	public void onDeath() {
	}
	public boolean isAlive() {
		return this.health > 0.0F;
	}
	public BufferedImage getHealthBarImage() {
		if(hbCache == null || hbCacheHealth != health) {
			hbCacheHealth = health;
			BufferedImage bi = new BufferedImage((int) getWidth(), 5, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(Color.BLACK);
			g2d.fillRoundRect(0, 0, bi.getWidth(), bi.getHeight(), 2, 2);
			
			g2d.setColor(Color.RED);
			g2d.fillRoundRect(1, 1, bi.getWidth() - 2, bi.getHeight() - 2, 2, 2);
			
			int width = (int) (((bi.getWidth() - 2) / getMaxHealth()) * getHealth());
			g2d.setColor(Color.GREEN);
			g2d.fillRoundRect(1, 1, width, bi.getHeight() - 2, 2, 2);
			
			g2d.dispose();
			this.hbCache = bi;
		}
		return hbCache;
	}
	public void renderDebug(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		super.renderDebug(g2d, camX, camY, camW, camH);
		if(this.getBrain() != null) this.getBrain().renderDebug(g2d, camX, camY, camW, camH);
	}
}
