package com.thebrenny.jumg.engine;

import com.thebrenny.jumg.MainGame;
import com.thebrenny.jumg.util.Logger;

public class Engine implements Runnable {
	public static int TICKS_PS = 0;
	public static int FRAMES_PS = 0;
	protected static int maxTPS;
	
	public Engine() {
		this(60);
	}
	public Engine(int maxTPS) {
		Engine.maxTPS = maxTPS;
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / (double) Engine.maxTPS;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double missedTicks = 1;
		
		MainGame.getMainGame().setRunning(MainGame.getMainGame().initialise());
		while(MainGame.getMainGame().isRunning()) {
			Logger.newThread();
			Logger.startSection("root", null);
			long now = System.nanoTime();
			missedTicks += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = false;
			
			Logger.startSection("tick", null);
			while(missedTicks >= 1) {
				ticks++;
				tick();
				missedTicks -= 1;
				shouldRender = true;
			}
			Logger.endLatestSection(null);
			
			Logger.startSection("render", null);
			if(shouldRender) {
				frames++;
				render();
			}
			Logger.endLatestSection(null);
			
			if(System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				TICKS_PS = ticks;
				FRAMES_PS = frames;
				frames = 0;
				ticks = 0;
			}
			Logger.startSection("sleep", null);
			try {
				Thread.sleep(20);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			Logger.endLatestSection(null);
			Logger.endLatestSection(null);
			//Logger.refreshTree();
		}
		Logger.log("Everything has literally stopped. Everything logging output from now on should be returning errors...");
		System.exit(0);
	}
	public void tick() {
		MainGame.getMainGame().update();
	}
	public void render() {
		MainGame.getMainGame().render();
	}
	
	public static int getMaxTPS() {
		return Engine.maxTPS;
	}
}