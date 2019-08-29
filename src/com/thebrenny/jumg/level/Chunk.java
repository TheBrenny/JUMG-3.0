package com.thebrenny.jumg.level;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.errors.BadLevelDataExcption;
import com.thebrenny.jumg.level.tiles.Tile;

public class Chunk {
	/**
	 * The one dimensional size of a chunk. ie, a chunk is made up of {@code CHUNK_SIZE * CHUNK_SIZE} tiles.
	 */
	public static final int CHUNK_SIZE = 16;
	private final Tile[][] chunk;
	private BufferedImage imageCache;
	
	public Chunk() throws BadLevelDataExcption {
		this(Chunk.nullTileData());
	}
	public Chunk(Tile[][] data) throws BadLevelDataExcption {
		if(data.length != Chunk.CHUNK_SIZE || data[0].length != Chunk.CHUNK_SIZE) throw new BadLevelDataExcption("Parsed level data length != 16");
		this.chunk = data;
		this.imageCache = null;
	}
	
	public Tile getTile(int x, int y) {
		return chunk[x][y];
	}
	
	public void setTile(int x, int y, Tile tile) {
		chunk[x][y] = tile;
		requestImageUpdate();
	}
	
	public void requestImageUpdate() {
		this.imageCache = null;
	}
	
	public void updateImage() {
		BufferedImage bi = new BufferedImage(Tile.TILE_SIZE * Chunk.CHUNK_SIZE, Tile.TILE_SIZE * Chunk.CHUNK_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
				g2d.drawImage(chunk[y][x].getImage(), x * Tile.TILE_SIZE, y * Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, null);
			}
		}
		
		g2d.dispose();
		this.imageCache = bi;
	}
	
	public BufferedImage getImage() {
		if(imageCache == null) updateImage();
		return imageCache;
	}
	
	public static Tile[][] nullTileData() {
		return new Tile[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
	}
}
