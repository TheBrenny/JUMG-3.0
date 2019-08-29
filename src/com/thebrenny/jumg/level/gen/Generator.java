package com.thebrenny.jumg.level.gen;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import com.thebrenny.jumg.entities.Entity;
import com.thebrenny.jumg.level.Chunk;
import com.thebrenny.jumg.level.ChunkMap;
import com.thebrenny.jumg.level.structure.Structure;
import com.thebrenny.jumg.level.tiles.TileEntity;
import com.thebrenny.jumg.util.Logger;

/**
 * Holds all the basic functions required by a map Generator. Maps are generated
 * on an <i>"On-Demand"</i> structure. This means that the Level holding the
 * generator needs to be pro-active in ensuring that there are enough chunks for
 * the player. This also means that the generator can build an infinite world,
 * or a specific world, as it enables control over how chunks are built.
 * 
 * @author TheBrenny
 */
public abstract class Generator {
	protected ChunkMap chunkMap;
	protected ArrayList<Structure> structures;
	
	/**
	 * Creates a new generator class.
	 * 
	 * @param width
	 *        The default chunk width of the map
	 * @param height
	 *        The default chunk height of the map
	 * 
	 * @see ChunkMap
	 */
	public Generator(int width, int height) {
		chunkMap = new ChunkMap(width, height);
		structures = new ArrayList<Structure>();
	}
	
	/**
	 * Generates a specific {@link Chunk} at position (x,y) relative to the
	 * {@link ChunkMap}'s origin.
	 * 
	 * @param x
	 *        The x coordinate of the chunk
	 * @param y
	 *        The y coordinate of the chunk
	 * @return The generated chunk.
	 */
	public abstract Chunk generateChunk(int x, int y);
	public abstract Chunk generateExpandedChunk(ChunkMap chunkMap, int x, int y);
	
	public abstract void buildBuildings();
	public abstract void buildExpandedBuildings();
	
	public void generateExpandedMap() {
		Logger.startSection("generateExpandedMap", "Generating the chunks for the expanded map");
		Point cmo = chunkMap.getOrigin();
		for(int x = 0; x < chunkMap.getWidth(); x++) {
			for(int y = 0; y < chunkMap.getHeight(); y++) {
				if(chunkMap.getChunk(x, y) == null) chunkMap.setChunk(x, y, generateExpandedChunk(chunkMap, x - cmo.x, y - cmo.y));
			}
		}
		buildExpandedBuildings();
		Logger.endLatestSection("Expansion generation complete.");
	}
	
	/**
	 * Generates the {@link ChunkMap} by looping through width * height times
	 * and generating a new chunk.
	 */
	public void generateMap() {
		Logger.startSection("generateMap", "Generating a map using class " + this.getClass().getSimpleName());
		Point cmo = chunkMap.getOrigin();
		for(int x = 0; x < chunkMap.getWidth(); x++) {
			for(int y = 0; y < chunkMap.getHeight(); y++) {
				chunkMap.setChunk(x, y, generateChunk(x - cmo.x, y - cmo.y));
			}
		}
		buildBuildings();
		Logger.endLatestSection("Map generated.");
	}
	
	public void addStructure(Structure s) {
		if(!structures.contains(s)) {
			for(int x = 0; x < s.getWidth(); x++) {
				for(int y = 0; y < s.getHeight(); y++) {
					chunkMap.setTileRelative(x + s.getX(), y + s.getY(), s.getTile(x, y));
				}
			}
			structures.add(s);
		}
	}
	
	public void addStructures(Structure ... structs) {
		for(Structure s : structs) addStructure(s);
	}
	
	/**
	 * Returns the entire map built by this generator.
	 * 
	 * @return {@link ChunkMap}
	 */
	public ChunkMap getMap() {
		return this.chunkMap;
	}
	
	public Structure[] getStructures() {
		return structures.toArray(new Structure[0]);
	}
	
	public Entity[] getEntities() {
		ArrayList<Entity> ents = new ArrayList<Entity>();
		for(Structure s : getStructures())
			ents.addAll(Arrays.asList(s.getEntities()));
		return ents.toArray(new Entity[0]);
	}
	
	public TileEntity[] getTileEntities() {
		ArrayList<TileEntity> tEnts = new ArrayList<TileEntity>();
		for(Structure s : getStructures())
			tEnts.addAll(Arrays.asList(s.getTileEntities()));
		return tEnts.toArray(new TileEntity[0]);
	}
}
