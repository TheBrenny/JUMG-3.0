package com.thebrenny.jumg.util;

import java.util.Calendar;

public class TimeUtil {
	public static final Calendar CALENDAR = Calendar.getInstance();
	
	public static long timeToMS(long hours, long minutes, long seconds, long millis) {
		return ((((hours * 60) + minutes) * 60 + seconds) * 1000 + millis);
	}
	public static long getTime(TimeType tt) {
		switch(tt) {
		case NANO:
			Logger.log("Cannot get current nano time, so I'm returning millis.");
		default:
			return CALENDAR.get(tt.getCalendarType());
		}
	}
	
	public static long getElapsed(long time) {
		return getElapsed(time, TimeType.MILLIS);
	}
	public static long getElapsed(long time, TimeType tt) {
		return getEpoch(tt) - time;
	}
	
	public static long getEpoch() {
		return getEpoch(TimeType.MILLIS);
	}
	public static long getEpoch(TimeType tt) {
		switch(tt) {
		case NANO:
			return System.nanoTime();
		default:
			Logger.log("[" + tt.name() + "] does not convert into an epoch time.");
		case MILLIS:
			return System.currentTimeMillis();
		case SECOND:
			return System.currentTimeMillis() / 1000;
		case MINUTE:
			return System.currentTimeMillis() / (1000 * 60);
		case HOUR:
			return System.currentTimeMillis() / (1000 * 60 * 60);
		}
	}
	
	public static enum TimeType {
		NANO(Calendar.MILLISECOND),
		MILLIS(Calendar.MILLISECOND),
		SECOND(Calendar.SECOND),
		MINUTE(Calendar.MINUTE),
		HOUR(Calendar.HOUR_OF_DAY),
		DAY_MONTH(Calendar.DAY_OF_MONTH),
		DAY_YEAR(Calendar.DAY_OF_YEAR),
		MONTH(Calendar.MONTH),
		YEAR(Calendar.YEAR);
		
		private int calendarType;
		
		TimeType(int calendarType) {
			this.calendarType = calendarType;
		}
		
		public int getCalendarType() {
			return calendarType;
		}
	}
}