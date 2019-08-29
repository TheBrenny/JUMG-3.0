package com.thebrenny.jumg.hud;

import java.awt.Point;

public interface IHudButton {
	public void mouseEvent(Point mousePoint, boolean clicked);
	public void onClick(Point mousePoint);
}
