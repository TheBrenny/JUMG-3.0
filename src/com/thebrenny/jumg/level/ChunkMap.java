package com.thebrenny.jumg.level;

import java.awt.Point;
import java.util.LinkedList;

import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.util.MathUtil;

/**
 * Holds all the chunks with a reference to the origin chunk (centre chunk).
 * Supports the ability to expand the map, and get specific tiles, with plans
 * later on to add the ability to decrease the map size - to unload part of the
 * map.
 * 
 * The map is built by having a LinkedList of LinkedLists of Chunks:
 * {@code LinkedList<LinkedList<Chunk>>}. This enables us to get references to
 * previous and next "nodes" (chunks) in the list.
 * 
 * @author TheBrenny
 * @see LinkedList
 */
public class ChunkMap {
	protected int width;
	protected int height;
	protected Point origin;
	protected LinkedList<LinkedList<Chunk>> chunks;
	
	/**
	 * Creates a new ChunkMap.
	 * 
	 * @param width
	 *        The amount of CHUNKS wide
	 * @param height
	 *        The amount of CHUNKS high
	 */
	public ChunkMap(int width, int height) {
		this(width, height, false);
	}
	
	/**
	 * Creates a new ChunkMap with the specified width and height of tiles or
	 * chunks.
	 * 
	 * @param width
	 *        The amount of chunks/tiles wide
	 * @param height
	 *        The amount of chunks/tiles high
	 * @param tileMapDimensions
	 *        Determines if width and height need to be divided by
	 *        {@code Chunk.CHUNK_SIZE}
	 */
	public ChunkMap(int width, int height, boolean tileMapDimensions) {
		if(tileMapDimensions) {
			width = Math.floorDiv(width, Chunk.CHUNK_SIZE);
			height = Math.floorDiv(height, Chunk.CHUNK_SIZE);
			width += 1; // because 15/16 = 0, but we obviously need the extra chunk...
			height += 1;
		}
		this.width = width;
		this.height = height;
		this.chunks = ChunkMap.newNulledMap(width, height);
		this.origin = new Point(width / 2, height / 2);
	}
	
	public boolean setTile(int x, int y, Tile tile) {
		int chunkX = Math.floorDiv(x, Chunk.CHUNK_SIZE);
		int chunkY = Math.floorDiv(y, Chunk.CHUNK_SIZE);
		x = MathUtil.wrap(0, x, Chunk.CHUNK_SIZE);
		y = MathUtil.wrap(0, y, Chunk.CHUNK_SIZE);
		getChunk(chunkX, chunkY).setTile(x, y, tile);
		return true;
	}
	public boolean setTileRelative(int x, int y, Tile tile) {
		int chunkX = Math.floorDiv(x, Chunk.CHUNK_SIZE);
		int chunkY = Math.floorDiv(y, Chunk.CHUNK_SIZE);
		x = MathUtil.wrap(0, x, Chunk.CHUNK_SIZE);
		y = MathUtil.wrap(0, y, Chunk.CHUNK_SIZE);
		getChunkRelative(chunkX, chunkY).setTile(x, y, tile);
		return true;
	}
	
	/**
	 * Sets the specified chunk coordinate (absolute from the origin) with the
	 * passed chunk.
	 * 
	 * @param x
	 *        The x location of the chunk absolute from the origin
	 * @param y
	 *        The y location of the chunk absolute from the origin
	 * @param chunk
	 *        The chunk to insert
	 * @return True if the original chunk was null, false otherwise
	 */
	public boolean setChunk(int x, int y, Chunk chunk) {
		return chunks.get(x).set(y, chunk) == null;
	}
	
	/**
	 * Sets the specified chunk coordinate (relative to the origin) with the
	 * passed chunk.
	 * 
	 * @param x
	 *        The x location of the chunk relative to the origin
	 * @param y
	 *        The y location of the chunk relative to the origin
	 * @param chunk
	 *        The chunk to insert
	 * @return True if the original chunk was null, false otherwise.
	 */
	public boolean setChunkRelative(int x, int y, Chunk chunk) {
		Point p = relativeToAbsolute(x, y);
		return setChunk(p.x, p.y, chunk);
	}
	
	/**
	 * Returns the chunk at the specified chunk coordinate (absolute from the
	 * origin).
	 * 
	 * @param x
	 *        The x location of the chunk absolute from the origin
	 * @param y
	 *        The y location of the chunk absolute from the origin
	 * @return The chunk at absolute position (x, y).
	 */
	public Chunk getChunk(int x, int y) {
		return chunks.get(x).get(y);
	}
	
	/**
	 * Returns the chunk at the specified chunk coordinate (relative to the
	 * origin).
	 * 
	 * @param x
	 *        The x location of the chunk relative to the origin
	 * @param y
	 *        The y location of the chunk relative to the origin
	 * @return The chunk at relative position (x, y).
	 */
	public Chunk getChunkRelative(int x, int y) {
		Point p = relativeToAbsolute(x, y);
		if(p.x < 0 || p.x >= chunks.size() || p.y < 0 || p.y >= chunks.get(p.x).size()) return null;
		return chunks.get(p.x).get(p.y);
	}
	
	/**
	 * Returns the {@link Tile} at the specified tile coordinate (absolute from
	 * the chunk origin). So {@code getTile(0, 0} is the tile in the upper left
	 * corner of the map. This tile will change as the player travels more
	 * north-west, depending on the generator.
	 * 
	 * @param x
	 *        The x location of the Tile relative to the origin
	 * @param y
	 *        The x location of the Tile relative to the origin
	 * @return The Tile at absolute position (x, y).
	 */
	public Tile getTile(int x, int y) {
		int chunkX = Math.floorDiv(x, Chunk.CHUNK_SIZE);
		int chunkY = Math.floorDiv(y, Chunk.CHUNK_SIZE);
		x = MathUtil.wrap(0, x, Chunk.CHUNK_SIZE);
		y = MathUtil.wrap(0, y, Chunk.CHUNK_SIZE);
		return getChunk(chunkX, chunkY).getTile(x, y);
	}
	
	/**
	 * Returns the {@link Tile} at the specified tile coordinate (relative to
	 * the chunk origin). So {@code getTile(-1, 0} is the tile to the left of
	 * the origin Tile.
	 * 
	 * @param x
	 *        The x location of the Tile relative to the origin
	 * @param y
	 *        The x location of the Tile relative to the origin
	 * @return The Tile at relative position (x, y).
	 */
	public Tile getTileRelative(int x, int y) {
		int chunkX = Math.floorDiv(x, Chunk.CHUNK_SIZE);
		int chunkY = Math.floorDiv(y, Chunk.CHUNK_SIZE);
		x = MathUtil.wrap(0, x, Chunk.CHUNK_SIZE);
		y = MathUtil.wrap(0, y, Chunk.CHUNK_SIZE);
		Chunk c = getChunkRelative(chunkX, chunkY);
		return c == null ? null : c.getTile(x, y);
	}
	
	/**
	 * Expands the map by the absolute value of width and height. If width or
	 * height is negative, it prepends the expansion to the linked lists,
	 * otherwise it appends it.
	 * 
	 * @param width
	 *        How much wider the map should get
	 * @param height
	 *        How much taller the map should get
	 * @return {@code this}.
	 */
	public ChunkMap expandMap(int width, int height) {
		int absX = Math.abs(width);
		int absY = Math.abs(height);
		boolean xNeg = width < 0;
		boolean yNeg = height < 0;
		if(width != 0) {
			for(int xx = 0; xx < absX; xx++) {
				this.chunks.add(xNeg ? 0 : this.chunks.size(), newNulledColumn(this.height));
			}
		}
		this.width = this.chunks.size();
		if(height != 0) {
			for(width = 0; width < this.width; width++) {
				for(int yy = 0; yy < absY; yy++) {
					this.chunks.get(width).add(yNeg ? 0 : this.chunks.get(width).size(), null);
				}
			}
		}
		this.height = this.chunks.get(0).size();
		this.origin.translate(xNeg ? absX : 0, yNeg ? absY : 0);
		return this;
	}
	
	/**
	 * Returns the width of the chunk map in chunks. Multiply this by
	 * {@link Chunk#CHUNK_SIZE} to get the number of tiles wide.
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of the chunk map in chunks. Multiply this by
	 * {@link Chunk#CHUNK_SIZE} to get the number of tiles high.
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns a new coordinate which is relative to the Chunk Origin. Such as a
	 * 10*10 map with and origin of (5,5), calling
	 * {@code relativeToAbsolute(0, 0)} would return (5, 5) - which is the
	 * origin. Therefore, calling {@code relativeToAbsolute(-5, -5)} would
	 * return (0, 0) - which is the upper left corner of the map.
	 * 
	 * A check should be conducted to ensure that the returned result is within
	 * the boundaries of the ChunkMap otherwise... I don't know... Stranger
	 * Things might occur...
	 * 
	 * This is the inverse of {@link ChunkMap#absoluteToRelative(int x, int y)}.
	 * 
	 * @param x
	 *        The x-coordinate which is relative to the origin
	 * @param y
	 *        The y-coordinate which is relative to the origin
	 * @return The absolute {@code Point}.
	 */
	public Point relativeToAbsolute(int x, int y) {
		Point p = new Point(origin.x, origin.y);
		p.translate(x, y);
		return p;
	}
	
	/**
	 * Returns a new coordinate which is absolute from the Chunk Origin. Such as
	 * a
	 * 10*10 map with and origin of (5,5), calling
	 * {@code absoluteToRelative(0, 0)} would return (-5, -5) - which is the
	 * upper left corner. Therefore, calling {@code absoluteToRelative(5, 5)}
	 * would return (0, 0) - which is the origin of the map.
	 * 
	 * A check should be conducted to ensure that the returned result is within
	 * the boundaries of the ChunkMap otherwise... I don't know... Stranger
	 * Things might occur...
	 * 
	 * This is the inverse of {@link ChunkMap#relativeToAbsolute(int x, int y)}.
	 * 
	 * @param x
	 *        The x-coordinate which is absolute from the origin
	 * @param y
	 *        The y-coordinate which is absolute from the origin
	 * @return The relative {@code Point}.
	 */
	public Point absoluteToRelative(int x, int y) {
		Point p = new Point(-origin.x, -origin.y);
		p.translate(x, y);
		return p;
	}
	
	/**
	 * Returns the coordinates of the origin chunk. Usually used for internal
	 * calculations, and should never be displayed to the player.
	 * 
	 * @return {@link java.awt.Point} of the origin
	 */
	public Point getOrigin() {
		return this.origin;
	}
	
	/**
	 * Creates a new {@link LinkedList} of chunks (which are actually null) of
	 * height {@code height}. This funciton should only be used internally.
	 * 
	 * @param height
	 *        The height of the chunk column
	 * @return a {@code LinkedList<Chunk>} full of nulls
	 */
	protected static LinkedList<Chunk> newNulledColumn(int height) {
		LinkedList<Chunk> column = new LinkedList<Chunk>();
		for(int y = 0; y < height; y++)
			column.add(y, null);
		return column;
	}
	
	/**
	 * Creates a new {@link LinkedList} of LinkedLists of Chunks (which are
	 * actually null) of size {@code width * height}. This function should only
	 * be used internally.
	 * 
	 * @param width
	 *        The width of the map
	 * @param height
	 *        The height of the map
	 * @return a {@code LinkedList<LinkedList<Chunk>>} full of nulls
	 */
	protected static LinkedList<LinkedList<Chunk>> newNulledMap(int width, int height) {
		LinkedList<LinkedList<Chunk>> chunks = new LinkedList<LinkedList<Chunk>>();
		for(int x = 0; x < width; x++)
			chunks.add(newNulledColumn(height));
		return chunks;
	}
}
