package com.thebrenny.jumg.net;

import java.net.InetAddress;
import java.util.ArrayList;

import com.thebrenny.jumg.util.Logger;

public abstract class GameServer extends NetworkInterface {
	private static GameServer INSTANCE;
	
	public static int DEFAULT_PORT = 52763; // jarod => convert to phone digits => 52763
	
	protected ServerInfo serverInfo;
	protected boolean running = false;
	protected ArrayList<NetworkInterface> allConnections = new ArrayList<NetworkInterface>();
	/**
	 * Set this to {@code null} if you want to handle all incoming packets.
	 * Otherwise JUMG will only handle connection packets (determined by the
	 * packet id set in this attribute), or if the server is already connected
	 * to the origin of the packet.
	 */
	protected String connectionPacketID = "000";
	
	public GameServer(String host) {
		super(null, -1, DEFAULT_PORT);
		if(GameServer.INSTANCE == null) GameServer.INSTANCE = this;
		else Logger.log("A GameServer is already running! Is this a mistake?");
		String tmp = this.getDomainSrcAddress();
		tmp = tmp != null ? tmp : this.getLANSrcAddress();
		String serverIP = tmp == null ? "127.0.0.1" : tmp;
		int port = this.getSrcPort();
		this.serverInfo = new ServerInfo(host, serverIP, port);
	}
	/**
	 * Sets the connection packet ID, to exclusively allow only incoming packets
	 * if they start with the passed Packet ID.
	 */
	public GameServer setConnectionPacket(String packetID) {
		this.connectionPacketID = packetID;
		return this;
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port) {
		super.parsePacket(data, address, port);
		String packetData = new String(data).trim();
		if(connectionPacketID == null || packetData.startsWith(Packet.PACKET_PREFIX + connectionPacketID) || isTtrackingConnection(address, port)) handlePacket(packetData, address, port);
		else {
			Logger.log("Blocked packet because none of these are true:");
			Logger.log("\t(conID == null, startsWith(prefix+conID), tracking(addr,port))");
			Logger.log("\t({}, \"{}\" startsWith \"{}\", {})", connectionPacketID, packetData.substring(0, (Packet.PACKET_PREFIX + connectionPacketID).length()), Packet.PACKET_PREFIX + connectionPacketID, address.getHostAddress() + ":" + port);
			Logger.log("\t({}, {}, {})", connectionPacketID == null, packetData.startsWith(Packet.PACKET_PREFIX + connectionPacketID), isTtrackingConnection(address, port));
			//System.out.println(packetData + " ----- " + Packet.PACKET_PREFIX + connectionPacketID);
		}
	}
	
	/**
	 * Handles the packet in its entirety, including the
	 * {@value Packet#PACKET_PREFIX} and the packet ID.
	 * <br>
	 * <br>
	 * Examples:<br>
	 * {@code message = "JUMG000,TheBrenny"        // connect (name)}<br>
	 * {@code message = "JUMG010,TheBrenny,2,-2,3" // add entity (name,entityID,xtile,ytile)}<br>
	 * {@code message = "JUMG011,TheBrenny,0.88"   // set player health like they've been hurt or healed (name,health%)}<br>
	 * {@code message = "JUMG012,TheBrenny,273"    // set player angle (name,angle)}<br>
	 * {@code message = "JUMG013,TheBrenny,"   // set player health like they've been hurt or healed (name,health%)}<br>
	 */
	public abstract void handlePacket(String message, InetAddress address, int port);
	
	public boolean addConnection(InetAddress address, int port) {
		synchronized(allConnections) {
			if(!isTtrackingConnection(address, port)) return allConnections.add(new NetworkInterface(address.getHostAddress(), port, this.getSrcPort(), false));
			else return false;
		}
	}
	protected boolean isTtrackingConnection(InetAddress address, int port) {
		synchronized(allConnections) {
			for(NetworkInterface iface : allConnections) {
				if(iface.getDestAddress().equals(address) && iface.getDestPort() == port) return true;
			}
			return false;
		}
	}
	public boolean removeConnection(InetAddress address, int port) {
		synchronized(allConnections) {
			for(int i = 0; i < allConnections.size(); i++) {
				if(allConnections.get(i).getDestAddress().equals(address) && allConnections.get(i).getDestPort() == port) return allConnections.remove(i) != null;
			}
			return false;
		}
	}

	public void sendPacket(Packet packet) {
		Logger.log("You need to specify a destination to send to! Use sendPacket(Packet, InetAddress, int)!", 1);
	}
	public void sendDataToAll(Packet packet) {
		sendDataToAll(packet.makeData());
	}
	public void sendDataToAll(byte[] data) {
		for(NetworkInterface ni : allConnections) sendData(data, ni.getDestAddress(), ni.getDestPort());
	}
	
	public static void destroyInstancedServer() {
		INSTANCE.running = false;
		INSTANCE.allConnections = null;
		INSTANCE.serverInfo = null;
		INSTANCE.socket.close();
		INSTANCE = null;
	}
	
	public ServerInfo getServerInfo() {
		return this.serverInfo;
	}
	
	public static GameServer getInstance() {
		return INSTANCE;
	}
	
	public static class ServerInfo extends Packet {
		private String host;
		private String ip;
		private int port;
		
		public ServerInfo(String host, String ip, int port) {
			super("servinfo");
			this.host = host;
			this.ip = ip;
			this.port = port;
		}
		
		public String getHost() {
			return host;
		}
		public String getIP() {
			return ip;
		}
		public int getPort() {
			return port;
		}
		
		public Object[] getObjectsToSend() {
			return new Object[] {host, ip, port};
		}
	}
}
