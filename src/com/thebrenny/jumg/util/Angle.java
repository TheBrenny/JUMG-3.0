package com.thebrenny.jumg.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.util.VectorUtil.Vector;

/**
 * This class is a simplified way to handle angles. This class allows for easier
 * access to creating, adjusting, messing with angles. This class also allows
 * for the creation of rotated images.
 * 
 * @author TheBrenny
 */
public class Angle {
	public static final Angle NORTH = new Angle.Final(0);
	public static final Angle EAST = new Angle.Final(90);
	public static final Angle SOUTH = new Angle.Final(180);
	public static final Angle WEST = new Angle.Final(270);
	public static final float MIN_ANGLE = 0.0F;
	public static final float MAX_ANGLE = 360.0F;
	
	protected float angle;
	
	/**
	 * Creates an angle object using the angle provided.
	 * 
	 * @param ang
	 *        The angle to make.
	 */
	public Angle(float angle) {
		setAngle(angle);
	}
	
	/**
	 * Creates an angle by using a previously made angle (essentially
	 * duplication).
	 * 
	 * @param angle
	 *        The angle to duplicate.
	 */
	public Angle(Angle angle) {
		this(angle.angle);
	}
	
	/**
	 * Changes the angle by adding on the amount passed to the current angle.
	 * The angle will always be in the bounds of 0 and 360.
	 * 
	 * @param amount
	 *        The amount to add to the angle.
	 * @return {@code this}.
	 */
	public Angle changeAngle(float amount) {
		this.angle = MathUtil.wrap(0, this.angle + amount, Angle.MAX_ANGLE);
		return this;
	}
	
	/**
	 * Sets the angle to the specified angle. This completely overwrites the
	 * previously used angle.
	 * 
	 * @param angle
	 *        The new angle
	 * @return {@code this}.
	 */
	public Angle setAngle(float angle) {
		this.angle = MathUtil.wrap(0, angle, Angle.MAX_ANGLE);
		return this;
	}
	
	public Angle snapAngleTo(float degrees) {
		float angOffset = getAngle() % degrees;
		boolean roundUp = Math.max(angOffset, degrees / 2.0F) >= degrees / 2.0F;
		
		if(roundUp) this.changeAngle(degrees - angOffset);
		else this.changeAngle(-angOffset);
		
		return this;
	}
	
	/**
	 * Gets the {@link AngleSpeed} of a currentSpeed at this angle. See
	 * {@link AngleSpeed#getAngleSpeed(Angle, float)}.
	 * 
	 * @param currentSpeed
	 *        The currentSpeed to convert
	 * @return {@link AngleSpeed} instance.
	 */
	public AngleSpeed getAngleSpeed(float speed) {
		return AngleSpeed.getAngleSpeed(this.angle, speed);
	}
	
	public float getAngle() {
		return this.angle;
	}
	
	public boolean equals(Object o) {
		return o instanceof Angle && ((Angle) o).angle == angle;
	}
	
	public String toString() {
		return StringUtil.insert("Angle[angle={0}]", this.angle);
	}
	
	/**
	 * Calculates a new angle between two points.
	 * 
	 * @param source
	 *        The source point
	 * @param destination
	 *        The destination point
	 * @return A calculated float
	 */
	public static float getAngle(Point2D source, Point2D destination) {
		return getAngle((float) source.getX(), (float) source.getY(), (float) destination.getX(), (float) destination.getY());
	}
	
	/**
	 * Short-hand for {@link Angle#getAngle(Point2D, Point2D)}.
	 */
	public static float getAngle(float x1, float y1, float x2, float y2) {
		return (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90.0F;
	}
	
	/**
	 * Returns the rotated version of an image passed, using this angle.
	 * Short-hand for the static method
	 * {@link #getRotation(BufferedImage, float)}.
	 * 
	 * @param bi
	 *        The image to rotate
	 * @return The rotated image.
	 */
	public BufferedImage getRotation(BufferedImage bi) {
		return Angle.getRotation(bi, angle);
	}
	
	/**
	 * Returns the rotated version of an image passed, using this angle, and
	 * anchored at {@code (anchorX, anchorY)} of the image.
	 * 
	 * @param bi
	 *        The image to rotate
	 * @param anchorX
	 *        The x position of the anchor
	 * @param anchorY
	 *        The y position of the anchor
	 * @return The rotated image.
	 */
	public BufferedImage getRotation(BufferedImage bi, float anchorX, float anchorY) {
		return Angle.getRotation(bi, angle, anchorX, anchorY);
	}
	
	/**
	 * Returns the rotated version of an image passed, anchored to the centre,
	 * at the angle passed.
	 * 
	 * @param bi
	 *        The image to rotate
	 * @param angle
	 *        The angle to rotate by
	 * @return The rotated image.
	 */
	public static BufferedImage getRotation(BufferedImage bi, float angle) {
		return Angle.getRotation(bi, angle, (float) (bi.getWidth() / 2), (float) (bi.getHeight() / 2));
	}
	
	/**
	 * Returns the rotated version of an image passed, at the angle passed,
	 * anchored at {@code (anchorX, anchorY)} of the image.
	 * 
	 * @param bi
	 *        The image to rotate
	 * @param angle
	 *        The angle to rotate by
	 * @param anchorX
	 *        The x position of the anchor
	 * @param anchorY
	 *        The y position of the anchor
	 * @return The rotated image.
	 */
	public static BufferedImage getRotation(BufferedImage bi, float angle, float anchorX, float anchorY) {
		AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(angle), anchorX, anchorY);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return op.filter(bi, null);
	}
	
	/**
	 * This class gives values as to how fast along the x and y axis an object
	 * should be moving.
	 * 
	 * @author TheBrenny
	 */
	public static class AngleSpeed extends Vector {
		protected float xSpeed;
		protected float ySpeed;
		
		/**
		 * Constructs and AngleSpeed object by using an existing AngleSpeed
		 * object.
		 * 
		 * @param old
		 *        The AngleSpeed to copy
		 */
		public AngleSpeed(AngleSpeed old) {
			this(old.distance, old.xSpeed, old.ySpeed, old.angle.getAngle());
		}
		
		/**
		 * Constructs an AngleSpeed object by using pre-computed values.
		 * 
		 * @param speed
		 *        The original speed of this vector
		 * @param xSpeed
		 *        The currentSpeed along the x axis
		 * @param ySpeed
		 *        The currentSpeed along the y axis
		 * @param angle
		 *        The directing angle
		 */
		public AngleSpeed(float speed, float xSpeed, float ySpeed, float angle) {
			super(angle, speed);
			this.xSpeed = xSpeed;
			this.ySpeed = ySpeed;
		}
		
		/**
		 * Adds an angle currentSpeed to this one by adding the two x and y
		 * speeds.
		 * 
		 * @param add
		 *        The {@code AngleSpeed} to add
		 * @return {@code this}.
		 */
		public AngleSpeed addAngleSpeed(AngleSpeed add) {
			this.xSpeed += add.xSpeed;
			this.ySpeed += add.ySpeed;
			this.distance = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);
			this.angle.setAngle(Angle.getAngle(0, 0, this.xSpeed, this.ySpeed));
			return this;
		}
		
		/**
		 * Returns the speed which this object is based off.
		 */
		public float getSpeed() {
			return this.distance;
		}
		
		/**
		 * Returns the x speed associated with this Angle Speed object.
		 */
		public float getXSpeed() {
			return xSpeed;
		}
		
		/**
		 * Returns the y speed associated with this Angle Speed object.
		 */
		public float getYSpeed() {
			return ySpeed;
		}
		
		/**
		 * {@inheritdoc}
		 */
		public String toString() {
			return StringUtil.insert("AngleSpeed[s={0}, xs={1}, ys={2}, a={3}]", this.distance, this.xSpeed, this.ySpeed, this.angle);
		}
		
		public boolean equals(Object obj) {
			return obj instanceof AngleSpeed && ((AngleSpeed) obj).xSpeed == this.xSpeed && ((AngleSpeed) obj).ySpeed == this.ySpeed; // && super.equals(obj); -- may not always be the case...
		}
		
		/**
		 * Calculates the speed along the x-axis and y-axis for a
		 * speed at the specified angle.
		 * 
		 * @param angle
		 *        The angle to calculate with
		 * @param speed
		 *        The speed to calculate with
		 * 
		 * @return a new {@link AngleSpeed} instance.
		 */
		public static AngleSpeed getAngleSpeed(float angle, float speed) {
			float xSpeed = 0.0F;
			float ySpeed = 0.0F;
			
			double sin = Math.sin(Math.toRadians(angle));
			double cos = Math.cos(Math.toRadians(angle));
			
			xSpeed = (float) sin * speed;
			ySpeed = (float) -cos * speed;
			
			return new AngleSpeed(speed, xSpeed, ySpeed, angle);
		}
		
		/**
		 * Calculates the speed along the x-axis and y-axis for a
		 * speed at the specified angle.
		 * 
		 * @param v
		 *        The Vector to use
		 * 
		 * @return a new {@link AngleSpeed} instance.
		 * @see #getAngleSpeed(float, float)
		 */
		public static AngleSpeed getAngleSpeed(Vector v) {
			return getAngleSpeed(v.angle.getAngle(), v.distance);
		}
	}
	
	public static class Relative extends Angle {
		public Relative(Angle angle) {
			this(angle.getAngle());
		}
		public Relative(float angle) {
			super(angle);
		}
		public Relative changeAngle(float amount) {
			this.angle = (this.angle + amount) % Angle.MAX_ANGLE;
			return this;
		}
		public Relative setAngle(float angle) {
			this.angle = angle % Angle.MAX_ANGLE;
			return this;
		}
	}
	
	public static class Final extends Angle {
		public Final(Angle angle) {
			this(angle.getAngle());
		}
		public Final(float angle) {
			super(angle);
			super.setAngle(angle);
		}
		public final Final changeAngle(float amount) {
			return this;
		}
		public final Final setAngle(float angle) {
			return this;
		}
	}
}
