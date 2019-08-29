package com.thebrenny.jumg.entities;

import java.awt.image.BufferedImage;

public interface IHealable {
	public float getHealth();
	public float getMaxHealth();
	public float heal(float amount);
	public float hurt(float amount);
	public void kill();
	public void onDeath();
	public boolean isAlive();
	public boolean canRenderHealthBar();
	public BufferedImage getHealthBarImage();
}
