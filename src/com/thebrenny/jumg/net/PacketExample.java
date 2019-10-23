package com.thebrenny.jumg.net;

/*
 * This is an example of how to write a packet which extends the {@link Packet}
 * class.
 */
public class PacketExample extends Packet {
	public static final String PACKET_ID = "283";
	private String itemOne;
	private int itemTwo;
	private boolean itemThree;
	
	public PacketExample(String one, int two, boolean three) {
		super(PACKET_ID);
		this.itemOne = one;
		this.itemTwo = two;
		this.itemThree = three;
	}
	public PacketExample(byte[] data) {
		super(PACKET_ID);
		String[] newData = readData(data).split(",");
		this.itemOne = newData[0];
		this.itemTwo = Integer.parseInt(newData[1]);
		this.itemThree = Boolean.parseBoolean(newData[2]);
	}
	
	public String getItemOne() {
		return itemOne;
	}
	public int getItemTwo() {
		return itemTwo;
	}
	public boolean getItemThree() {
		return itemThree;
	}
	
	public Object[] getObjectsToSend() {
		return new Object[] {itemOne, itemTwo, itemThree};
	}
}
