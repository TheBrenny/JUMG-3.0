package com.thebrenny.jumg.entities.ai.pathfinding;

import java.awt.geom.Point2D;

public class Node {
	private Node parent;
	private Node child;
	private Point2D.Float point;
	
	private float f; // Total Cost
	private float g; // Distance from start
	private float h; // Estimated distance to end
	
	public Node(Node parent, Point2D.Float point) {
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
	public void setChild(Node child) {
		this.child = child;
	}
	
	public Node getParent() {
		return this.parent;
	}
	public Node getChild() {
		return this.child;
	}
	public Point2D.Float getPoint() {
		return this.point;
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
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Node && ((Node) obj).getPoint() == this.point;
	}
}
