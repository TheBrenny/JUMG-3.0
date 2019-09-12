package com.thebrenny.jumg.input;

import java.awt.Point;
import java.util.ArrayList;

import com.thebrenny.jumg.util.Logger;
import com.thebrenny.jumg.util.StringUtil;

public class KeyBindings {
	public static ArrayList<Thing> KEY_REGISTRY = new ArrayList<Thing>();
	public static ArrayList<Thing> KEY_UNREGISTERED = new ArrayList<Thing>();
	public static Point MOUSE_POINT = new Point(0, 0);
	
	public static ArrayList<Thing> PRESSED_KEYS = new ArrayList<Thing>();
	
	public static Thing addKey(String name, int code, boolean toggles, boolean isKey) {
		name = name.toLowerCase();
		String thingName = isKey ? Key.class.getSimpleName() : Mouse.class.getSimpleName();
		Logger.log("Binding action [" + name + "] to code [" + code + "] with toggling [" + toggles + "] and is a " + thingName + ".");
		for(Thing k : KEY_REGISTRY) {
			String kC = k.getClass().getSimpleName();
			if(k.name.equals(name) || (k.code == code && kC.equals(thingName))) {
				Logger.log("Could not bind {0} because it conflicts with the name or code.", (Object) thingName);
				Logger.log("  {0} to being made [{1}, {2}].", isKey ? "Key" : "Mouse", name, code);
				Logger.log("  {0} stopping this [{1}, {2}].", kC, k.name, k.code);
				return null;
			}
		}
		for(Thing k : KEY_UNREGISTERED) {
			String kC = k.getClass().getSimpleName();
			if(k.code == code && kC.equals(thingName)) {
				Logger.log("FYI: {0} was previously unregistered!", (Object) kC);
				KEY_UNREGISTERED.remove(k);
			}
		}
		
		Thing k = isKey ? new Key(name, code, toggles) : new Mouse(name, code);
		if(isKey) KEY_REGISTRY.add(k);
		else KEY_REGISTRY.add(k);
		
		return k;
	}
	private static Thing addUnregisteredKey(int code, boolean isKey) {
		String n = "uk_" + code;
		Thing k = isKey ? new Key(n, code, false) : new Mouse(n, code);
		KEY_UNREGISTERED.add(k);
		return k;
	}
	
	public static void pressKey(String name, boolean flag) {
		Thing k = getThingByName(name);
		if(k == null) return;
		k.press(flag);
	}
	public static void pressKey(int code, boolean flag, boolean isKey) {
		Thing k = getThingByCode(code);
		if(k == null) k = addUnregisteredKey(code, isKey);
		k.press(flag);
	}
	
	public static boolean isPressed(String name) {
		Thing k = getThingByName(name);
		if(k != null && PRESSED_KEYS.contains(k)) PRESSED_KEYS.remove(k);
		return k != null ? k.isPressed() : false;
	}
	public static boolean isPressed(int code) {
		Thing k = getThingByCode(code);
		if(k != null && PRESSED_KEYS.contains(k)) PRESSED_KEYS.remove(k);
		return k != null ? k.isPressed() : false;
	}
	
	public static boolean wasPressed(String name) {
		Thing k = getThingByName(name);
		if(k != null && PRESSED_KEYS.contains(k) && !k.isPressed()) {
			PRESSED_KEYS.remove(k);
			return true;
		}
		return false;
	}
	public static boolean wasPressed(int code) {
		Thing k = getThingByCode(code);
		if(k != null && PRESSED_KEYS.contains(k) && !k.isPressed()) {
			PRESSED_KEYS.remove(k);
			return true;
		}
		return false;
	}
	
	public static boolean doesToggle(String name) {
		Thing t = getThingByName(name);
		if(!(t instanceof Key)) return false;
		Key k = (Key) t;
		return k != null ? k.toggles : false;
	}
	public static boolean doesToggle(int keyCode) {
		Thing t = getThingByCode(keyCode);
		if(!(t instanceof Key)) return false;
		Key k = (Key) t;
		return k != null ? k.toggles : false;
	}
	
	public static Thing getThingByName(String name) {
		for(Thing k : KEY_REGISTRY) {
			if(k.name.equals(name)) return k;
		}
		for(Thing k : KEY_UNREGISTERED) {
			if(k.name.equals(name)) return k;
		}
		return null;
	}
	public static Thing getThingByCode(int code) {
		for(Thing k : KEY_REGISTRY) {
			if(k.code == code) return k;
		}
		for(Thing k : KEY_UNREGISTERED) {
			if(k.code == code) return k;
		}
		return null;
	}
	
	public abstract static class Thing {
		private final String name;
		private final int code;
		protected boolean isPressed;
		
		public Thing(String name, int code) {
			this.name = name;
			this.code = code;
			this.isPressed = false;
		}
		
		public String getName() {
			return name;
		}
		public int getCode() {
			return code;
		}
		public boolean isPressed() {
			return isPressed;
		}
		public void press(boolean flag) {
			this.isPressed = flag;
			if(flag && !PRESSED_KEYS.contains(this)) PRESSED_KEYS.add(this);
		}
		public String toString() {
			return getClass().getName() + "[Name{" + name + "},Code{" + code + "},IsPressed{" + isPressed + "}]";
		}
	}
	public static class Key extends Thing {
		private final boolean toggles;
		private boolean beingPressed = false;
		
		public Key(String name, int keyCode, boolean toggles) {
			super(name, keyCode);
			this.toggles = toggles;
		}
		public boolean isToggleable() {
			return toggles;
		}
		public void press(boolean flag) {
			if(toggles) {
				if(flag) {
					if(!beingPressed) {
						isPressed = !isPressed;
						beingPressed = true;
					}
				} else {
					beingPressed = false;
				}
			} else {
				super.press(flag);
			}
		}
		public String toString() {
			return StringUtil.insert(super.toString(), ",Toggles{" + toggles + "}", -2);
		}
	}
	public static class Mouse extends Thing {
		public Mouse(String name, int mouseCode) {
			super(name, mouseCode);
		}
	}
	
	public static void setMousePos(Point mouse) {
		KeyBindings.MOUSE_POINT = mouse;
	}
}
