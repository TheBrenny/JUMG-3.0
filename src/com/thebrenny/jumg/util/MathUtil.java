package com.thebrenny.jumg.util;

import java.awt.geom.Point2D;
import java.util.Random;

public class MathUtil {
	/**
	 * Clamps the value between the minimum and maximum. If the max value is
	 * less than the min value, the function will automatically switch them to
	 * give a correct result.
	 * 
	 * @param min
	 *        The minimum value
	 * @param num
	 *        The number to clamp
	 * @param max
	 *        The maximum value
	 * @return The clamped value.
	 */
	public static int clamp(int min, int num, int max) {
		return (int) clamp((float) min, (float) num, (float) max);
	}
	
	/**
	 * See {@link #clamp(int, int, int)}.
	 */
	public static float clamp(float min, float num, float max) {
		if(max < min) {
			float t = max;
			max = min;
			min = t;
		}
		return num >= max ? max : num <= min ? min : num;
	}
	
	/**
	 * Maps the number from the old scale to the new scale.
	 * 
	 * @param num
	 *        The number to scale
	 * @param oldLow
	 *        The original low value
	 * @param oldHigh
	 *        The original high value
	 * @param newLow
	 *        The new low value
	 * @param newHigh
	 *        The new high value
	 * @return The scaled {@code num} value.
	 */
	public static int map(int num, int oldLow, int oldHigh, int newLow, int newHigh) {
		return ((num - oldLow) / (oldHigh - oldLow)) * (newHigh - newLow) + newLow;
	}
	
	/**
	 * See {@link #map(int, int, int, int, int)}.
	 */
	public static float map(float num, float oldLow, float oldHigh, float newLow, float newHigh) {
		return ((num - oldLow) / (oldHigh - oldLow)) * (newHigh - newLow) + newLow;
	}
	
	/**
	 * Clamps the number but makes sure it wraps around.
	 * 
	 * @param min
	 *        The minimum value, inclusive in the range
	 * @param num
	 *        The number to wrap around
	 * @param max
	 *        The maximum value, inclusive in the range.
	 * @return A number within the range, wrapped around.
	 */
	public static int wrap(int min, int num, int max) {
		return (int) wrap((float) min, (float) num, (float) max);
	}
	/**
	 * See {@link #wrap(int, int, int)}.
	 */
	public static float wrap(float min, float num, float max) {
		float c = max - min;
		num -= min;
		num %= c;
		if(num < 0.0F) num += c;
		return num + min;
	}
	
	/**
	 * Returns a pseudo-random number between min (inclusive) and max
	 * (exclusive).
	 * 
	 * @param min
	 *        The minimum boundary
	 * @param max
	 *        The maximum boundary
	 * @return The pseudo-random number.
	 * @see Random#nextInt(int)
	 */
	public static int random(int min, int max) {
		return random(min, max, new Random().nextLong());
	}
	
	/**
	 * Returns a pseudo-random number between min (inclusive) and max
	 * (exclusive) using a predetermined seed.
	 * 
	 * @param min
	 *        The minimum boundary
	 * @param max
	 *        The maximum boundary
	 * @param seed
	 *        The seed for the RNG
	 * @return The pseudo-random number.
	 * @see Random#nextInt(int)
	 */
	public static int random(int min, int max, long seed) {
		return random(min, max, new Random(seed));
	}
	/**
	 * Returns a pseudo-random number between min (inclusive) and max
	 * (exclusive) using a predetermined seed.
	 * 
	 * @param min
	 *        The minimum boundary
	 * @param max
	 *        The maximum boundary
	 * @param random
	 *        The {@code Random} object to use
	 * @return The pseudo-random number.
	 */
	public static int random(int min, int max, Random random) {
		return random.nextInt(max - min) + min;
	}
	
	/**
	 * See {@link MathUtil#random(int, int)}.
	 */
	public static float random(float min, float max) {
		return random(min, max, new Random().nextLong());
	}
	/**
	 * See {@link MathUtil#random(int, int, long)}.
	 */
	public static float random(float min, float max, long seed) {
		return random(min, max, new Random(seed));
	}
	/**
	 * See {@link MathUtil#random(int, int, Random)}.
	 */
	public static float random(float min, float max, Random random) {
		return random.nextFloat() * (max - min) + min;
	}
	
	/**
	 * Converts a number to a boolean array of all bits.
	 * 
	 * @param num
	 *        The number to convert
	 * @param size
	 *        The size of the resulting bit array - typically multiples of 2
	 * @return The bit array represented as a primitive boolean array
	 * @see #fromBitArray(boolean[])
	 */
	public static boolean[] toBitArray(int num, int size) throws Exception {
		if(size <= 0) throw new Exception("Passed size (" + size + ") must be greater than 0!");
		boolean[] b = new boolean[size];
		for(int a = 0; a < size; a++) {
			b[b.length - 1 - a] = (num & 0b1) == 1;
			num = (char) (num >> 1);
		}
		return b;
	}
	/**
	 * Converts a boolean array of bits into a number.
	 * 
	 * @param data
	 *        The bit array
	 * @return A long. To ensure all bits can be included.
	 * @throws Exception
	 *         If the length of data is larger than Long.SIZE (64).
	 * @see #toBitArray(int, int)
	 */
	public static long fromBitArray(boolean[] data) throws Exception {
		if(data.length > Long.SIZE) throw new Exception("The length of data must be less than " + Long.SIZE + ". It was " + data.length);
		long l = 0;
		for(int a = 0; a < data.length; a++) {
			l = (long) ((l << 1) | (data[a] ? 1 : 0));
		}
		return l;
	}
	
	/**
	 * Short-hand for {@link MathUtil#distanceSqrd(Point2D, Point2D)};
	 */
	public static float distanceSqrd(float x1, float y1, float x2, float y2) {
		double x = x2 - x1;
		double y = y2 - y1;
		return (float) (x * x + y * y);
	}
	
	/**
	 * Gets the distance<sup>2</sup> between the two points {@code p1} and
	 * {@code p2}. <em>Square-rooting the answer can take time, so comparing
	 * {@code c}<sup>2</sup>s are quicker.</em>
	 * 
	 * @param p1
	 *        The start point
	 * @param p2
	 *        The destination point
	 * @return The squared distance.
	 */
	public static float distanceSqrd(Point2D p1, Point2D p2) {
		return distanceSqrd((float) p1.getX(), (float) p1.getY(), (float) p2.getX(), (float) p2.getY());
	}
	
	public static float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(distanceSqrd(x1, y1, x2, y2));
	}
	
	public static float distance(Point2D p1, Point2D p2) {
		return (float) Math.sqrt(distanceSqrd(p1, p2));
	}
	
	public static float lerp(float min, float max, float t) {
		return min + t * (max - min);
	}
	
	public static interface LinearFunction {
		float eq(float n, float m);
	}
}
