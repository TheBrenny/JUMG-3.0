package com.thebrenny.jumg.entities.ai.pathfinding;

import java.awt.geom.Point2D;

import com.thebrenny.jumg.level.Level;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.util.Logger;
import com.thebrenny.jumg.util.Logger.LoggerNode;
import com.thebrenny.jumg.util.MathUtil;
import com.thebrenny.jumg.util.VectorUtil;

public class PathFinding {
	private static final float STRAIGHT_COST = 1.0F;
	private static final float DIAGONAL_COST = (float) Math.sqrt(2);
	private NodeList open;
	private NodeList closed;
	private NodeList path;
	
	private Node start;
	private Node goal;
	private Level level;
	
	public PathFinding(Point2D.Float start, Point2D.Float goal, Level level) {
		this.start = new Node(null, start);//VectorUtil.toFloatPoint(Level.roundTileCoords(start.x, start.y)));
		this.start.setCost(0, 0);
		this.goal = new Node(null, goal);//VectorUtil.toFloatPoint(Level.roundTileCoords(goal.x, goal.y)));
		this.goal.setCost(0, 0);
		this.level = level;
		
		open = new NodeList();
		closed = new NodeList();
		
		open.add(0, this.start);
	}
	
	public NodeList search(float radius, boolean lineOfSightRequired) {
		LoggerNode pfLogNode = Logger.startSection("pathfind", "Finding a path between " + start.getPoint().toString() + " and " + goal.getPoint().toString() + ". (HC: " + this.hashCode() + ")");

		Node current = null;
		Node c;
		Tile t;
		float alpha = 1;
		
		while(open.size() > 0) {
			current = open.pop();
			closed.add(current);
			
			if(current == goal || (MathUtil.distanceSqrd(current.getPoint(), goal.getPoint()) <= radius * radius && (!lineOfSightRequired || level.unobstructedTiles(current.getPoint(), goal.getPoint())))) {
				this.path = new NodeList();
				while(current != null) {
					// while I can still see my parent, traverse until I cant, then get the child
					// if it doesn't have a parent, add it, because that's the start.
					this.path.push(current);
					current = current.getParent();
				}
				Logger.endSection(pfLogNode, "Pathfinding done for HC " + hashCode());
				return path;
			}
			
			for(int x = -1; x <= 1; x++) {
				for(int y = -1; y <= 1; y++) {
					if(Math.abs(x) == Math.abs(y)) continue;
					// The above skips diagonals and (0, 0).
					// do a quick think, if x == -1 and y == 1, then it becomes 1 == 1!
					
					c = new Node(current, VectorUtil.translatePoint(current.getPoint(), x, y));
					t = level.getTileRelative(c.getPoint().x, c.getPoint().y);
					if(t != null && t.canTraverseOnFoot() && !closed.contains(c)) {
						c.setCost(current.getG() + 1, MathUtil.distance(c.getPoint(), goal.getPoint()));
						open.tryOverride(c);
					}
				}
			}
		}
		
		Logger.endSection(pfLogNode, "Pathfinding done for HC " + hashCode());
		NodeList failed = new NodeList();
		failed.push(this.start);
		return failed;
	}
	
	public NodeList getPath() {
		if(this.path == null) Logger.log("Getting a null path!");
		return this.path;
	}
}
