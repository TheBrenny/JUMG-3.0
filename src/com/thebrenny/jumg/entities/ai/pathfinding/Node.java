package com.thebrenny.jumg.entities.ai.pathfinding;

import java.awt.geom.Point2D;

import com.thebrenny.jumg.util.StringUtil;

public class Node {
	private Node parent;
	private Point2D point;
	
	private float f; // Total Cost
	private float g; // Distance from start
	private float h; // Estimated distance to end
	
	public Node(Node parent, Point2D point) {
		this.parent = parent;
		this.point = point;
		
		this.f = 0;
		this.g = 0;
		this.h = 0;
	}
	
	public void setCost(float g, float h) {
		this.g = g;
		this.h = h;
		this.f = g + h;
	}
	
	public Node getParent() {
		return this.parent;
	}
	public Point2D.Float getPoint() {
		return new Point2D.Float((float) this.point.getX(), (float) this.point.getY());
	}
	public float getG() {
		return this.g;
	}
	public float getH() {
		return this.h;
	}
	public float getCost() {
		return this.f;
	}
	
	public boolean equals(Object obj) {
		return obj instanceof Node && ((Node) obj).getPoint() == this.point;
	}

	public String toString() {
		return StringUtil.insert("{0}[pos:({1},{2}), f={3}={4}+{5}]", getClass().getSimpleName(), getPoint().getX(), getPoint().getY(), getCost(), getG(), getH());
	}
}
