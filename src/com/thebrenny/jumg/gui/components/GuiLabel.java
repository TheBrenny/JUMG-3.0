package com.thebrenny.jumg.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.thebrenny.jumg.util.MathUtil;

public class GuiLabel extends Component {
	private static final long serialVersionUID = 1L;
	public static final String ALIGN_HORIZONTAL_LEFT = "left";
	public static final String ALIGN_HORIZONTAL_CENTRE = "centre";
	public static final String ALIGN_HORIZONTAL_RIGHT = "right";
	public static final String ALIGN_VERTICAL_TOP = "top";
	public static final String ALIGN_VERTICAL_CENTRE = "centre";
	public static final String ALIGN_VERTICAL_BOTTOM = "bottom";
	public static final String ALIGN_TOP_LEFT = ALIGN_HORIZONTAL_LEFT + ":" + ALIGN_VERTICAL_TOP;
	public static final String ALIGN_CENTRE = ALIGN_HORIZONTAL_CENTRE + ":" + ALIGN_VERTICAL_CENTRE;
	
	public static Font BUTTON_FONT = new Font("Terminal", Font.BOLD, 16);
	public static Font TITLE_FONT = new Font("Terminal", Font.BOLD, 40);
	public static Font BODY_FONT = new Font("Terminal", Font.PLAIN, 12);
	
	protected float realX;
	protected float realY;
	
	protected String[] string;
	protected String[] allignment;
	protected Font font;
	protected Color color;
	protected FontMetrics fontMets;
	
	public GuiLabel(float x, float y, String string) {
		this(x, y, string, GuiLabel.BODY_FONT);
	}
	public GuiLabel(float x, float y, String string, Font font) {
		super(x, y);
		this.realX = x;
		this.realY = y;
		if(string == null) string = "";
		this.string = string.split("\n");
		if(font == null) font = BODY_FONT;
		this.font = font;
		this.color = Color.BLACK;
		this.allignment = ALIGN_TOP_LEFT.split(":");
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		this.fontMets = g2d.getFontMetrics(font);
		g2d.dispose();
		this.fixBounds();
	}
	
	public GuiLabel setFont(Font font) {
		this.font = font;
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		this.fontMets = g2d.getFontMetrics(font);
		g2d.dispose();
		this.fixBounds();
		return this;
	}
	public GuiLabel adjustFont(int style) {
		this.setFont(this.font.deriveFont(style));
		return this;
	}
	public GuiLabel adjustFont(float size) {
		this.setFont(this.font.deriveFont(size));
		return this;
	}
	public GuiLabel setColor(Color color) {
		this.color = color;
		this.requestNewImage();
		return this;
	}
	public GuiLabel align(String allign) {
		if(allign.contains(":")) this.allignment = allign.split(":");
		this.fixBounds();
		return this;
	}
	public GuiLabel align(String alignHorizontal, String alignVertical) {
		return this.align(alignHorizontal + ":" + alignVertical);
	}
	
	public void setString(int line, String s) {
		line = MathUtil.clamp(0, line, string.length);
		if(line == string.length) {
			String[] newStrings = new String[string.length + 1];
			int i = 0;
			for(i = 0; i < string.length; i++) newStrings[i] = string[i];
			this.string = newStrings;
		}
		this.string[line] = s;
		this.fixBounds();
	}
	public void insertString(int line, int pos, String s) {
		line = MathUtil.clamp(0, line, string.length);
		if(line == string.length) setString(line, s);
		else string[line] = string[line].substring(0, pos) + s + string[line].substring(pos);
		this.fixBounds();
	}
	
	public int getLines() {
		return string.length;
	}
	public String getString(int line) {
		line = MathUtil.clamp(0, line, string.length - 1);
		return string[line];
	}
	public String[] getAllignment() {
		return allignment;
	}
	
	public float getAllignedX(int line) {
		float xOff = allignment[0].equalsIgnoreCase(ALIGN_HORIZONTAL_RIGHT) ? 1 : allignment[0].equalsIgnoreCase(ALIGN_HORIZONTAL_CENTRE) ? 0.5F : 0;
		return ((float) getWidth() - getWidth(line)) * xOff;
	}
	public float getAllignedY(int line) {
		float yOff = allignment[1].equalsIgnoreCase(ALIGN_VERTICAL_BOTTOM) ? 1 : allignment[1].equalsIgnoreCase(ALIGN_VERTICAL_CENTRE) ? 0.5F : 0;
		return ((float) getHeight() - getSingleHeight()) * yOff;
	}
	public float getMaxAllignedX() {
		float xOff = allignment[0].equalsIgnoreCase(ALIGN_HORIZONTAL_RIGHT) ? getMaxWidth() : allignment[0].equalsIgnoreCase(ALIGN_HORIZONTAL_CENTRE) ? getMaxWidth() / 2 : 0;
		return xOff;//(float) getX() - xOff;
	}
	public float getMaxAllignedY() {
		float yOff = allignment[1].equalsIgnoreCase(ALIGN_VERTICAL_BOTTOM) ? getMaxHeight() : allignment[1].equalsIgnoreCase(ALIGN_VERTICAL_CENTRE) ? getMaxHeight() / 2 : 0;
		return yOff;//(float) getY() - yOff;
	}
	public float getWidth(int line) {
		return Math.max(1, fontMets.stringWidth(string[line]));
	}
	public float getSingleHeight() {
		return Math.max(1, fontMets.getHeight() + fontMets.getDescent());
	}
	public float getMaxWidth() {
		float biggestString = 1;
		for(String s : string)
			biggestString = Math.max(fontMets.stringWidth(s), biggestString);
		return biggestString;
	}
	public float getMaxHeight() {
		return Math.max(1, (fontMets.getHeight() + fontMets.getDescent()) * string.length);
	}
	public float getSubstringWidth(int line, int startPos, int endPos) {
		return fontMets.stringWidth(string[line].substring(startPos, endPos));
	}
	
	public BufferedImage getNewImage() {
		BufferedImage bi = new BufferedImage((int) getMaxWidth() + 3, (int) getMaxHeight() + 3, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(this.font);
		g2d.setColor(color);
		for(int i = 0; i < string.length; i++) {
			g2d.drawString(string[i], getAllignedX(i), fontMets.getHeight() * (i + 1));
		}
		return bi;
	}

	public Component move(float x, float y) {
		this.realX = x;
		this.realY = y;
		this.fixBounds();
		return this;
	}
	public Component translate(float x, float y) {
		return this.move(this.realX + x, this.realY + y);
	}

	public void fixBounds() {
		this.resize(getMaxWidth(), getMaxHeight());
		super.move(realX - getMaxAllignedX(), realY - getMaxAllignedY());
		this.requestNewImage();
	}
}
