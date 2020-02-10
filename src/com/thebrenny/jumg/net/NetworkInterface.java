package com.thebrenny.jumg.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;

import com.thebrenny.jumg.util.Logger;
import com.thebrenny.jumg.util.StringUtil;

public class NetworkInterface implements Runnable {
	private static final long DEFAULT_TIMEOUT = 7000;
	private static final int DEFAULT_CHECK_COUNT = 10;
	public static int PACKET_SIZE = 1024; // max 512 chars
	protected DatagramSocket socket;
	protected LinkedList<DatagramPacket> grabbedPackets;
	protected InetAddress destAddress;
	protected int destPort;
	protected InetAddress srcAddress;
	protected int srcPort;
	protected boolean wasPingOrPongRxd = false; // Rxd = received
	protected boolean running = false;
	
	public NetworkInterface(String destAddress, int destPort) {
		this(destAddress, destPort, 0);
	}
	public NetworkInterface(String destIPAddress, int destPort, int srcPort) {
		this(destIPAddress, destPort, srcPort, true);
	}
	public NetworkInterface(String destIPAddress, int destPort, int srcPort, boolean makeSocket) {
		try {
			Logger.log(StringUtil.insert("Attempting to create a networking client from [{}] to [{}:{}]", srcPort, destIPAddress, destPort));
			this.setDestination(destIPAddress, destPort);
			this.srcAddress = InetAddress.getLocalHost();
			this.srcPort = srcPort;
			grabbedPackets = new LinkedList<DatagramPacket>();
			if(makeSocket) {
				this.socket = new DatagramSocket(srcPort);
				this.srcPort = this.socket.getLocalPort();
			}
			Logger.log(StringUtil.insert("Networking client created from [{}] to [{}:{}]", srcPort, destIPAddress, destPort));
		} catch(Exception e) {
			e.printStackTrace();
			Logger.log(StringUtil.insert("Oh no! Networking client could not be created from [{}] to [{}:{}]", srcPort, destIPAddress, destPort));
			Logger.log("Maybe there's already a process running on that port?");
		}
	}
	
	public void start() {
		running = true;
		new Thread(new Runnable() {
			public void run() {
				NetworkInterface.this.run();
			}
		}).start();
	}
	
	public void run() {
		byte[] data = new byte[PACKET_SIZE];
		DatagramPacket packet = null;
		String s = "";
		
		if(running) Logger.log(StringUtil.insert("Networking client ready and listening on [{}]!", srcPort));
		else Logger.log("Oh no! Something went wrong, and I'm already closing! [{}]", srcPort);
		
		while(running) {
			packet = new DatagramPacket(data, data.length);
			try {
				// System.out.println(this.getClass().getSimpleName() + " waiting...");
				socket.receive(packet);
				Logger.logInPacket(packet);
				
				grabbedPackets.add(new DatagramPacket(Arrays.copyOf(packet.getData(), packet.getData().length), packet.getData().length, packet.getAddress(), packet.getPort()));
				
				// System.out.println(this.getClass().getSimpleName() + ", " + getLANSrcAddress() + ":" + getSrcPort());
				// for(int i = 0; i < grabbedPackets.size(); i++) System.out.println(new String(grabbedPackets.get(i).getData()).trim());
				
				s = new String(packet.getData()).trim();
				
				if(s.startsWith(Packet.PACKET_PREFIX + ",ping-jumg")) {
					sendData((Packet.PACKET_PREFIX + ",pong-jumg-" + (destAddress == null)).getBytes(), packet.getAddress(), packet.getPort());
					wasPingOrPongRxd = true;
				} else if(s.startsWith(Packet.PACKET_PREFIX + ",pong-jumg")) {
					wasPingOrPongRxd = true;
				}
				
				parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			} catch(Exception e) {
				if(e.getLocalizedMessage() == null || !e.getLocalizedMessage().equals("socket closed")) {
					Logger.log("Oh no! Something went wrong!");
					e.printStackTrace();
				}
			}
			Arrays.fill(data, (byte) 0);
		}
		socket.close();
	}
	
	public void parsePacket(byte[] data, InetAddress ipAddress, int port) {
	}
	
	public void stop() {
		this.running = false;
		socket.close();
	}
	
	public void setDestination(String address, int port) {
		try {
			this.destAddress = InetAddress.getByName(address);
			this.destPort = port;
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public InetAddress getDestAddress() {
		return destAddress;
	}
	public int getDestPort() {
		return destPort;
	}
	public InetAddress getSrcAddress() {
		return srcAddress;
	}
	public int getSrcPort() {
		return srcPort;
	}
	
	public String getLANSrcAddress() {
		return srcAddress != null ? srcAddress.getHostAddress() : null;
	}
	public String getDomainSrcAddress() {
		return srcAddress != null ? srcAddress.getCanonicalHostName() : null;
	}
	public String getLANDestAddress() {
		return destAddress != null ? destAddress.getHostAddress() : null;
	}
	public String getDomainDestAddress() {
		return destAddress != null ? destAddress.getCanonicalHostName() : null;
	}
	
	public void sendPacket(Packet packet) {
		sendPacket(packet, destAddress, destPort);
	}
	public void sendPacket(Packet packet, InetAddress address, int port) {
		sendData(packet.makeData(), address, port);
	}
	public void sendData(byte[] data) {
		sendData(data, destAddress, destPort);
	}
	public void sendData(byte[] data, InetAddress address, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		try {
			socket.send(packet);
			Logger.logOutPacket(packet);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public DatagramPacket grabPacket() {
		return this.grabPacket(DEFAULT_TIMEOUT, DEFAULT_CHECK_COUNT);
	}
	public DatagramPacket grabPacket(long timeout, int checks) {
		return this.grabPacket(timeout, checks, null);
	}
	public DatagramPacket grabPacket(String packetHeader) {
		return this.grabPacket(DEFAULT_TIMEOUT, DEFAULT_CHECK_COUNT, packetHeader);
	}
	public DatagramPacket grabPacket(long timeout, int checks, String packetHeader) {
		if(packetHeader != null && !packetHeader.startsWith(Packet.PACKET_PREFIX)) Logger.log("Packet header [{}] doesn't start with a traditional header! Be careful!", packetHeader);
		
		DatagramPacket p = null;
		
		try {
			for(int i = checks; i > 0 || grabbedPackets.size() > 0; i--) {
				Thread.sleep(timeout / checks);
				p = grabbedPackets.poll();
				
				//@formatter:off
				if(
					packetHeader != null &&
					p != null &&
					new String(p.getData()).trim().startsWith(packetHeader)
				) return p;
				//@formatter:on
			}
		} catch(Exception e) {
			Logger.log("Oh no! Something went wrong!");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean testConnection2() {
		sendData((Packet.PACKET_PREFIX + ",ping-jumg").getBytes());
		DatagramPacket packet = grabPacket(Packet.PACKET_PREFIX + ",pong-jumg");
		return packet != null;
	}
	
	@Deprecated
	public boolean testConnection() {
		return testConnection2();
		
		// if(destAddress == null || destPort == -1) return false;
		
		// boolean success = false;
		// long timeout = 7000;
		// int checks = 10;
		
		// try {
		// 	sendData("ping-jumg".getBytes());
		// 	for(int i = checks; i > 0 && !success; i--) {
		// 		Thread.sleep(timeout / checks);
		// 		if(wasPingOrPongRxd) {
		// 			success = true;
		// 			wasPingOrPongRxd = false;
		// 		}
		// 	}
		// } catch(Exception e) {
		// 	e.printStackTrace();
		// }
		// return success;
	}
}
