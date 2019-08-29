package com.thebrenny.jumg.level.gen;

import com.thebrenny.jumg.errors.BadLevelDataExcption;
import com.thebrenny.jumg.level.Chunk;
import com.thebrenny.jumg.level.ChunkMap;
import com.thebrenny.jumg.level.tiles.Tile;

public class GeneratorOneTileOnly extends Generator {
	protected Tile singleTile;
	
	public GeneratorOneTileOnly(int width, int height, Tile singleTile) {
		super(width, height);
		this.singleTile = singleTile;
	}
	
	public Chunk generateChunk(int x, int y) {
		try {
			Tile[][] td = Chunk.nullTileData();
			
			for(int tx = 0; tx < td.length; tx++) {
				for(int ty = 0; ty < td[tx].length; ty++) {
					td[tx][ty] = this.singleTile;
				}
			}
			
			return new Chunk(td);
		} catch(BadLevelDataExcption e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Chunk generateExpandedChunk(ChunkMap chunkMap, int x, int y) {
		return generateChunk(x, y);
	}
	
	public void buildBuildings() {}
	public void buildExpandedBuildings() {}
}
