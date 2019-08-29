package com.thebrenny.jumg.items;

import com.thebrenny.jumg.util.MathUtil;

public class ItemStack {
	protected Item item;
	protected int maxCount;
	protected int count;
	
	public ItemStack(Item item) {
		this(item, 1);
	}
	
	public ItemStack(Item item, int count) {
		this.item = item;
		this.count = count;
		this.maxCount = (item instanceof IItemStackable) ? ((IItemStackable) item).getMaxStack() : 1;
	}
	
	private ItemStack(ItemStack stack) {
		this(stack.item, stack.count);
		this.maxCount = stack.maxCount;
		// add other data about itemstack here.
	}
	
	public Item getItem() {
		return this.item;
	}
	public int getMaxCount() {
		return maxCount;
	}
	public void setCount(int count) {
		this.count = MathUtil.clamp(0, count, this.maxCount);
	}
	public int getCount() {
		return this.count;
	}
	
	/**
	 * Merges the adding ItemStack to this ItemStack and modifies both items.
	 * This means that if {@code add} completely fills this ItemStack, its
	 * count will be equal to zero.
	 * 
	 * @param add
	 * @return The amount of items that can't be used in the merge.
	 */
	public int merge(ItemStack add) {
		if(add.getItem() == getItem() && this.getCount() < this.getMaxCount()) {
			add.setCount(this.replenish(add.getCount()));
		}
		return add.getCount();
	}
	
	// returns the counts that couldn't be used
	/**
	 * Adds the {@code count} to this stack, and returns the amount that
	 * exceeded the maximum amount in the stack.
	 * 
	 * @param count
	 * @return The amount of items that can't be used in the replenish.
	 */
	public int replenish(int count) {
		int ret = (this.count + count) % this.maxCount;
		this.count = MathUtil.clamp(0, this.count + count, this.maxCount);
		return ret;
	}
	/**
	 * Consumes the {@code count} from this stack, and returns the amount that
	 * is yet to be consumed by other potential stacks.
	 * 
	 * @param count
	 * @return
	 */
	public int consume(int count) {
		int ret = (count - this.count);
		this.count = MathUtil.clamp(0, this.count - count, this.maxCount);
		return ret < 0 ? 0 : ret;
		/*
		 * Test cases:
		 * 
		 * stack of 10, consume 3:
		 *   ret = (3 - 10)              = -7
		 *   count = clamp(0, 7, 10)     =  7
		 *   return ret < 0 ? 0 : ret    =  0
		 *   
		 * stack of 3, consume 5:
		 *   ret = (5 - 3)               =  2
		 *   count = clamp(0, -2, 10)    =  0
		 *   return ret < 0 ? 0 : ret    =  2
		 */
	}

	//@MustOverride // - we want subordinate classes to HAVE TO override this method.
	public ItemStack cloneStack() {
		return new ItemStack(this);
	}
}
