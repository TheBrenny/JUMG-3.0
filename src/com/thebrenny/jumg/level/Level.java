package com.thebrenny.jumg.level;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.entities.IHealable;
import com.thebrenny.jumg.gui.Screen;
import com.thebrenny.jumg.items.ItemEntity;
import com.thebrenny.jumg.level.gen.Generator;
import com.thebrenny.jumg.level.structure.Structure;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.level.tiles.TileEntity;
import com.thebrenny.jumg.util.Angle;
import com.thebrenny.jumg.util.Angle.AngleSpeed;
import com.thebrenny.jumg.util.Logger;
import com.thebrenny.jumg.util.MathUtil;
import com.thebrenny.jumg.util.VectorUtil;
import com.thebrenny.jumg.util.VectorUtil.Ray;

/**
 * Holds all the important information about the level: tiles, entities, etc.
 * 
 * @author TheBrenny
 */
public class Level {
	/**
	 * How far away (in chunks) an entity must be away before a new chunk is
	 * requested.
	 */
	public static int CHUNK_EXPANSION_BUFFER = 2;
	public static int ALL_TILES_ON_MAP = 0;
	public static int ALL_ENTITIES_ON_MAP = 0;
	/**
	 * Holds how many tiles were drawn to the screen.
	 */
	public static int RENDERED_TILES = 0;
	/**
	 * Holds how many tiles were skipped when rendering.
	 */
	public static int RENDERED_ENTITIES = 0;
	
	protected Generator generator;
	protected ChunkMap chunkMap;
	protected ArrayList<Entity> entities;
	protected ArrayList<ItemEntity> itemEntities;
	protected ArrayList<TileEntity> tileEntities;
	protected ArrayList<Structure> structures;
	protected DayNightCycle dayNightCycle;
	protected boolean doesExpand = false;
	
	/**
	 * Creates a new level based off the {@link Generator} {@code g}.
	 * 
	 * @param g
	 *        The Generator to use
	 */
	public Level(Generator g) {
		this.generator = g;
		g.generateMap();
		this.chunkMap = g.getMap();
		this.entities = new ArrayList<Entity>();
		this.itemEntities = new ArrayList<ItemEntity>();
		this.tileEntities = new ArrayList<TileEntity>();
		this.structures = new ArrayList<Structure>();
		this.dayNightCycle = new DayNightCycle(0.0F);
		
		for(Structure s : g.getStructures()) addStructure(s);
		
		calculateAllTiles();
	}
	
	/**
	 * Sets the {@link DayNightCycle} of the level and returns {@code this} for
	 * ease of chaining.
	 * 
	 * @param dayNight
	 *        The new DayNightCycle
	 * @return {@code this}.
	 */
	public Level setDayNightCycle(DayNightCycle dayNight) {
		this.dayNightCycle = dayNight;
		return this;
	}
	
	/**
	 * Gets the Tile in absolute position (x, y). See
	 * {@link ChunkMap#getTile(int, int)}.
	 */
	public Tile getTile(int x, int y) {
		return chunkMap.getTile(x, y);
	}
	
	/**
	 * Gets the Tile in the position, relative to the map's origin point. See
	 * {@link ChunkMap#getTileRelative(int, int)}.
	 */
	public Tile getTileRelative(int x, int y) {
		return chunkMap.getTileRelative(x, y);
	}
	/**
	 * Calls {@link Level#getTileRelative(int, int)} after converting the float
	 * position to tile coordinates (through {@link Level#toTileCoords(int)}).
	 */
	public Tile getTileRelative(float x, float y, boolean isTileCoords) {
		Point p = roundTileCoords(x, y);
		if(!isTileCoords) p = toTileCoords(x, y);
		return getTileRelative(p.x, p.y);
	}
	
	/**
	 * Gets the Chunk in absolute position (x, y). See
	 * {@link ChunkMap#getChunk(int, int)}.
	 */
	public Chunk getChunk(int x, int y) {
		return chunkMap.getChunk(x, y);
	}
	
	/**
	 * Gets the Chunk in the position, relative to the map's origin point. See
	 * {@link ChunkMap#getChunkRelative(int, int)}.
	 */
	public Chunk getChunkRelative(int x, int y) {
		return chunkMap.getChunkRelative(x, y);
	}
	
	/**
	 * Gets the amount of tiles wide the map currently is.
	 * 
	 * @return The map width in tiles.
	 */
	public int getMapWidth() {
		return chunkMap.getWidth() * Chunk.CHUNK_SIZE;
	}
	/**
	 * Gets the amount of tiles high the map currently is.
	 * 
	 * @return The map height in tiles.
	 */
	public int getMapHeight() {
		return chunkMap.getHeight() * Chunk.CHUNK_SIZE;
	}
	
	public int calculateAllTiles() {
		return (Level.ALL_TILES_ON_MAP = this.getMapWidth() * this.getMapHeight() + tileEntities.size());
	}
	
	public void expandMap(int x, int y) {
		Logger.log("EXPANDING MAP!!");
		chunkMap.expandMap(x, y);
		generator.generateExpandedMap();
		for(TileEntity te : generator.getTileEntities()) {
			addTileEntity(te);
		}
		calculateAllTiles();
	}
	public void expandMapTo(int minX, int minY, int maxX, int maxY) {
		Point p = chunkMap.relativeToAbsolute(minX, minY);
		minX = p.x;
		minY = p.y;
		p = chunkMap.relativeToAbsolute(maxX, maxY);
		maxX = p.x - chunkMap.getWidth();
		maxY = p.y - chunkMap.getHeight();
		
		if(minX < 0 || minY < 0) expandMap(minX, minY);
		if(maxX > 0 || maxY > 0) expandMap(maxX, maxY);
	}
	
	/**
	 * Returns the most upper left tile coordinate of the map. Useful for
	 * getting tile boundaries so far.
	 * 
	 * @return A {@link Point} location the upper left tile.
	 */
	public Point topLeftTile() {
		Point tlChunk = topLeftChunk();
		return new Point(tlChunk.x * Chunk.CHUNK_SIZE + 1, tlChunk.y * Chunk.CHUNK_SIZE + 1);
	}
	
	/**
	 * Returns the most lower right tile coordinate of the map. Useful for
	 * getting tile boundaries so far.
	 * 
	 * @return A {@link Point} location the bottom right tile.
	 */
	public Point bottomRightTile() {
		Point brChunk = bottomRightChunk();
		return new Point((brChunk.x + 1) * Chunk.CHUNK_SIZE - 1, (brChunk.y + 1) * Chunk.CHUNK_SIZE - 1);
	}
	
	/**
	 * Returns the most upper left chunk coordinate of the map. Useful for
	 * expanding chunk boundaries so far.
	 * 
	 * @return A {@link Point} location the upper left chunk.
	 */
	public Point topLeftChunk() {
		Point origin = chunkMap.getOrigin();
		return new Point(-origin.x, -origin.y);
	}
	/**
	 * Returns the most lower right chunk coordinate of the map. Useful for
	 * expanding chunk boundaries so far.
	 * 
	 * @return A {@link Point} location the upper left chunk.
	 */
	public Point bottomRightChunk() {
		Point origin = chunkMap.getOrigin();
		int w = chunkMap.getWidth();
		int h = chunkMap.getHeight();
		return new Point(w - origin.x - 1, h - origin.y - 1);
	}
	
	public Ray castRay(Ray ray) {
		return castRay(ray, 0);
	}
	
	// See com.brennytizer.zombies.res.raycasting_test.js
	public Ray castRay(Ray ray, int skip) {
		ray = new Ray(ray); // duplicate so we don't fuck the original
		if(ray.distance == 0) return ray;
		
		// Start prepping vars
		//     Origin stuff
		final Point2D.Float origin = new Point2D.Float(ray.getLocation().x, ray.getLocation().y);
		//final Point originTile = roundTileCoords(origin.x, origin.y);
		final Point tlTile = topLeftTile();
		final Point brTile = bottomRightTile();
		final float angle = new Angle(ray.getAngle().getAngle()).getAngle();
		//     Moving stuff
		Point tile = roundTileCoords(origin.x, origin.y);
		Point grid = new Point(tile);
		Point2D.Float mvp = new Point2D.Float(origin.x, origin.y);
		int dirX, dirY;
		int tileShiftX = 0, tileShiftY = 0;
		float dist;
		//     Function stuff
		float gradient;
		MathUtil.LinearFunction f;
		MathUtil.LinearFunction g;
		//     Loop stuff
		int giveUpLoop = 100; // this is the max chickens cycles for the while loop
		int giveUpCounter = 0;
		boolean hit = false;
		boolean exhausted = false;
		
		// Get gradient and produce functions
		AngleSpeed asTmp = AngleSpeed.getAngleSpeed(ray.angle.getAngle(), 1);
		gradient = (asTmp.getYSpeed() / asTmp.getXSpeed());
		f = (x, m) -> m * (x - origin.x) + origin.y;
		g = (y, m) -> (1 / m) * (y - origin.y) + origin.x;
		if(asTmp.getXSpeed() == 0) {
			f = (x, m) -> Float.POSITIVE_INFINITY;
			g = (y, m) -> origin.x; // (1/0) * (y - origin.y) + origin.x;
		}
		
		// determine direction to traverse
		dirX = angle > 0 && angle < 180 ? 1 : angle > 180 ? -1 : 0;
		dirY = angle > 90 && angle < 270 ? 1 : angle < 90 || angle > 270 ? -1 : 0;
		
		tileShiftX = dirX > 0 ? dirX : 0;
		tileShiftY = dirY > 0 ? dirY : 0;
		
		for(int i = 0; i <= skip; i++) {
			hit = false;
			exhausted = false;
			giveUpCounter = 0;
			do {
				// move tile
				grid.x += tileShiftX;
				grid.y += tileShiftY;
				// OLD: grid = roundTileCoords(mvp.x + tileShiftX, mvp.y + tileShiftY);
				// We want to maintain the grid.x and grid.y because it should keep moving, instead of resetting it.
				tileShiftX = tileShiftY = 0;
				
				mvp.x = g.eq(grid.y, gradient);
				mvp.y = f.eq(grid.x, gradient);
				
				// find the closest of the intercepts
				if(MathUtil.distanceSqrd(mvp.x, grid.y, origin.x, origin.y) < MathUtil.distanceSqrd(grid.x, mvp.y, origin.x, origin.y)) mvp.y = grid.y;
				else mvp.x = grid.x;
				
				// get tile coord
				tile = roundTileCoords(mvp.x, mvp.y);
				
				// check to see if we are exhausted
				if((dist = MathUtil.distance(mvp.x, mvp.y, origin.x, origin.y)) >= ray.distance) exhausted = true;
				else if(giveUpCounter >= giveUpLoop) exhausted = true;
				else if(!(tile.x >= tlTile.x && tile.x <= brTile.x && tile.y >= tlTile.y && tile.y <= brTile.y)) exhausted = true;
				//if(exhausted) break;
				
				// determine if hit is solid
				if(mvp.x % 1 == 0) tileShiftX = dirX;
				if(mvp.y % 1 == 0) tileShiftY = dirY;
				
				//if(getTileRelative((int) Math.floor(mvp.x), (int) Math.floor(mvp.y)).isSolid() && !exhausted) {
				if(getTileRelative(tile.x + (dirX < 0 ? tileShiftX : 0), tile.y + (dirY < 0 ? tileShiftY : 0)).isSolid() && !exhausted) {
					hit = true;
					ray.distance = dist;
				}
				giveUpCounter++;
			} while(!hit && !exhausted);
		}
		
		return ray;
	}
	
	/**
	 * Determines if the two tiles are unobstructed, regardless of distance.
	 * 
	 * @param p1
	 *        - The point identifying the first tile.
	 * @param p2
	 *        - The point identifying the second tile.
	 * @return True if the two tiles are unobstructed.
	 */
	public boolean unobstructedTiles(Point2D.Float p1, Point.Float p2) {
		p1 = VectorUtil.translatePoint(p1, 0F, 0F);
		p2 = VectorUtil.translatePoint(p2, 0F, 0F);
		
		Point2D.Float defP1 = VectorUtil.toFloatPoint(p1);
		Point2D.Float defP2 = VectorUtil.toFloatPoint(p2);
		Ray r1, r2;
		
		for(int x = 0; x <= 1; x++) {
			for(int y = 0; y <= 1; y++) {
				p1 = VectorUtil.translatePoint(p1, x, y);
				p2 = VectorUtil.translatePoint(p2, x, y);
				
				r1 = new Ray(p1, p2);
				r2 = this.castRay(r1);
				if(!r1.equals(r2)) return false;
				
				p1.setLocation(defP1);
				p2.setLocation(defP2);
			}
		}
		
		return true;
	}
	
	/**
	 * Checks to see if each pair of points are unobstructed, regardless of
	 * distance.
	 * 
	 * @param points
	 *        - A Point array of a length that is a multiple of 2.
	 * @return An array of booleans
	 */
	public boolean[] unobstructedTiles(Point2D.Float ... points) {
		if(points.length % 2 != 0) {
			Logger.log("OH NO! The points list passed wasn't even!");
			return null;
		}
		
		boolean[] ret = new boolean[points.length / 2];
		
		Point2D.Float p1, p2;
		for(int i = 0; i < points.length; i += 2) {
			p1 = points[i];
			p2 = points[i + 1];
			
			ret[i / 2] = unobstructedTiles(p1, p2);
		}
		
		return ret;
	}
	/**
	 * Determines if the path is unobstructed. That is to say that all adjacent
	 * tiles in the list are unobstructed.
	 * 
	 * @param points
	 *        - The points of the tiles which make up the path.
	 * @return Whether this path is unobstructed.
	 */
	public boolean unobstructedPath(Point2D.Float ... points) {
		for(int i = 0; i < points.length - 1; i++) {
			if(!unobstructedTiles(points[i], points[i + 1])) return false;
		}
		return true;
	}
	
	/**
	 * Adds a new {@link Entity} to the level as long as the name isn't already
	 * registered.
	 * 
	 * @param e
	 *        The entity to add
	 * @return If the entity was added. See {@link java.util.Collection#add}.
	 */
	public boolean addEntity(Entity e) {
		Logger.log("Adding entity [{0}] at tile location [({1}, {2})]", e.getName(), e.getAnchoredTileX(), e.getAnchoredTileY());
		Entity tmp;
		if((tmp = getEntity(e.getName())) != null) {
			Logger.log("OH NO! An entity is already using the name [{0}]: {1}", (Object) tmp.getName(), tmp.getClass().getSimpleName());
			return false;
		}
		Level.ALL_ENTITIES_ON_MAP++;
		return this.getEntities().add(e.setLevel(this));
	}
	
	/**
	 * Returns an entity based off the Unique {@code name} it was given.
	 * 
	 * @param name
	 *        The name to search for
	 * @return The {@link Entity} with the given name
	 */
	public Entity getEntity(String name) {
		for(Entity e : getEntities()) {
			if(e.getName().equals(name)) return e;
		}
		return null;
	}
	
	/**
	 * Returns all the nearby entities within the specified radius from a
	 * specified point.
	 * 
	 * @param tileX
	 *        The x-centre of the radius
	 * @param tileY
	 *        The y-centre of the radius
	 * @param tileRadius
	 *        The radius around (tileX, tileY)
	 * @param exclude
	 *        The entities to exclude from the resulting list
	 * @return An {@code ArrayList<Entity>} of all nearby entities.
	 */
	public ArrayList<Entity> getNearbyEntities(float tileX, float tileY, float tileRadius, Entity ... exclude) {
		ArrayList<Entity> excList = new ArrayList<Entity>(Arrays.asList(exclude));
		HashMap<Entity, Float> found = new HashMap<Entity, Float>();
		float trSqrd = tileRadius * tileRadius;
		float tmpDist;
		
		for(Entity e : getEntities()) {
			if(excList.contains(e)) continue;
			tmpDist = MathUtil.distanceSqrd(tileX, tileY, e.getAnchoredTileX(), e.getAnchoredTileY());
			if(trSqrd == 0 || tmpDist <= trSqrd) {
				found.put(e, new Float(tmpDist));
			}
		}
		
		ArrayList<Entity> ret = new ArrayList<Entity>(found.keySet());
		
		Collections.sort(ret, new Comparator<Entity>() {
			public int compare(Entity o1, Entity o2) {
				float d = found.get(o1) - found.get(o2);
				return d > 0 ? 1 : d < 0 ? -1 : 0; // if positive, o2 wins, if negative, o1 wins, otherwise equal
			}
		});
		
		return ret;
	}
	
	public Entity getNearestEntity(float tileX, float tileY, float tileRadius, Entity ... exclude) {
		ArrayList<Entity> ents = getNearbyEntities(tileX, tileY, tileRadius, exclude);
		return ents.size() > 0 ? ents.get(0) : null;
		/*
		 * ArrayList<Entity> excList = new
		 * ArrayList<Entity>(Arrays.asList(exclude));
		 * float trSqrd = tileRadius * tileRadius;
		 * Entity nearest = null;
		 * float distance = Float.MAX_VALUE;
		 * float distTmp;
		 * for(Entity e : getEntities()) {
		 * if(excList.contains(e) || nearest == e) continue;
		 * distTmp = MathUtil.distanceSqrd(tileX, tileY, e.getAnchoredTileX(),
		 * e.getAnchoredTileY());
		 * if(tileRadius == 0 || (distTmp <= trSqrd && distTmp <= distance)) {
		 * nearest = e;
		 * distance = distTmp;
		 * }
		 * }
		 * return nearest;
		 */
	}
	
	/**
	 * Removes an {@link Entity} from the level.
	 * 
	 * @param e
	 *        The entity to remove
	 * @return If the entity was removed. See
	 *         {@link java.util.Collection#remove}.
	 */
	public boolean removeEntity(Entity e) {
		if(this.getEntities().contains(e)) Level.ALL_ENTITIES_ON_MAP--;
		return this.getEntities().remove(e);
	}
	
	/**
	 * Gets all {@link Entity}s added to this level. This method has been
	 * synchronized already.
	 * 
	 * @return An {@code ArrayList<Entity>} object of all entities.
	 */
	public synchronized ArrayList<Entity> getEntities() {
		synchronized(this.entities) {
			return this.entities;
		}
	}
	
	/**
	 * Adds a new {@link ItemEntity} to the level as long as the name isn't
	 * already
	 * registered.
	 * 
	 * @param ie
	 *        The itemStack entity to add
	 * @return If the itemStack entity was added. See
	 *         {@link java.util.Collection#add}.
	 */
	public boolean addItemEntity(ItemEntity ie) {
		Logger.log("Adding item entity [{0}] at tile location [({1}, {2})]", ie.getName(), ie.getAnchoredTileX(), ie.getAnchoredTileY());
		ItemEntity tmp;
		if((tmp = getItemEntity(ie.getName())) != null) {
			Logger.log("OH NO! An item entity is already using the name [{0}]: {1}", (Object) tmp.getName(), tmp.getClass().getSimpleName());
			return false;
		}
		Level.ALL_ENTITIES_ON_MAP++;
		return this.getItemEntities().add((ItemEntity) ie.setLevel(this));
	}
	
	/**
	 * Returns an itemStack entity based off the Unique {@code name} it was
	 * given.
	 * 
	 * @param name
	 *        The name to search for
	 * @return The {@link ItemEntity} with the given name
	 */
	public ItemEntity getItemEntity(String name) {
		for(ItemEntity ie : getItemEntities()) {
			if(ie.getName().equals(name)) return ie;
		}
		return null;
	}
	
	/**
	 * Returns an itemStack entity based off the location it was spawned at.
	 * 
	 * @param x
	 *        The x coordinate to look at
	 * @param y
	 *        The y coordinate to look at
	 * @return The {@link ItemEntity} at the location or null.
	 */
	public ItemEntity getItemEntity(float x, float y) {
		for(ItemEntity ie : getItemEntities()) {
			if(ie.getTileX() == x && ie.getTileY() == y) return ie;
		}
		return null;
	}
	
	/**
	 * Removes an {@link ItemEntity} from the level.
	 * 
	 * @param ie
	 *        The itemStack entity to remove
	 * @return If the itemStack entity was removed. See
	 *         {@link java.util.Collection#remove}.
	 */
	public boolean removeItemEntity(ItemEntity ie) {
		if(this.getItemEntities().contains(ie)) Level.ALL_ENTITIES_ON_MAP--;
		return this.getItemEntities().remove(ie);
	}
	
	/**
	 * Gets all {@link ItemEntity}s added to this level. This method has been
	 * synchronized already.
	 * 
	 * @return An {@code ArrayList<ItemEntity>} object of all itemStack
	 *         entities.
	 */
	public synchronized ArrayList<ItemEntity> getItemEntities() {
		synchronized(this.itemEntities) {
			return this.itemEntities;
		}
	}
	
	/**
	 * Adds a new {@link TileEntity} to the level as long as the space isn't
	 * already occupied.
	 * 
	 * @param te
	 *        The tile entity to add
	 * @return If the tile entity was added. See
	 *         {@link java.util.Collection#add}.
	 */
	public boolean addTileEntity(TileEntity te) {
		Logger.log("Adding tile entity [{0}] at tile location [({1}, {2})]", te.getName(), te.getTileX(), te.getTileY());
		TileEntity tmp = getTileEntity(te.getTileX(), te.getTileY());
		if(tmp != null) {
			Logger.log("OH NO! A tile entity is already there [{0}]: {1}", tmp.getName(), tmp.getClass().getSimpleName());
			return false;
		}
		Level.ALL_TILES_ON_MAP++;
		return this.getTileEntities().add((TileEntity) te.setLevel(this));
	}
	
	/**
	 * Returns a tile entity based off the location it was spawned at.
	 * 
	 * @param x
	 *        The x coordinate to look at
	 * @param y
	 *        The y coordinate to look at
	 * @return The {@link TileEntity} at the location or null.
	 */
	public TileEntity getTileEntity(float x, float y) {
		for(TileEntity te : getTileEntities()) {
			if(te.getTileX() == x && te.getTileY() == y) return te;
		}
		return null;
	}
	
	/**
	 * Removes a {@link TileEntity} from the level.
	 * 
	 * @param te
	 *        The tile entity to remove
	 * @return If the tile entity was removed. See
	 *         {@link java.util.Collection#remove}.
	 */
	public boolean removeTileEntity(TileEntity te) {
		if(this.getTileEntities().contains(te)) Level.ALL_TILES_ON_MAP--;
		return this.getTileEntities().remove(te);
	}
	
	/**
	 * Gets all {@link TileEntity}s added to this level. This method has been
	 * synchronized already.
	 * 
	 * @return An {@code ArrayList<TileEntity>} object of all entities.
	 */
	public synchronized ArrayList<TileEntity> getTileEntities() {
		synchronized(this.tileEntities) {
			return this.tileEntities;
		}
	}
	
	public boolean addStructure(Structure s) {
		Logger.log("Adding structure [{0}] at tile location [({1}, {2})]", s.getUID(), s.getAnchoredX(), s.getAnchoredY());
		Structure tmp;
		if((tmp = getStructure(s.getUID())) != null) {
			Logger.log("OH NO! A structure is already using the UID [{0}]: {1}", (Object) tmp.getUID(), tmp.getClass().getSimpleName());
			return false;
		}
		//s.setLevel(this); // there's currently no "setLevel(Level) function.
		//Level.ALL_ENTITIES_ON_MAP++; // No Structures counter either.
		
		for(Entity e : s.getEntities()) addEntity(e);
		for(TileEntity te : s.getTileEntities()) addTileEntity(te);
		
		return this.getStructures().add(s);
	}
	public Structure getStructure(String uid) {
		for(Structure s : getStructures()) {
			if(s.getUID().equals(uid)) return s;
		}
		return null;
	}
	/**
	 * Deprecated because it's not doing anything yet.
	 * 
	 * @param s
	 * @return
	 */
	@Deprecated
	public boolean removeStructure(Structure s) {
		// doo some crazy map whizbangery. probably need to call on the generator for the map fix. don't worry too much about this though, it'll probably never ever ever get used.
		return false;
	}
	public synchronized ArrayList<Structure> getStructures() {
		synchronized(this.structures) {
			return this.structures;
		}
	}
	
	/**
	 * Returns the DayNightCycle associated to this map.
	 * 
	 * @return {@link DayNightCycle}.
	 */
	public DayNightCycle getDayNightCycle() {
		return this.dayNightCycle;
	}
	
	/**
	 * Executes game logic code: make things move, update health, and everything
	 * else not related to drawing things to the screen.
	 */
	public void tick() {
		tickEntities();
		tickItemEntities();
		tickTileEntities();
		dayNightCycle.tick();
	}
	
	/**
	 * Loops through the entity list to tick the entities on the map. This will
	 * update all entities on the map, regardless of whether they can be seen or
	 * not. This is already called by {@link Level#tick()}.
	 */
	public void tickEntities() {
		Entity[] ents = getEntities().toArray(new Entity[getEntities().size()]);
		for(Entity e : ents) {
			e.tick();
			checkForMapExpansion((int) e.getTileX(), (int) e.getTileY());
		}
	}
	
	/**
	 * Loops through the itemStack entity list to tick the itemStack entities on
	 * the map.
	 * This will update all itemStack entities on the map, regardless of whether
	 * they
	 * can be seen or not. This is already called by {@link Level#tick()}.
	 */
	public void tickItemEntities() {
		for(ItemEntity ie : getItemEntities()) {
			ie.tick();
		}
	}
	
	/**
	 * Loops through the tile entity list to tick the tile entities on the map.
	 * This will update all tile entities on the map, regardless of whether they
	 * can be seen or not. This is already called by {@link Level#tick()}.
	 */
	public void tickTileEntities() {
		for(TileEntity te : getTileEntities()) {
			te.tick();
		}
	}
	
	public void checkForMapExpansion(int tileX, int tileY) {
		tileX = Math.floorDiv((int) tileX, Chunk.CHUNK_SIZE);
		tileY = Math.floorDiv((int) tileY, Chunk.CHUNK_SIZE);
		expandMapTo(tileX - CHUNK_EXPANSION_BUFFER, tileY - CHUNK_EXPANSION_BUFFER, tileX + CHUNK_EXPANSION_BUFFER, tileY + CHUNK_EXPANSION_BUFFER);
	}
	
	/**
	 * Executes all the drawing functions. No game logic should be placed here!
	 * Only renders things the camera can see!
	 * 
	 * @param g2d
	 *        The graphics object to draw on
	 * @param camX
	 *        The xOffset of the camera
	 * @param camY
	 *        The yOffset of the camera
	 * @param camW
	 *        The width of the camera
	 * @param camH
	 *        The height of the camera
	 */
	public void render(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		RENDERED_TILES = 0;
		RENDERED_ENTITIES = 0;
		
		int ts = Tile.TILE_SIZE; // Tile size
		int cs = Chunk.CHUNK_SIZE; // Chunk size
		int ds = cs * ts; // Drawing size
		int tox = chunkMap.getOrigin().x;
		int toy = chunkMap.getOrigin().y;
		int xx;
		int yy;
		
		for(int x = 0; x < chunkMap.getWidth(); x++) {
			for(int y = 0; y < chunkMap.getHeight(); y++) {
				xx = x - tox;
				yy = y - toy;
				if(Screen.canCameraSeeChunk(xx, yy, camX, camY, camW, camH) && getChunk(x, y) != null) {
					g2d.drawImage(getChunk(x, y).getImage(), (int) (xx * ds - camX), (int) (yy * ds - camY), ds, ds, null);
					RENDERED_TILES += cs * cs;
				}
			}
		}
		renderTileEntities(g2d, camX, camY, camW, camH);
		renderItemEntities(g2d, camX, camY, camW, camH);
		renderEntities(g2d, camX, camY, camW, camH);
		g2d.drawImage(dayNightCycle.getImage(), 0, 0, camW, camH, null);
	}
	
	/**
	 * Renders all the entities to the screen, but only if the camera can see
	 * them! This is already called by
	 * {@link Level#render(Graphics2D, long, long, int, int)}.
	 * 
	 * @param g2d
	 *        The graphics object to draw on
	 * @param camX
	 *        The xOffset of the camera
	 * @param camY
	 *        The yOffset of the camera
	 * @param camW
	 *        The width of the camera
	 * @param camH
	 *        The height of the camera
	 */
	public void renderEntities(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		int ex = 0;
		int ey = 0;
		for(Entity e : getEntities()) {
			ex = (int) e.getX();
			ey = (int) e.getY();
			if(Screen.canCameraSeeEntity(e, camX, camY, camW, camH)) {
				e.render(g2d, camX, camY, camW, camH);
				//@formatter:off
				/*
				g2d.drawImage(
						e.getImage(),
						(int) (ex - camX),
						(int) (ey - camY),
						null
				);
				*/
				//@formatter:on
				RENDERED_ENTITIES++;
				
				if(e instanceof IHealable && ((IHealable) e).canRenderHealthBar()) {
					g2d.drawImage(((IHealable) e).getHealthBarImage(), (int) (ex - camX), (int) (ey + e.getHeight() + 2 - camY), null);
				}
			}
		}
	}
	public void renderItemEntities(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		for(ItemEntity ie : getItemEntities()) {
			if(Screen.canCameraSeeTile((int) ie.getTileX(), (int) ie.getTileY())) {
				ie.render(g2d, camX, camY, camW, camH);
				//@formatter:off
				/*
				g2d.drawImage(
						ie.getImage(),
						(int) (ie.getX() - camX),
						(int) (ie.getY() - camY),
						null
				);
				*/
				//@formatter:on
				RENDERED_ENTITIES++;
			}
		}
	}
	public void renderTileEntities(Graphics2D g2d, long camX, long camY, int camW, int camH) {
		for(TileEntity te : getTileEntities()) {
			if(Screen.canCameraSeeTile((int) te.getTileX(), (int) te.getTileY())) {
				te.render(g2d, camX, camY, camW, camH);
				//@formatter:off
				/*
				g2d.drawImage(
						te.getImage(),
						(int) (te.getX() - camX),
						(int) (te.getY() - camY),
						null
				);
				*/
				//@formatter:on
				RENDERED_TILES++;
			}
		}
	}
	
	/**
	 * Rounds a tile coordinate to return the tile that contains this tile
	 * coordinate. This is done by {@code floor}ing the coordinates.
	 * 
	 * @param x
	 *        The x coordinate
	 * @param y
	 *        The y coordinate
	 * @return The tile coordinate which "owns" the given coordinate.
	 */
	public static Point roundTileCoords(float x, float y) {
		x = (float) Math.floor(x);
		y = (float) Math.floor(y);
		return new Point((int) x, (int) y);
	}
	
	/**
	 * Converts a pixel coordinate to a tile coordinate.
	 * 
	 * @param x
	 *        The x pixel
	 * @param y
	 *        The y pixel
	 * @return The coordinate of the tile which "owns" this pixel.
	 */
	public static Point toTileCoords(float x, float y) {
		return roundTileCoords(x / Tile.TILE_SIZE, y / Tile.TILE_SIZE);
	}
	
	public static Point2D.Float toPixelCoords(float x, float y) {
		x *= Tile.TILE_SIZE;
		y *= Tile.TILE_SIZE;
		return new Point2D.Float(x, y);
	}
}
