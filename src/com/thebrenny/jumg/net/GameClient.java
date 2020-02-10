package com.thebrenny.jumg.net;

import java.net.InetAddress;

import com.thebrenny.jumg.util.Logger;

public abstract class GameClient extends NetworkInterface {
	private static GameClient INSTANCE;
	public boolean running = false;
	
	public GameClient(String serverIPAddress, int serverPort) {
		super(serverIPAddress, serverPort);
		if(INSTANCE == null) INSTANCE = this;
		else Logger.log("A GameClient is already running! Is this a mistake?");
	}
	
	public void parsePacket(byte[] data, InetAddress ipAddress, int port) {
		super.parsePacket(data, ipAddress, port);
		handlePacket(new String(data).trim(), ipAddress, port);
	}
	
	public abstract void handlePacket(String message, InetAddress ipAddress, int port);
	
	public void stop() {
		super.stop();
		GameClient.destroyInstancedClient();
	}
	
	protected static void destroyInstancedClient() {
		INSTANCE.running = false;
		INSTANCE.socket.close();
		INSTANCE = null;
	}

	public static GameClient getInstance() {
		return INSTANCE;
	}
}
