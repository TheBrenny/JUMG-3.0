package com.thebrenny.jumg.util;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

import com.thebrenny.jumg.level.Level;
import com.thebrenny.jumg.util.Angle.AngleSpeed;

public class VectorUtil {
	/**
	 * Casts the given {@code ray} and determines if it collides with the line
	 * defined by {@code p1} and {@code p2}.
	 * 
	 * @return A clone of {@code ray}, with the distance clamped to the
	 *         collision if there is one.
	 */
	public static Ray castRay(Ray ray, Point2D p1, Point2D p2) {
		Point2D.Float rayStart = ray.getLocation();
		Point2D.Float rayEnd = ray.getEndLocation();
		Point2D.Float rayCollide = lineIntersection(p1, p2, rayStart, rayEnd);
		float rayCollideDist = (float) rayCollide.distance(rayStart);
		if(rayCollideDist < ray.getDistance() && doesIntersect(p1, p2, rayStart, rayEnd)) ray = new Ray(rayStart, ray.getAngle().getAngle(), rayCollideDist);
		return new Ray(ray);
	}
	
	public static Point2D.Float lineIntersection(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
		float s = (float) (((p4.getX() - p3.getX()) * (p1.getY() - p3.getY()) - (p4.getY() - p3.getY()) * (p1.getX() - p3.getX())) / ((p4.getY() - p3.getY()) * (p2.getX() - p1.getX()) - (p4.getX() - p3.getX()) * (p2.getY() - p1.getY())));
		return new Point2D.Float((float) (p1.getX() + s * (p2.getX() - p1.getX())), (float) (p1.getY() + s * (p2.getY() - p1.getY())));
	}
	public static boolean doesIntersect(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
		return Line2D.linesIntersect(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY());
	}
	
	public static Point2D.Float lerp(Point2D p, Point2D q, float f) {
		return new Point2D.Float(MathUtil.lerp((float) p.getX(), (float) q.getX(), f), MathUtil.lerp((float) p.getY(), (float) q.getY(), f));
	}

	public static final Point2D.Float translatePoint(Point2D point, float x, float y) {
		return new Point2D.Float((float) point.getX() + x, (float) point.getY() + y);
	}
	public static final Point toIntPoint(Point2D p) {
		return new Point((int) p.getX(), (int) p.getY());
	}
	public static final Point2D.Float toFloatPoint(Point2D p) {
		return new Point2D.Float((float) p.getX(), (float) p.getY());
	}
	
	public static class Vector {
		public Angle angle;
		public float distance;
		
		public Vector(float angle, float distance) {
			this(new Angle(angle), distance);
		}
		public Vector(Angle angle, float distance) {
			this.angle = angle;
			this.distance = distance;
		}
		
		public Angle getAngle() {
			return this.angle;
		}
		public float getDistance() {
			return this.distance;
		}
		
		public String toString() {
			return getClass().getSimpleName() + StringUtil.insert("[angle={0},dist={1}]", angle, distance);
		}
		
		public boolean equals(Object obj) {
			return obj instanceof Vector && ((Vector) obj).angle.equals(this.angle) && ((Vector) obj).distance == this.distance;
		}
	}
	
	public static class Ray extends Vector {
		public Point2D location;
		
		public Ray(Point2D origin, Point2D destination) {
			this(origin, Angle.getAngle(origin, destination), MathUtil.distance(origin, destination));
		}
		public Ray(Point2D location, Vector v) {
			this(location, v.angle.getAngle(), v.distance);
		}
		public Ray(Point2D location, float angle) {
			this(location, angle, Float.MAX_VALUE / 2);
		}
		public Ray(Point2D location, float angle, float distance) {
			super(angle, distance);
			this.location = location;
		}
		public Ray(Ray ray) {
			this(new Point2D.Float((float) ray.location.getX(), (float) ray.location.getY()), ray.angle.getAngle(), ray.distance);
		}
		
		public Point2D.Float getLocation() {
			if(!(this.location instanceof Point2D.Float)) this.location = new Point2D.Float((float) this.location.getX(), (float) this.location.getY());
			return (Point2D.Float) this.location;
		}
		public Point2D.Float getRelativeEndLoaction() {
			AngleSpeed as = getAngleSpeed();
			return new Point2D.Float(as.getXSpeed(), as.getYSpeed());
		}
		public Point2D.Float getEndLocation() {
			Point2D.Float rel = getRelativeEndLoaction();
			return new Point2D.Float(this.getLocation().x + rel.x, this.getLocation().y + rel.y);
		}
		
		public AngleSpeed getAngleSpeed() {
			return AngleSpeed.getAngleSpeed(this);
		}
		
		public String toString() {
			return StringUtil.insert("Ray[loc={0},angle={1},dist={2}]", location, angle, distance);
		}
		
		public boolean equals(Object obj) {
			return obj instanceof Ray && ((Ray) obj).location.equals(this.location) && super.equals(obj);
		}
	}
	
	public static class Visibility {
		public static final Comparator<EndPoint> END_POINT_COMPARE = new Comparator<EndPoint>() {
			public int compare(EndPoint a, EndPoint b) {
				if(a.angle > b.angle) return 1;
				if(a.angle < b.angle) return -1;
				
				if(!a.begin && b.begin) return 1;
				if(a.begin && !b.begin) return -1;
				
				return 0;
			}
		};
		
		public LinkedList<EndPoint> endPoints; // all endpoints
		public LinkedList<Segment> segments; // all segments
		public LinkedList<Segment> open; // open segments
		public ArrayList<Point2D> output; // series of points to form a visible area polygon
		
		public Level level;
		public Point2D centre; // the centre of visibility
		public float radius;
		
		public boolean ready = false;
		
		public Visibility(Level level, float x, float y, float radius) {
			this(level, new Point2D.Float(x, y), radius);
		}
		public Visibility(Level level, Point2D centre, float radius) {
			this.level = level;
			this.centre = centre;
			this.radius = radius;
			
			segments = new LinkedList<Segment>();
			endPoints = new LinkedList<EndPoint>();
			open = new LinkedList<Segment>();
			output = new ArrayList<Point2D>();
			
			// load map data here and await for an execute.
			// For ease of math sake, we will only have the zombies
			//   call it if within 6 chunks of player - this is a
			//   level thing to manage.
			// Player will always call it.
		}
		
		private class TileBlob {
			private Polygon polygon;
			
			public TileBlob(Point[] tiles) {
				int length = tiles.length;
				int[] xPoints = new int[length];
				int[] yPoints = new int[length];
				for(int i = 0; i < length; i++) {
					xPoints[i] = tiles[i].x;
					yPoints[i] = tiles[i].y;
				}
				this.polygon = new Polygon(xPoints, yPoints, length);
			}
		}
		
		private class EndPoint extends Point2D.Float {
			private static final long serialVersionUID = 1L;
			public boolean begin = false;
			public Segment segment = null;
			public float angle = 0.0F;
		}
		
		private class Segment {
			public EndPoint p1;
			public EndPoint p2;
			public float d; // distance/length
		}
	}
}
