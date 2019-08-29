package com.thebrenny.jumg.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import com.thebrenny.jumg.MainGame;
import com.thebrenny.jumg.util.Logger;

public class Handler {
	public KeyListener keyH;
	public MouseListener mouseH;
	public WindowListener winH;
	public boolean inputStolen = false;
	public InputStealer inputStealer = null;
	
	public Handler() {
		Logger.log("Initialising handlers.");
		keyH = new KeyListener();
		mouseH = new MouseListener();
		winH = new WindowListener();
		Logger.log("Handler initialised.");
	}
	
	public void stealInput(InputStealer stealer) {
		inputStolen = true;
		this.inputStealer = stealer;
		if(stealer == null) giveBackInput();
	}
	public void giveBackInput() {
		inputStolen = false;
	}
	
	public class KeyListener implements java.awt.event.KeyListener {
		public void keyTyped(KeyEvent e) {}
		public void keyPressed(KeyEvent e) {
			if(inputStolen) {
				inputStealer.stealInput(e);
			} else {
				KeyBindings.pressKey(e.getKeyCode(), true, true);
			}
		}
		public void keyReleased(KeyEvent e) {
			KeyBindings.pressKey(e.getKeyCode(), false, true);
		}
	}
	
	public class MouseListener implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
		public void mouseClicked(MouseEvent e) {
			KeyBindings.setMousePos(e.getPoint());
		}
		public void mousePressed(MouseEvent e) {
			KeyBindings.pressKey(e.getButton(), true, false);
			MainGame.getMainGame().getScreenManager().currentScreen().mouseEvent(KeyBindings.MOUSE_POINT, true);
		}
		public void mouseReleased(MouseEvent e) {
			KeyBindings.pressKey(e.getButton(), false, false);
			MainGame.getMainGame().getScreenManager().currentScreen().mouseEvent(KeyBindings.MOUSE_POINT, false);
		}
		public void mouseEntered(MouseEvent e) {
			KeyBindings.setMousePos(e.getPoint());
		}
		public void mouseExited(MouseEvent e) {
			KeyBindings.setMousePos(e.getPoint());
		}
		public void mouseDragged(MouseEvent e) {
			KeyBindings.setMousePos(e.getPoint());
			KeyBindings.pressKey(e.getButton(), true, false);
			MainGame.getMainGame().getScreenManager().currentScreen().mouseEvent(KeyBindings.MOUSE_POINT, true);
		}
		public void mouseMoved(MouseEvent e) {
			KeyBindings.setMousePos(e.getPoint());
			MainGame.getMainGame().getScreenManager().currentScreen().mouseEvent(KeyBindings.MOUSE_POINT, false);
		}
	}
	
	public class WindowListener implements java.awt.event.WindowListener {
		public void windowOpened(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {
			MainGame.getMainGame().stop();
		}
		public void windowClosed(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
	}
	
	public interface InputStealer {
		public abstract void stealInput(KeyEvent keyStolen);
	}
}