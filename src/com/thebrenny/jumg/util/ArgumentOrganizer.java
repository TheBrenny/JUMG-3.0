package com.thebrenny.jumg.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.thebrenny.jumg.MainGame;

public class ArgumentOrganizer {
	public static long MAXIMUM_MEMORY = 800 * 1024 * 1024;
	public static ArgumentOrganizer ORGANIZED_ARGUMENTS;
	public HashMap<String,String> fixedArgs;
	
	public ArgumentOrganizer(String[] args) {
		ORGANIZED_ARGUMENTS = this;
		fixedArgs = new HashMap<String,String>();
		
		for(String arg : args) fixedArgs.put(arg.split(":")[0], arg.split(":")[1]);
		
		if(fixedArgs.containsKey("maxmem")) MAXIMUM_MEMORY = Long.parseLong(fixedArgs.get("maxmem"));
		
		if(Runtime.getRuntime().maxMemory() > MAXIMUM_MEMORY) {
			fixedArgs.put("debug", "true");
			Logger.log("Maximum memory will change...\n\tPrevious:\t" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "mb\n\tNew:\t\t800mb");
			try {
				String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
				String jar = new File(MainGame.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
				String command = java + " -jar -Xmx800m \"" + jar + "\"";
				
				Logger.log("Executing the following command:\n\t" + command);
				
				Runtime.getRuntime().exec(command);
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
			System.exit(1);
		}
		
		if(!fixedArgs.containsKey("username")) fixedArgs.put("username", System.getProperty("user.name"));
		
		Logger.log("Raw Arguments:");
		for(String arg : args) {
			Logger.log("  " + arg);
		}
		Logger.log("Organized Arguments:");
		Iterator<Entry<String,String>> it = fixedArgs.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,String> entry = it.next();
			Logger.log("  {0}:{1}", (Object) entry.getKey(), entry.getValue());
		}
	}
	public boolean boolVal(String key) {
		String val = fixedArgs.get(key);
		return val == null ? false : val.equalsIgnoreCase("true");
	}
	public int intVal(String key) {
		return intVal(key, 10);
	}
	public int intVal(String key, int radix) {
		String val = fixedArgs.get(key);
		return val == null ? 0 : Integer.parseInt(val, radix);
	}
	public long longVal(String key) {
		return longVal(key, 10);
	}
	public long longVal(String key, int radix) {
		String val = fixedArgs.get(key);
		return val == null ? 0L : Long.parseLong(val, radix);
	}
	public float floatVal(String key) {
		String val = fixedArgs.get(key);
		return val == null ? 0.0F : Float.parseFloat(val);
	}
	public String stringVal(String key) {
		String val = fixedArgs.get(key);
		return val == null ? "" : val;
	}
	public boolean argumentExists(String key) {
		return fixedArgs.containsKey(key);
	}
	public static ArgumentOrganizer getOrganizedArguments() {
		return ArgumentOrganizer.ORGANIZED_ARGUMENTS;
	}
}
