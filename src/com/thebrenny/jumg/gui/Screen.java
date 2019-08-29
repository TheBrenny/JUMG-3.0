package com.thebrenny.jumg.gui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import com.thebrenny.jumg.MainGame;
import com.thebrenny.jumg.engine.Engine;
import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.input.KeyBindings;
import com.thebrenny.jumg.level.Chunk;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.util.Logger;
import com.thebrenny.jumg.util.MathUtil;
import com.thebrenny.jumg.util.StringUtil;

/**
 * The core class which enables the ability to draw things to the screen.
 * Screens are what display graphics to the frame, and can display whatever and
 * however through the {@link #render(Graphics2D)} method. A common
 * implementation would be for Menu Screens (see {@link ScreenMenu}) or for Game
 * Screens. The camera variables contained within are used particularly for the
 * game screen, as to know where the camera is at all times.
 * 
 * @author TheBrenny
 */
public abstract class Screen {
	private static final Rectangle2D CAM_TEST_RECT = new Rectangle2D.Float();
	private static long CAMERA_X = 0;
	private static long CAMERA_Y = 0;
	private static float CAMERA_SPEED = 1.0F;
	
	public void mouseEvent(Point mousePoint, boolean mouseDown) {}
	public void moveCamera() {}
	
	public void update() {
		moveCamera();
		tick();
	}
	public void draw(Graphics2D g2d) {
		render(g2d);
		if(canRenderDebug()) renderDebug(g2d, 1);
	}
	public abstract void tick();
	public abstract void render(Graphics2D g2d);
	public int renderDebug(Graphics2D g2d, int line) {
		g2d.setFont(new Font("Consolas", Font.BOLD, 16));
		int fontHeight = g2d.getFontMetrics().getHeight();
		drawDebugString(g2d, "Ticks: " + Engine.TICKS_PS, line++, fontHeight);
		drawDebugString(g2d, "Frames: " + Engine.FRAMES_PS, line++, fontHeight);
		drawDebugString(g2d, StringUtil.insert("Camera: ({0}, {1}, {2}, {3})", getCameraX(), getCameraY(), getWidth(), getHeight()), line++, fontHeight);
		return line;
	}
	public void drawDebugString(Graphics2D g2d, String str, int line, int lineHeight) {
		g2d.drawString(str, 10, lineHeight * line++);
	}
	public boolean canRenderDebug() {
		return MainGame.getMainGame().isDebugging() || KeyBindings.isPressed("debug");
	}
	
	public static long getCameraX() {
		return Screen.CAMERA_X;
	}
	public static long getCameraY() {
		return Screen.CAMERA_Y;
	}
	public static float getCameraSpeed() {
		return Screen.CAMERA_SPEED;
	}
	public static int getWidth() {
		return MainGame.getMainGame().getDisplay().getWidth();
	}
	public static int getHeight() {
		return MainGame.getMainGame().getDisplay().getHeight();
	}
	
	public static void screenForward(Screen s) {
		MainGame.getMainGame().getDisplay().screenMan().screenForward(s);
	}
	public static void screenBack() {
		MainGame.getMainGame().getDisplay().screenMan().screenBack();
	}
	
	public static void setCameraX(long camX) {
		Screen.CAMERA_X = camX;
	}
	public static void setCameraY(long camY) {
		Screen.CAMERA_Y = camY;
	}
	public static void setCameraSpeed(float camSpeed) {
		Screen.CAMERA_SPEED = MathUtil.wrap(0.001F, camSpeed, 1.0F);
		if(camSpeed != Screen.CAMERA_SPEED) Logger.log("Cam speed was either too small or too high, so it's been wrapped within the boundaries of 0.001 and 1.0: " + Screen.CAMERA_SPEED);
	}
	
	public static boolean canCameraSeeTile(float x, float y) {
		return canCameraSeeTile(x, y, getCameraX(), getCameraY(), getWidth(), getHeight());
	}
	public static boolean canCameraSeeChunk(int x, int y) {
		return canCameraSeeChunk(x, y, getCameraX(), getCameraY(), getWidth(), getHeight());
	}
	public static boolean canCameraSeeEntity(Entity e) {
		return canCameraSeeEntity(e, getCameraX(), getCameraY(), getWidth(), getHeight());
	}
	public static boolean canCameraSeeBox(Rectangle2D r2d) {
		return canCameraSeeBox(r2d, getCameraX(), getCameraY(), getWidth(), getHeight());
	}
	
	public static boolean canCameraSeeTile(float x, float y, long camX, long camY, int camW, int camH) {
		CAM_TEST_RECT.setRect((int) x * Tile.TILE_SIZE, (int) y * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE);
		return canCameraSeeBox(CAM_TEST_RECT, camX, camY, camW, camH);
	}
	public static boolean canCameraSeeChunk(int x, int y, long camX, long camY, int camW, int camH) {
		CAM_TEST_RECT.setRect(x * Tile.TILE_SIZE * Chunk.CHUNK_SIZE, y * Tile.TILE_SIZE * Chunk.CHUNK_SIZE, Tile.TILE_SIZE * Chunk.CHUNK_SIZE, Tile.TILE_SIZE * Chunk.CHUNK_SIZE);
		return canCameraSeeBox(CAM_TEST_RECT, camX, camY, camW, camH);
	}
	public static boolean canCameraSeeEntity(Entity e, long camX, long camY, int camW, int camH) {
		return canCameraSeeBox(e.getBoundingBox(), camX, camY, camW, camH);
	}
	public static boolean canCameraSeeBox(Rectangle2D r2d, long camX, long camY, int camW, int camH) {
		int x = (int) r2d.getX();
		int y = (int) r2d.getY();
		int w = (int) r2d.getWidth();
		int h = (int) r2d.getHeight();
		if(//@formatter:off -- This determines if the entity is within the boundaries of the camera.
				x + w >= camX &&
				y + h >= camY &&
				x <= camX + camW &&
				y <= camY + camH
		) return true;
		//@formatter:on
		return false;
	}
}
