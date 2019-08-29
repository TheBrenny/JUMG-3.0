package com.thebrenny.jumg.items;

import com.thebrenny.jumg.entities.Entity;

public interface IItemStackUsable {
	public boolean canUse(Entity owner);
	public boolean startUsing(Entity owner);
	public float use(Entity owner); // return the reload timer
	public boolean isUsing(Entity owner);
}
