package com.thebrenny.jumg.gui.components;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.thebrenny.jumg.util.MathUtil;

/**
 * Creates a number spinner built from two buttons and a text box, which is
 * limited to number input only. It doesn't actually render anything, instead it
 * adds those three components to the components ArrayList of the screen (which
 * is passed).
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
				GuiNumberSpinner.this.readCurrentNumber();
				GuiNumberSpinner.this.changeNumber(-1);
			}
		});
		
		spinnerText = new GuiTextBox(x + height, y, width - 2 * height, height, "0");
		spinnerText.setASCII("-1234567890");
		spinnerText.getGuiLabel().align(GuiLabel.ALIGN_CENTRE).move((float) spinnerText.getWidth() / 2, (float) spinnerText.getHeight() / 2);
		
		upButton = new GuiButton(x + width - height, y, height, height, "↑", new Runnable() {
			public void run() {
				GuiNumberSpinner.this.readCurrentNumber();
				GuiNumberSpinner.this.changeNumber(1);
			}
		});
		
		if(height > width) {
			upButton.move(x, y).resize(width, width);
			spinnerText.move(x, y + width).resize(width, height - 2 * width);
			downButton.move(x, y + height - width).resize(width, width);
		}
		components.add(spinnerText);
		components.add(upButton);
		components.add(downButton);
	}
	
	public Component move(float x, float y) {
		if(height > width) {
			upButton.move(x, y);
			spinnerText.move(x, y + width);
			downButton.move(x, y + height - width);
		} else {
			downButton.move(x, y);
			spinnerText.move(x + height, y);
			upButton.move(x + width - height, y);
		}
		return super.move(x, y);
	}
	
	public GuiNumberSpinner setMinMax(Integer min, Integer max) {
		if(min != null) this.min = min;
		if(max != null) this.max = max;
		return this;
	}
	
	public void tick() {
		if(this.spinnerText.isSelected()) readCurrentNumber();
	}

	public GuiNumberSpinner readCurrentNumber() {
		try {
			return setNumber(Integer.parseInt(this.spinnerText.getString()));
		} catch (Exception e) {
			return this.setNumber(0);
		}
	}
	public GuiNumberSpinner changeNumber(int amount) {
		return setNumber(this.number + amount);
	}
	public GuiNumberSpinner setNumber(int number) {
		this.number = MathUtil.clamp(this.min, number, this.max);
		spinnerText.setString("" + this.number);
		return this;
	}
	public int getNumber() {
		return this.number;
	}
	
	public BufferedImage getNewImage() {
		return null;
	}
}
