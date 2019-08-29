package com.thebrenny.jumg.gui.components;

import java.awt.image.BufferedImage;

import com.thebrenny.jumg.util.Images;
import com.thebrenny.jumg.util.TimeUtil;

public class GuiImageLabelAnimated extends GuiImageLabel {
	protected long lastTime;
	protected long delay;
	protected short spriteCount;
	protected short spriteIndex;
	protected int multiplierY;
	protected int multiplierX;
	
	public GuiImageLabelAnimated(float x, float y, BufferedImage image, long delay, int multX, int multY) {
		super(x, y, image);
		super.resize(multX, multY);
		this.delay = delay;
		this.lastTime = TimeUtil.getEpoch();
		this.spriteCount = (short) (image.getWidth() / multX);
		this.spriteIndex = 0;
		this.multiplierX = multX;
		this.multiplierY = multY;
	}
	
	public BufferedImage getImage() {
		long elapsed = TimeUtil.getElapsed(this.lastTime);
		this.lastTime += elapsed;
		int posAdd = (int) Math.floor(elapsed / this.delay);
		elapsed %= this.delay;
		this.spriteIndex = (short) ((this.spriteIndex + posAdd) % this.spriteCount);
		return Images.getSubImage(this.image, this.multiplierX, this.multiplierY, this.spriteIndex, 0, this.multiplierX, this.multiplierY);
	}
}