package com.thebrenny.jumg.util;

public class StringUtil {
	public static final String ASCII = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-=[]\\;',./`~!@#$%^&*()_+{}|:\"<>? ";
	public static long NEXT_UID = 0;
	
	public static String capitaliseFirstLetter(String str) {
		String first = str.substring(0, 1);
		String last = str.substring(1);
		return first.toUpperCase() + last;
	}
	
	public static String insert(String main, Object ... values) {
		String target = "";
		int valIndex = 0;
		
		// This allows us to do things like insert("{}{}{}", 1, 2, 3) => "123".
		while(main.contains("{}")) {
			main = main.replaceFirst("\\{\\}", values[Math.min(valIndex++, values.length - 1)].toString());
		}
		
		for(int arrIndex = 0; arrIndex < values.length; arrIndex++) {
			target = "{" + arrIndex + "}";
			if(main.contains(target)) main = main.replace(target, values[arrIndex].toString());
		}
		
		return main;
	}
	
	public static String multiply(String main, int length) {
		String ret = "";
		for(int i = 0; i < length; i++) ret += main;
		return ret;
	}
	
	public static String getStringList(Object[] o, String delimiter) {
		String ret = "";
		for(Object oo : o) ret += oo.toString() + delimiter;
		return ret.substring(0, Math.max(0, ret.length() - delimiter.length()));
	}
	
	public static String padTo(String word, int amount, String padding, boolean append) {
		String newPad = multiply(padding, amount);
		if(append) word = word + newPad;
		else word = newPad + word;
		if(append) return word.substring(0, amount);
		else return word.substring(word.length() - amount);
	}
	
	public static String normalizeCase(String word, boolean eachWord) {
		if(eachWord) {
			String ret = "";
			String[] words = word.split(" ");
			for(String a : words) ret += capitaliseFirstLetter(a) + " ";
			return ret.trim();
		} else {
			return capitaliseFirstLetter(word);
		}
	}
	
	public static String formatFloat(float num, int decimalPlaces) { // Super rough, but the best I could do with no internet! :') #SeaLyfe18
		return String.format("%." + decimalPlaces + "f", num);
		/*
		 * String sNum = num + "";
		 * String[] n = sNum.split("\\.");
		 * sNum = padTo(n[1], decimalPlaces + 1, "0", true);
		 * sNum = sNum.substring(0, decimalPlaces) + "." +
		 * sNum.substring(decimalPlaces);
		 * n[1] = "" + Math.round(Float.parseFloat(sNum));
		 * return n[0] + "." + padTo(n[1], decimalPlaces, "0", true);
		 */
	}
	
	public static String getNextUID(String prefix) {
		if(prefix == null) {
			StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
			prefix = ste.getClassName().substring(ste.getClassName().lastIndexOf(".") + 1);
		}
		return prefix + "_" + (NEXT_UID++);
	}
	
	/**
	 * Removes all instances of {@code trim} from the start of {@code main} in
	 * an iterative fashion. Eg:
	 * 
	 * <pre>
	 * String main = "hellohello";
	 * main = ltrim(main, "hel");
	 * assert main == "lohello";
	 * 
	 * main = "hihihoho";
	 * main = ltrim(main, "hi");
	 * assert main == "hoho"; // because both "hi"s were removed.
	 * </pre>
	 * 
	 * @param main
	 *        The String to alter.
	 * @param trim
	 *        The String to use as the trimming piece.
	 * @return A new, trimmed String.
	 * @see #rtrim(String, String)
	 */
	public static String ltrim(String main, String trim) {
		boolean didRemove = true;
		while(didRemove) {
			didRemove = main.substring(0, trim.length()).equals(trim);
			if(didRemove) main = main.substring(trim.length());
		}
		return main;
	}
	
	/**
	 * Removes all instances of {@code trim} from the end of {@code main} in an
	 * iterative fashion. Eg:
	 * 
	 * <pre>
	 * String main = "hellohello";
	 * main = rtrim(main, "llo");
	 * assert main == "hellohe";
	 * 
	 * main = "hihihoho";
	 * main = rtrim(main, "ho");
	 * assert main == "hihi"; // because both "ho"s were removed.
	 * </pre>
	 * 
	 * @param main
	 *        The String to alter.
	 * @param trim
	 *        The String to use as the trimming piece.
	 * @return A new, trimmed String.
	 * @see #ltrim(String, String)
	 */
	public static String rtrim(String main, String trim) {
		boolean didRemove = true;
		while(didRemove) {
			didRemove = main.substring(main.length() - trim.length()).equals(trim);
			if(didRemove) main = main.substring(0, main.length() - trim.length());
		}
		return main;
	}
	
	/**
	 * Removes all instances of {@code trim} from both sides of {@code main} in
	 * an iterative fashion. Eg:
	 * 
	 * <pre>
	 * String main = "hello, oh hello";
	 * main = trim(main, "hello");
	 * assert main == ", oh ";
	 * 
	 * main = "hi hi ho ho hi hi";
	 * main = rtrim(main, "hi hi");
	 * assert main == " ho ho "; // because both "hi hi"s were removed.
	 * </pre>
	 * 
	 * @param main
	 *        The String to alter.
	 * @param trim
	 *        The String to use as the trimming piece.
	 * @return A new, trimmed String.
	 * @see #ltrim(String, String)
	 * @see #rtrim(String, String)
	 */
	public static String trim(String main, String trim) {
		return ltrim(rtrim(main, trim), trim);
	}
}
