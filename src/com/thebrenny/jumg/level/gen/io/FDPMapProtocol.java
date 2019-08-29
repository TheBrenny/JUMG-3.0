package com.thebrenny.jumg.level.gen.io;

import com.thebrenny.jumg.util.FileIO.DataChunk;
import com.thebrenny.jumg.util.FileIO.FileDataProtocol;
import com.thebrenny.jumg.util.MathUtil;

/*
 * *****************************************************************************
 * YOU ARE ONLY ALLOWING 2 BITS FOR TILE ID! MAKE SURE YOU MAKE A WAY FOR THIS
 * TO CHANGE SOMEWHERE!
 * *****************************************************************************
 */

public class FDPMapProtocol extends FileDataProtocol {
	// 8 bits (width), --------------------------------------------------- 8
	// 8 bits (height), -------------------------------------------------- 8
	// 2 bits * width*height (tiles), ------------------------------------ 130050 bits = 65025 tiles
	// endless 2 bits + 8 bits + 8 bits (tile entity ID + xpos + ypos) --- 18 * x
	// total bits with 8 tile entities (130066 + 18*8) bits = 130210 bits = 16276 Bytes = 14 kB
	
	private int width;
	private int height;
	private byte[][] tileIDs;
	private int[][] tileEntities;
	
	public FDPMapProtocol(int width, int height, byte[][] tileIDs, int[][] tileEntities) {
		// maybe put ID length req in the protocol? width, height, idLength, tiles, tileEnts ?
		this.width = width;
		this.height = height;
		this.tileIDs = tileIDs;
		this.tileEntities = tileEntities;
	}
	public FDPMapProtocol(DataChunk data) {
		try {
			readData(data);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public DataChunk[] getDataChunks() throws Exception {
		DataChunk[] dc = new DataChunk[2 + width * height + tileEntities.length];
		
		int index = 0;
		
		//dc0 = width
		//dc1 = height
		dc[index++] = new DataChunk(MathUtil.toBitArray(width, Byte.SIZE));
		dc[index++] = new DataChunk(MathUtil.toBitArray(height, Byte.SIZE));
		
		//dcx-y = tiles
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				dc[index++] = new DataChunk(MathUtil.toBitArray(tileIDs[y][x], 2));
			}
		}
		
		//dclast = tilentities
		int teDC = 0;
		for(int i = 0; i < tileEntities.length; i++) {
			teDC = (0 + tileEntities[i][0]) << 8;
			teDC = (teDC | tileEntities[i][1]) << 8;
			teDC = (teDC | tileEntities[i][2]) << 0; // just ls 0 bits because looks neat.
			//te = 0b110000000000000000;
			//te = 0b001111111100000000;
			//te = 0b000000000011111111;
			
			dc[index++] = new DataChunk(MathUtil.toBitArray(teDC, 18));
		}
		
		return dc;
	}
	public void readData(DataChunk data) throws Exception {
		int counter = 0;
		this.width = (int) MathUtil.fromBitArray(data.getData(0, 8));
		this.height = (int) MathUtil.fromBitArray(data.getData(8, 8));
		this.tileIDs = new byte[this.height][this.width];
		
		counter = 16;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				this.tileIDs[y][x] = (byte) MathUtil.fromBitArray(data.getData(counter, 2));
				counter += 2;
			}
		}
		
		int tileEntityChunkSize = 18;
		this.tileEntities = new int[(data.getSize() - counter) / tileEntityChunkSize][3];
		int teStart = counter;
		int teData, teID, teX, teY;
		while(counter < data.getSize()) {
			teData = (int) MathUtil.fromBitArray(data.getData(counter, tileEntityChunkSize));
			teID = (teData & 0b110000000000000000) >> 16;
			teX = (teData & 0b001111111100000000) >> 8;
			teY = (teData & 0b000000000011111111) >> 0;
			tileEntities[(counter - teStart) / tileEntityChunkSize] = new int[] {teID, teX, teY};
			
			counter += tileEntityChunkSize;
		}
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public byte[][] getTiles() {
		return tileIDs;
	}
	public int[][] getTileEntities() {
		return tileEntities;
	}
}
