package com.thebrenny.jumg.entities;

import com.thebrenny.jumg.items.IItemStackUsable;

public interface ICanEquipItems {
	public int getHandX();
	public int getHandY();
	public IItemStackUsable getInHand();
	public IItemStackUsable setInHand(IItemStackUsable usable);
}
