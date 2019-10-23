package com.thebrenny.jumg.util;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;

public class Logger {
	public static HashMap<String, LoggerNode> sectionNodes = new HashMap<String, LoggerNode>();
	public static ArrayList<String> sectionTree = new ArrayList<String>();
	public static String[] lastSavedTree = null;
	public static LoggerNode currentSection;
	public static boolean shouldSave = false;
	private static int LOG_CALLER_LENGTH = 0;
	private static int PAC_CALLER_LENGTH = 0;
	
	public static void log(String message, Object ... objects) {
		log(StringUtil.insert(message, objects), 1);
	}
	public static void log(String msg) {
		log(msg, 1);
	}
	public static void log(String message, int stackTraceShift) {
		if(canLogOutput()) {
			StackTraceElement ste = Thread.currentThread().getStackTrace()[2 + stackTraceShift];
			String logCaller = "[" + ste.getClassName().substring(ste.getClassName().lastIndexOf(".") + 1) + "." + ste.getMethodName() + "] ";
			if(logCaller.length() < LOG_CALLER_LENGTH) logCaller = StringUtil.padTo(logCaller, LOG_CALLER_LENGTH, ".", true);
			else LOG_CALLER_LENGTH = logCaller.length();
			System.out.println(logCaller.substring(0, logCaller.length() - 1) + " > " + message);
		}
	}
	
	public static void logPacket(boolean inbound, String address, String port, String message) {
		if(canLogOutput()) {
			String pacCaller = "[" + (inbound ? "IN FROM " : "OUT TO  ") + address + ":" + port + "] ";
			if(pacCaller.length() < PAC_CALLER_LENGTH) pacCaller = StringUtil.padTo(pacCaller, PAC_CALLER_LENGTH, ".", true);
			else PAC_CALLER_LENGTH = pacCaller.length();
			
			// This is here while the logging happens to the same console.
			// TODO: Make it so they can be printed to a chose output stream.
			if(PAC_CALLER_LENGTH < LOG_CALLER_LENGTH) PAC_CALLER_LENGTH = LOG_CALLER_LENGTH;
			else LOG_CALLER_LENGTH = PAC_CALLER_LENGTH;
			
			System.out.println(pacCaller.substring(0, pacCaller.length() - 1) + " > " + message);
		}
	}
	public static void logInPacket(DatagramPacket packet) {
		logInPacket(packet.getAddress().getHostAddress(), packet.getPort() + "", new String(packet.getData()).trim());
	}
	public static void logInPacket(String address, String port, String message) {
		logPacket(true, address, port, message);
	}
	public static void logOutPacket(DatagramPacket packet) {
		logOutPacket(packet.getAddress().getHostAddress(), packet.getPort() + "", new String(packet.getData()).trim());
	}
	public static void logOutPacket(String address, String port, String message) {
		logPacket(false, address, port, message);
	}
	
	public static void error(String message, Exception e) {
		// TODO: Insert code to print the error message
		// This is so all errors are logged in a standardised way!
	};
	
	private static boolean canLogOutput() {
		return ArgumentOrganizer.getOrganizedArguments() != null && ArgumentOrganizer.getOrganizedArguments().boolVal("debug");
	}
	
	public static LoggerNode startSection(String section, String message) {
		if(message != null) log(message, 1);
		if(!sectionNodes.containsKey(section)) sectionNodes.put(section, new LoggerNode(section));
		currentSection = sectionNodes.get(section);
		currentSection.setState(true);
		return currentSection;
	}
	public static LoggerNode endLatestSection(String message) {
		currentSection = endSection(currentSection, message, "");
		return currentSection;
	}
	public static LoggerNode endSection(String section, String message) {
		LoggerNode ln = sectionNodes.get(section);
		if(ln == null) {
			Logger.log("No found section with name [" + section + "]");
			return null;
		}
		return endSection(ln, message, "");
	}
	public static LoggerNode endSection(LoggerNode section, String ... message) {
		if(section == null) {
			Logger.log("Passed section is null!", 1);
			return null;
		}
		section.setState(false);
		if(message.length >= 1 && message[0] != null) {
			int stackShift = message.length > 1 ? 2 : 1;
			log(message[0], stackShift);
			log("    Time taken: " + section.getElapsedTime() + "ms", stackShift);
		}
		//drawSectionToTree(section);
		return section.parent;
	}
	
	/*
	 * public static void drawSectionToTree(LoggerNode ln) {
	 * sectionTree.add(StringUtil.insertMany("{0}| {1} | {2}ms",
	 * StringUtil.multiply("-", ln.getDepth()), ln.name, ln.lastElapsedTime));
	 * }
	 * public static void saveTree(boolean save) {
	 * shouldSave = save;
	 * }
	 * public static String[] savedTree() {
	 * String[] clone = lastSavedTree;
	 * lastSavedTree = null;
	 * return clone;
	 * }
	 * public static void refreshTree() {
	 * if(shouldSave) {
	 * Collections.reverse(sectionTree);
	 * lastSavedTree = sectionTree.toArray(new String[] {});
	 * shouldSave = false;
	 * }
	 * sectionTree.clear();
	 * }
	 */
	public static void newThread() {
		currentSection = null;
	}
	
	public static class LoggerNode {
		private final LoggerNode parent;
		private final String name;
		private boolean open = false;
		private long startTime = 0;
		private long lastElapsedTime = 0;
		
		public LoggerNode(String name) {
			this(name, currentSection);
		}
		
		public LoggerNode(String name, LoggerNode parent) {
			this.name = name;
			this.parent = parent;
		}
		
		public void setState(boolean open) {
			this.open = open;
			if(open) startTime = TimeUtil.getEpoch();
			else lastElapsedTime = TimeUtil.getElapsed(startTime);
		}
		
		public LoggerNode getParent() {
			return parent;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean isOpen() {
			return open;
		}
		
		public long getStartTime() {
			return startTime;
		}
		
		public long getLastElapsedTime() {
			return lastElapsedTime;
		}
		
		public int getDepth() {
			int depth = 0;
			LoggerNode ln = this;
			while(ln.parent != null) {
				depth += 1;
				ln = ln.parent;
			}
			return depth;
		}
		
		public long getElapsedTime() {
			return this.open ? TimeUtil.getElapsed(startTime) : lastElapsedTime;
		}
	}
}
