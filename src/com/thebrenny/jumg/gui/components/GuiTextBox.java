package com.thebrenny.jumg.gui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.MainGame;
import com.thebrenny.jumg.input.Handler.InputStealer;
import com.thebrenny.jumg.util.MathUtil;
import com.thebrenny.jumg.util.StringUtil;

public class GuiTextBox extends Component implements InputStealer {
	public String ASCII = StringUtil.ASCII;
	public boolean selected = false;
	public boolean loseInput = false;
	public GuiLabel text;
	public int blinker = 0;
	public int cursor;
	public BufferedImage biCache;
	
	public GuiTextBox(float x, float y, float width, float height, String defText) {
		super(x, y, width, height);
		super.run = new Runnable() {
			public void run() {
				selected = !selected;
				if(loseInput) selected = false;
				if(selected) {
					MainGame.getMainGame().getHandler().stealInput(GuiTextBox.this);
				} else {
					MainGame.getMainGame().getHandler().giveBackInput();
				}
			}
		};
		text = new GuiLabel(3, height / 2, defText, GuiLabel.BUTTON_FONT).allign(GuiLabel.ALLIGN_HORIZONTAL_LEFT, GuiLabel.ALLIGN_VERTICAL_CENTRE).setColor(Color.WHITE);
		cursor = defText.length();
	}
	
	public void changeMe(boolean hover, boolean clicked, Point mousePoint) {
		if(enabled) {
			if(hover) {
				if(clicked) {
					hovering = true;
					clicked = true;
					run.run();
				} else {
					hovering = false;
					clicked = false;
				}
			} else {
				if(clicked) {
					hovering = true;
					clicked = true;
					loseInput = true;
					run.run();
					loseInput = false;
				} else {
					hovering = false;
					clicked = false;
				}
			}
		}
	}
	public void doClick() {
		if(this.clicked) pressed = true;
		
		if(pressed && hovering && !clicked) {
			run.run();
			pressed = false;
		} else if(pressed && !hovering) {
			pressed = false;
		}
	}
	
	public void stealInput(KeyEvent keyStolen) {
		if(ASCII.contains(keyStolen.getKeyChar() + "")) {
			addString(keyStolen.getKeyChar() + "");
		}
		if(keyStolen.getKeyChar() == '\b') {
			removePrevious();
		}
		switch(keyStolen.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			shiftCursor(-1);
			break;
		case KeyEvent.VK_RIGHT:
			shiftCursor(1);
			break;
		case KeyEvent.VK_HOME:
			placeCursor(0);
			break;
		case KeyEvent.VK_END:
			placeCursor(text.getString(0).length());
			break;
		case KeyEvent.VK_DELETE:
			removeNext();
			break;
		}
	}
	
	public void shiftCursor(int amount) {
		placeCursor(cursor + amount);
	}
	public void placeCursor(int index) {
		index = MathUtil.clamp(0, index, text.getString(0).length());
		cursor = index;
	}
	public void setString(String s) {
		removeAll();
		addString(s);
	}
	public void addString(String s) {
		text.insertString(0, cursor, s);
		cursor += s.length();
	}
	public void removePrevious() {
		if(cursor > 0) {
			text.setString(0, text.getString(0).substring(0, cursor - 1) + text.getString(0).substring(cursor));
			cursor--;
		}
	}
	public void removeNext() {
		if(cursor < text.getString(0).length()) {
			text.setString(0, text.getString(0).substring(0, cursor) + text.getString(0).substring(cursor + 1));
		}
	}
	public void removeAll() {
		text.setString(0, "");
	}
	
	public String getString() {
		return this.text.getString(0);
	}
	
	public Component resize(float width, float height) {
		requestNewImage();
		return super.resize(width, height);
	}
	
	public void tick() {
		if(this.selected && blinker > 0) {
			blinker--;
		} else {
			blinker = 40;
		}
		
		if(!this.selected) blinker = 0;
	}
	public BufferedImage getImage() {
		if(biCache == null) {
			BufferedImage bi = new BufferedImage((int) getWidth(), (int) getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();
			
			g2d.setColor(new Color(70, 70, 70));
			g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			g2d.setColor(Color.BLACK);
			g2d.fillRect(2, 2, bi.getWidth() - 5, bi.getHeight() - 5);
			
			g2d.dispose();
			biCache = bi;
		}
		return biCache;
	}
	public void requestNewImage() {
		this.biCache = null;
	}
	
	public void render(Graphics2D g2d, long xOffset, long yOffset) {
		if(blinker > 20) {
			g2d.setColor(Color.ORANGE);
			g2d.fillRect((int) (getX() + xOffset + text.getSubstringWidth(0, 0, cursor)) + 3, (int) (getY() + yOffset + text.getAllignedY(0)) + 7, 1, (int) text.getSingleHeight());
		}
		
		g2d.drawImage(getImage(), (int) (getX() + xOffset), (int) (getY() + yOffset), null);
		text.render(g2d, (int) getX() + xOffset, (int) getY() + yOffset);
	}
}