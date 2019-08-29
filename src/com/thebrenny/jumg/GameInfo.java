package com.thebrenny.jumg;

public class GameInfo {
	private String gameName;
	private String packageRoot;
	
	public GameInfo(String gameName, String packageRoot) {
		this.gameName = gameName;
		this.packageRoot = packageRoot;
	}
	public String gameName() {
		return gameName;
	}
	public String packageRoot() {
		return packageRoot;
	}
}