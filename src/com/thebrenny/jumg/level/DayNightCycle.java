package com.thebrenny.jumg.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.engine.Engine;
import com.thebrenny.jumg.util.MathUtil;
import com.thebrenny.jumg.util.StringUtil;

public class DayNightCycle {
	public static final int PRE_DAWN =	0b00001; // This is so they can be bitmasked!
	public static final int SUNRISE =	0b00010;
	public static final int DAYTIME =	0b00100;
	public static final int SUNSET =	0b01000;
	public static final int POST_DUSK =	0b10000;
	
	private static final int SECONDS_PER_MINUTE = 60;
	private static final int MINUTES_PER_HOUR = 60;
	private static final int HOURS_PER_DAY = 24;
	
	private static final float MIN_ALPHA = 0.0F;
	private static final float MAX_ALPHA = 0.7F;
	private static final float MIN_TIME = 0.0F;
	
	private static final float ONE_SECOND = 1.0F;
	private static final float ONE_MINUTE = SECONDS_PER_MINUTE * ONE_SECOND;
	private static final float ONE_HOUR = MINUTES_PER_HOUR * ONE_MINUTE;
	private static final float ONE_DAY = HOURS_PER_DAY * ONE_HOUR;
	private static final float MAX_TIME = ONE_DAY;
	
	private float sunriseStart = 6 * ONE_HOUR;
	private float sunriseEnd = 9 * ONE_HOUR;
	private float sunsetStart = 16 * ONE_HOUR;
	private float sunsetEnd = 19 * ONE_HOUR;
	
	private long day;
	private float time;
	private float secondPerTick;
	
	public DayNightCycle() {
		this(90.0F); // 90 / ticks per second = means 90 in-game seconds per second
	}
	
	public DayNightCycle(float ingameSecondsPerRealSecond) {
		this(0, 10, 00, 0.0F, ingameSecondsPerRealSecond / Engine.getMaxTPS());
	}
	
	public DayNightCycle(long day, float hour, float minute, float second, float secondPerTick) {
		setTime(day, hour, minute, second);
		setSecondPerTick(secondPerTick);
	}
	
	public DayNightCycle setTime(long day, float hour, float minute, float second) {
		this.day = day;
		this.time += hour * ONE_HOUR;
		this.time += minute * ONE_MINUTE;
		this.time += second * ONE_SECOND;
		return this;
		
	}
	public DayNightCycle setSecondPerTick(float secondPerTick) {
		this.secondPerTick = secondPerTick;
		return this;
	}
	
	public void tick() {
		this.time += secondPerTick;
		
		if(this.time >= MAX_TIME) {
			this.time = MathUtil.wrap(MIN_TIME, this.time, MAX_TIME);
			this.day++;
		}
	}
	public BufferedImage getImage() {
		BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setColor(new Color(0, 0, 0, getAlphaForTimeOfDay()));
		g2d.fillRect(0, 0, 10, 10);
		g2d.dispose();
		return bi;
	}
	
	public long getDay() {
		return this.day;
	}
	public float getHour() {
		return (this.time / ONE_HOUR) % (float) HOURS_PER_DAY;
	}
	public float getMinute() {
		return (this.time / ONE_MINUTE) % (float) MINUTES_PER_HOUR;
	}
	public float getSecond() {
		return (this.time / ONE_SECOND) % (float) SECONDS_PER_MINUTE;
	}
	
	public float getSecondPerTick() {
		return this.secondPerTick;
	}
	
	protected float getAlphaForTimeOfDay() {
		// >6 = full, 6-9 = fade, 9-16 = none, 16-19 = fade, 19< = full
		int tod = getTimeOfDay();
		
		if(tod == PRE_DAWN) return MAX_ALPHA;
		else if(tod == SUNRISE) return MathUtil.clamp(MIN_ALPHA, MathUtil.map(this.time, sunriseStart, sunriseEnd, MAX_ALPHA, MIN_ALPHA), MAX_ALPHA);
		else if(tod == DAYTIME) return MIN_ALPHA;
		else if(tod == SUNSET) return MathUtil.clamp(MIN_ALPHA, MathUtil.map(this.time, sunsetStart, sunsetEnd, MIN_ALPHA, MAX_ALPHA), MAX_ALPHA);
		else if(tod == POST_DUSK) return MAX_ALPHA;
		
		return MAX_ALPHA;
	}
	protected int getTimeOfDay() {
		return this.time <= sunriseStart ? PRE_DAWN : this.time <= sunriseEnd ? SUNRISE : this.time <= sunsetStart ? DAYTIME : this.time <= sunsetEnd ? SUNSET : POST_DUSK;
	}
	
	public String getTimeString(boolean withSeconds) {
		String hour = StringUtil.padTo((int) getHour() + "", 2, "0", false);
		String minute = StringUtil.padTo((int) getMinute() + "", 2, "0", false);
		String second = StringUtil.padTo((int) getSecond() + "", 2, "0", false);
		return StringUtil.insert("{0}:{1}{2}", hour, minute, withSeconds ? ":" + second : "");
	}
}