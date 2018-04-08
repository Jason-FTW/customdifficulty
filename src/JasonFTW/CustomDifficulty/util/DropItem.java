package jasonftw.CustomDifficulty.util;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DropItem {
	private static Random rand = new Random();
	private int amountMax;
	private int amountMin;
	private byte chance;
	private Material material;
	private short meta;

	public DropItem(Material material, short meta, int amountMin, int amountMax, int chance) {
		this.material = material;
		this.meta = meta;
		this.amountMin = Math.min(amountMin, amountMax);
		this.amountMax = Math.max(amountMin, amountMax);
		this.chance = (byte)chance;
	}

	public ItemStack getDrop() {
		ItemStack output = new ItemStack(this.material);
		output.setDurability(this.meta);
		int amount = rand.nextInt(this.amountMax - this.amountMin + 1) + this.amountMin;
		if (amount < 1 || rand.nextInt(100) >= this.chance) {
			return null;
		}
		output.setAmount(amount);
		return output;
	}
}

