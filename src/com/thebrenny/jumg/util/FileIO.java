package com.thebrenny.jumg.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.NoSuchElementException;

public class FileIO {
	// all FileIO things.
	// Reading and writing pure-text files, and
	// Reading and writing files as bits and bytes
	// specify protocol
	
	public static boolean writeFile(String fileLoc, DataChunk data, boolean inJar) {
		if(inJar) return false;
		BinaryWrite bw = new BinaryWrite(fileLoc);
		bw.writeData(data);
		bw.close();
		return false;
	}
	
	public static DataChunk readFile(String fileLoc, boolean inJar) {
		if(inJar) {
			fileLoc = "/" + StringUtil.trim(fileLoc.replace(".", "/"), "/");
			int i = fileLoc.lastIndexOf('/');
			fileLoc = fileLoc.substring(0, i) + "." + fileLoc.substring(i + 1);
		}
		BinaryRead br = new BinaryRead(fileLoc, inJar);
		DataChunk dc = new DataChunk(new boolean[] {});
		
		while(!br.isEmpty()) {
			dc = DataChunk.combine(dc, new DataChunk(br.readBits()));
		}
		
		return dc;
	}
	
	/*
	 * Modified from Princeton's Stdlib Library:
	 * https://introcs.cs.princeton.edu/java/stdlib/BinaryOut.java.html
	 * .
	 * Allowed under the GPU v3 License
	 */
	public static class BinaryRead {
		private static final int EOF = -1;
		
		private BufferedInputStream in;
		private byte buffer;
		
		public BinaryRead(String name, boolean inJar) {
			try {
				if(inJar) {
					this.in = new BufferedInputStream(getClass().getResourceAsStream(name));
				} else {
					File file = new File(name);
					if(file.exists()) {
						FileInputStream fis = new FileInputStream(file);
						in = new BufferedInputStream(fis);
					}
				}
				fillBuffer();
			} catch(IOException ioe) {
				System.err.println("Could not open " + name);
			}
		}
		
		private void fillBuffer() {
			try {
				buffer = (byte) in.read();
			} catch(IOException e) {
				System.err.println("EOF");
				buffer = EOF;
			}
		}
		
		public boolean[] readBits() {
			if(isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
			boolean[] bits = new boolean[Byte.SIZE];
			try {
				bits = MathUtil.toBitArray(buffer, Byte.SIZE);
				fillBuffer();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return bits;
		}
		
		public boolean exists() {
			return in != null;
		}
		public boolean isEmpty() {
			return buffer == EOF;
		}
	}
	public static class BinaryWrite {
		private BufferedOutputStream out;
		private byte buffer;
		private byte n;
		
		public BinaryWrite(String filename) {
			try {
				OutputStream os = new FileOutputStream(filename);
				out = new BufferedOutputStream(os);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		private void writeBit(boolean x) {
			buffer <<= 1;
			if(x) buffer |= 1;
			n++;
			if(n == 8) clearBuffer();
		}
		
		// write out any remaining bits in buffer to the binary output stream, padding with 0s
		private void clearBuffer() {
			if(n == 0) return;
			if(n > 0) buffer <<= (8 - n);
			try {
				out.write(buffer);
			} catch(IOException e) {
				e.printStackTrace();
			}
			n = 0;
			buffer = 0;
		}
		
		/**
		 * Flushes the binary output stream, padding 0s if number of bits
		 * written so far
		 * is not a multiple of 8.
		 */
		public void flush() {
			clearBuffer();
			try {
				out.flush();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Flushes and closes the binary output stream.
		 * Once it is closed, bits can no longer be written.
		 */
		public void close() {
			flush();
			try {
				out.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		public void writeData(DataChunk data) {
			for(int i = 0; i < data.getSize(); i++) {
				writeBit(data.getData(i));
			}
		}
	}
	
	public static class DataChunk {
		private int size;
		private boolean[] data;
		
		public DataChunk(boolean[] data) {
			this.size = data.length;
			this.data = data;
		}
		
		public int getSize() {
			return size;
		}
		public boolean[] getData() {
			return data;
		}
		public boolean[] getData(int start, int size) {
			boolean[] data = new boolean[size];
			
			for(int i = start; i < start + size; i++)
				data[i - start] = this.data[i];
			
			return data;
		}
		public boolean getData(int index) {
			return getData()[index];
		}
		
		public static DataChunk combine(DataChunk a, DataChunk b) {
			boolean[] data = new boolean[(int) (a.size + b.size)];
			
			int i = 0;
			for(i = 0; i < a.getSize(); i++) {
				data[i] = a.getData(i);
			}
			for(i = a.getSize(); i < a.getSize() + b.getSize(); i++) {
				data[i] = b.getData(i - a.getSize());
			}
			
			return new DataChunk(data);
		}
	}
	
	public static abstract class FileDataProtocol {
		public abstract DataChunk[] getDataChunks() throws Exception;
		public DataChunk getData() throws Exception {
			DataChunk[] dcs = getDataChunks();
			DataChunk running = new DataChunk(new boolean[] {});
			
			for(DataChunk dc : dcs) running = DataChunk.combine(running, dc);
			
			return running;
		}
		public abstract void readData(DataChunk data) throws Exception;
		@Deprecated
		public static boolean isFDP(BitSet data) {
			byte[] fdp = data.get(0, Character.SIZE * 3).toByteArray();
			//@formatter:off
			if(
				(char) ((fdp[0] << 8) | fdp[1]) == 'F' &&
				(char) ((fdp[2] << 8) | fdp[3]) == 'D' &&
				(char) ((fdp[4] << 8) | fdp[5]) == 'P'
			//@formatter:on
			) {
				return true;
			}
			return false;
		}
	}
	public static class FDPTextProtocol extends FileDataProtocol {
		private String data;
		
		public FDPTextProtocol(String data) {
			this.data = data;
		}
		public FDPTextProtocol(DataChunk data) {
			readData(data);
		}
		
		public DataChunk[] getDataChunks() {
			DataChunk[] dc = new DataChunk[getText().length()];
			
			try {
				char[] textChars = getText().toCharArray();
				boolean[] charBits;
				
				for(int i = 0; i < dc.length; i++) {
					charBits = MathUtil.toBitArray(textChars[i], Character.SIZE);
					dc[i] = new DataChunk(charBits);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			return dc;
		}
		public void readData(DataChunk data) {
			try {
				String read = "";
				char c = 0;
				
				for(int a = 0; a < data.getSize(); a += 16) {
					c = (char) MathUtil.fromBitArray(data.getData(a, 16));
					read = read + (char) c;
				}
				this.data = read;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public String getText() {
			return this.data;
		}
	}
}
