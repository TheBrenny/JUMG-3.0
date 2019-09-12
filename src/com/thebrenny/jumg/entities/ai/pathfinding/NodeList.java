package com.thebrenny.jumg.entities.ai.pathfinding;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;

public class NodeList extends ArrayList<Node> {
	private static final long serialVersionUID = 3063720407053093925L;
	public static final float DEFAULT_TEST_DISTANCE = 0.1F;
	
	public static final int PROXIMITY_TEST_FAILED = 0;
	public static final int PROXIMITY_TEST_REACHED = 1;
	public static final int PROXIMITY_TEST_COMPLETE = 2;
	
	private int nodeIndex = 0;
	
	public static final Comparator<Node> NODE_COMPARATOR = new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
			return n1.getCost() < n2.getCost() ? -1 : n1.getCost() > n2.getCost() ? 1 : n1.getH() < n2.getH() ? -1 : n1.getH() > n2.getH() ? 1 : 0;
			// First compare the costs, but in the case of a tie compare the hueristic.
		}
	};
	
	public Node peek() {
		return this.get(0);
	}
	public Node pop() {
		Node n = peek();
		this.remove(0);
		return n;
	}
	public void push(Node n) {
		this.add(0, n);
		this.sort(NODE_COMPARATOR);
	}
	
	public Node getCurrentStep() {
		return this.get(nodeIndex);
	}
	public int testStepReached(float x, float y, float dist) {
		Point2D.Float p = getCurrentStep().getPoint();
		if(Math.abs(p.x - x) <= dist && Math.abs(p.y - y) <= dist) {
			int ret = PROXIMITY_TEST_REACHED;
			if(nodeIndex == this.size() - 1) ret = PROXIMITY_TEST_COMPLETE;
			else nodeIndex++;
			return ret;
		}
		return PROXIMITY_TEST_FAILED;
	}
	
	/**
	 * Places the given node in the list after it deletes it. This is used when
	 * a node with a lower g cost (distance from start) is found.
	 * 
	 * @param n
	 *        - The new node.
	 */
	public void tryOverride(Node n) {
		int overrideIndex = this.indexOf(n);
		if(overrideIndex != -1 && this.get(overrideIndex).getG() >= n.getG()) this.remove(overrideIndex);
		if(!this.contains(n)) this.push(n);
	}
}
