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
	private static final long serialVersionUID = 1L;
	private String ASCII = StringUtil.ASCII;
	private boolean selected = false;
	private boolean loseInput = false;
	private GuiLabel text;
	private int blinker = 0;
	private int cursor;
	
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
		text = new GuiLabel(3, height / 2, defText, GuiLabel.BUTTON_FONT).align(GuiLabel.ALIGN_HORIZONTAL_LEFT, GuiLabel.ALIGN_VERTICAL_CENTRE).setColor(Color.WHITE);
		cursor = defText.length();
	}
	public GuiTextBox setASCII(String ascii) {
		ASCII = ascii;
		return this;
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
			this.requestNewImage();
		}
	}
	public void doClick() {
		if(!this.enabled) return;
		if(this.clicked) pressed = true;
		
		if(pressed && hovering && !clicked) {
			run.run();
			pressed = false;
		} else if(pressed && !hovering) {
			pressed = false;
		}
		this.requestNewImage();
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
	public boolean isSelected() {
		return this.selected;
	}
	
	public void shiftCursor(int amount) {
		placeCursor(cursor + amount);
		this.requestNewImage();
	}
	public void placeCursor(int index) {
		index = MathUtil.clamp(0, index, text.getString(0).length());
		cursor = index;
		this.requestNewImage();
	}
	public void setString(String s) {
		removeAll();
		addString(s);
		this.requestNewImage();
	}
	public void addString(String s) {
		text.insertString(0, cursor, s);
		cursor += s.length();
		this.requestNewImage();
	}
	public void removePrevious() {
		if(cursor > 0) {
			text.setString(0, text.getString(0).substring(0, cursor - 1) + text.getString(0).substring(cursor));
			cursor--;
			this.requestNewImage();
		}
	}
	public void removeNext() {
		if(cursor < text.getString(0).length()) {
			text.setString(0, text.getString(0).substring(0, cursor) + text.getString(0).substring(cursor + 1));
			this.requestNewImage();
		}
	}
	public void removeAll() {
		text.setString(0, "");
		cursor = 0;
		this.requestNewImage();
	}
	
	public GuiTextBox move(float x, float y) {
		super.move(x,y);
		this.text.move((float) getWidth() / 2, (float) getHeight() / 2);
		requestNewImage();
		return this;
	}
	public GuiTextBox resize(float width, float height) {
		super.resize(width, height);
		this.text.move((float) getWidth() / 2, (float) getHeight() / 2);
		requestNewImage();
		return this;
	}

	public GuiLabel getGuiLabel() {
		return text;
	}
	public String getString() {
		return this.text.getString(0);
	}
	
	public void tick() {
		if(this.selected && blinker > 0) blinker--;
		else blinker = 40;
		
		if(blinker == 40 || blinker == 20) this.requestNewImage();
		
		if(!this.selected) blinker = 0;
	}
	public BufferedImage getNewImage() {
		BufferedImage bi = new BufferedImage((int) getWidth(), (int) getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		
		g2d.setColor(new Color(70, 70, 70));
		g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g2d.setColor(Color.BLACK);
		g2d.fillRect(2, 2, bi.getWidth() - 5, bi.getHeight() - 5);
		
		if(blinker > 20) {
			g2d.setColor(Color.ORANGE);
			g2d.fillRect((int) (text.getSubstringWidth(0, 0, cursor) + text.getX()), 10, 1, bi.getHeight() - 20);
		}
		
		text.render(g2d, 0, 0);
		
		g2d.dispose();
		return bi;
	}
}
