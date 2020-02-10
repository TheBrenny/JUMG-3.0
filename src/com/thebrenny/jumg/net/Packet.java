package com.thebrenny.jumg.net;

/*
 * TODO:
 * Idealy this wouldn't pass strings, but the old library used strings. Maybe in
 * an update, this'll change to using packets to establish their own protocols?
 */
public abstract class Packet {
	public static String PACKET_PREFIX = "JUMG";
	public static String DELIMITER = ",";
	protected String packetID;
	
	public Packet(String packetID) {
		this.packetID = packetID;
	}
	
	public String readData() {
		return readData(makeData());
	}
	
	public static String readData(byte[] data) {
		String[] message = new String(data).trim().split(DELIMITER, 2);
		return message[1];
	}
	
	public abstract Object[] getObjectsToSend();
	
	public byte[] makeData() {
		String result = generatePacketHeader() + Packet.DELIMITER;
		Object[] objs = getObjectsToSend();
		if(objs != null) { // in case we have an empty packet, just send the header.
			for(int i = 0; i < objs.length; i++) {
				result += objs[i];
				if(i != objs.length - 1) result += Packet.DELIMITER;
			}
		}
		return result.getBytes();
	}
	
	public String getPacketID() {
		return this.packetID;
	}
	
	public String toString() {
		return getClass().getName() + "[" + new String(makeData()).trim() + "]";
	}
	
	public String generatePacketHeader() {
		return PACKET_PREFIX + packetID;
	}
	
	public static String retrievePacketID(byte[] data) {
		return Packet.retrievePacketID(new String(data).trim());
	}
	public static String retrievePacketID(String data) {
		return data.substring(PACKET_PREFIX.length(), data.indexOf(Packet.DELIMITER));
	}
	
	public static String generatePacketHeader(Packet packet) {
		return packet.generatePacketHeader();
	}
}
