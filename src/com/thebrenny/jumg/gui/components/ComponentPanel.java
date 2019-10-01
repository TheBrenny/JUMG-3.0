package com.thebrenny.jumg.gui.components;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ComponentPanel extends Component {
	private static final long serialVersionUID = 1L;
	protected ArrayList<Component> components;

	public ComponentPanel(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.components = new ArrayList<Component>();
	}
	
	public boolean addComponent(Component c) {
		return components.add(c);
	}

	public void appendToComponents(ArrayList<Component> components) {
		for(Component c : this.components) components.add(c.translate(this.x, this.y));
	}
	public ArrayList<Component> getComponents() {
		return this.components;
	}

	public BufferedImage getNewImage() {
		return null;
	}
}