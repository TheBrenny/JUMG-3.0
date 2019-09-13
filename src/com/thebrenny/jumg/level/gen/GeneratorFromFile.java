package com.thebrenny.jumg.level.gen;

import java.awt.Point;

import com.thebrenny.jumg.errors.BadLevelDataExcption;
import com.thebrenny.jumg.level.Chunk;
import com.thebrenny.jumg.level.ChunkMap;
import com.thebrenny.jumg.level.gen.io.FDPMapProtocol;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.util.FileIO;

public class GeneratorFromFile extends Generator {
	protected FDPMapProtocol mapData;
	protected Tile boundaryTile;
	protected int startMapX;
	protected int startMapY;
	protected int tMapWidth;
	protected int tMapHeight;
	
	public GeneratorFromFile(String mapLoc, boolean inJar, Tile boundaryTile) {
		super(0, 0);
		this.boundaryTile = boundaryTile;
		this.mapData = new FDPMapProtocol(FileIO.readFile(mapLoc, inJar));
		tMapWidth = mapData.getWidth();
		tMapHeight = mapData.getHeight();
		int cMapWidth = (int) Math.ceil((double) (tMapWidth / Chunk.CHUNK_SIZE));
		int cMapHeight = (int) Math.ceil((double) (tMapHeight / Chunk.CHUNK_SIZE));
		startMapX = (int) (Math.ceil((double) (tMapWidth % Chunk.CHUNK_SIZE)) / 2);
		startMapY = (int) (Math.ceil((double) (tMapHeight % Chunk.CHUNK_SIZE)) / 2);
		
		this.chunkMap.expandMap((int) -Math.floor(cMapWidth / 2), (int) -Math.floor(cMapHeight / 2));
		this.chunkMap.expandMap((int) Math.ceil(cMapWidth / 2), (int) Math.ceil(cMapHeight / 2));
	}
	
	/*
	 * LETS FUCKING HOPE THIS ACTUALLY WORKS!!!
	 * .
	 * Can only be tested in the real world. You also need to make a thing where
	 * you can actually create a map.
	 * .
	 * (non-Javadoc)
	 * @see com.thebrenny.jumg.level.gen.Generator#generateChunk(int, int)
	 */
	
	public Chunk generateChunk(int x, int y) {
		Point p = chunkMap.relativeToAbsolute(x, y);
		x = p.x;
		y = p.y;
		
		try {
			Tile[][] td = Chunk.nullTileData();
			
			for(int ty = y * td.length; ty < y * td.length + td.length; ty++) {
				for(int tx = x * td.length; tx < x * td.length + td.length; tx++) {
					td[tx - x * td.length][ty - y * td.length] = Tile.getTile(mapData.getTiles()[tx][ty]);
				}
			}
			
			return new Chunk(td);
		} catch(BadLevelDataExcption e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public Chunk generateExpandedChunk(ChunkMap chunkMap, int x, int y) {
		return null;
	}
	public void buildBuildings() {}
	public void buildExpandedBuildings() {}
}
