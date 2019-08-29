package com.thebrenny.jumg.items;

public interface IHasInventory {
	public Inventory getInventory();
	public int pickupItem(ItemStack itemStack);
}
