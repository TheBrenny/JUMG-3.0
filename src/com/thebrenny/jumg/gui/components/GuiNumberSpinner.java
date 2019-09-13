package com.thebrenny.jumg.gui.components;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.thebrenny.jumg.util.MathUtil;

/**
 * Creates a number spinner built from two buttons and a text box, which is limited to number input only.
 * 
 * @author TheBrenny
 */
public class GuiNumberSpinner extends Component {
	private static final long serialVersionUID = 1L;
	protected GuiButton upButton;
	protected GuiTextBox spinnerText;
	protected GuiButton downButton;
	protected int number = 0;
	public int min = Integer.MIN_VALUE;
	public int max = Integer.MAX_VALUE;
	
	public GuiNumberSpinner(float x, float y, float width, float height, ArrayList<Component> components) {
		super(x, y, width, height);
		
		downButton = new GuiButton(x, y, height, height, "↓", new Runnable() {
			public void run() {
				GuiNumberSpinner.this.changeNumber(-1);
			}
		});
		
		spinnerText = new GuiTextBox(x + height, y, width - 2 * height, height, "0");
		spinnerText.ASCII = "1234567890";
		
		upButton = new GuiButton(x + width - height, y, height, height, "↑", new Runnable() {
			public void run() {
				GuiNumberSpinner.this.changeNumber(1);
			}
		});
		
		if(height > width) {
			upButton.setRect(x, y, width, width);
			spinnerText.setRect(x, y + width,width,height - 2 * width);
			downButton.setRect(x, y + height - width, width, width);
		}
		components.add(upButton);
		components.add(downButton);
		components.add(spinnerText);
	}
	
	public GuiNumberSpinner setMinMax(Integer min, Integer max) {
		if(min != null) this.min = min;
		if(max != null) this.max = max;
		return this;
	}
	
	public GuiNumberSpinner changeNumber(int amount) {
		return setNumber(this.number + amount);
	}
	
	public GuiNumberSpinner setNumber(int number) {
		this.number = MathUtil.clamp(this.min, number, this.max);
		spinnerText.setString("" + number);
		return this;
	}
	public int getNumber() {
		return this.number;
	}
	
	public BufferedImage getImage() {return null;}
	public void render(Graphics2D g2d, long xOffset, long yOffset) {}
}
