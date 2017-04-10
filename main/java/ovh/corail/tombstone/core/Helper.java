package ovh.corail.tombstone.core;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Helper {
	
	public static void sendMessage(String message, EntityPlayer currentPlayer, boolean translate) {
		if (currentPlayer != null) {
			if (translate) {
				message = getTranslation(message);
			}
			currentPlayer.sendMessage(new TextComponentString(message));
		}
	}
	
	public static String getTranslation(String message) {
		return I18n.translateToLocal(message);
	}
	
	public static boolean areItemEqual(ItemStack s1, ItemStack s2) {
		return s1.isItemEqual(s2) && s1.getMetadata() == s2.getMetadata() && ItemStack.areItemStackTagsEqual(s1, s2);
	}

	public static void render() {
		/** render blocks */
		render(Main.tombstone);
		render(Main.decorative_grave_simple);
		render(Main.decorative_grave_normal);
		render(Main.decorative_grave_cross);
		render(Main.decorative_tombstone);
		/** render achievement icon items */
		render(Main.itemAchievement001);
		render(Main.itemAchievement002);
		/** render items */
		render(Main.grave_key);
	}
	
	private static void render(Block block) {
		render(Item.getItemFromBlock(block), 0);
	}
	
	private static void render(Item item) {
		render(item, 0);
	}

	private static void render(Block block, int meta) {
		render(Item.getItemFromBlock(block), meta);
	}

	private static void render(Item item, int meta) {
		/** only for meta 0 for now */
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,	new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
	public static void register() {
		/** register blocks */
		register(Main.tombstone);
		register(Main.decorative_grave_simple);
		register(Main.decorative_grave_normal);
		register(Main.decorative_grave_cross);
		register(Main.decorative_tombstone);
		/** register achievement icon items */
		register(Main.itemAchievement001);
		register(Main.itemAchievement002);
		/** register items */
		register(Main.grave_key);
	}
	
	private static void register(Block block) {
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	private static void register(Item item) {
		GameRegistry.register(item);
	}
	
	public static boolean isSafeBlock(World worldIn, BlockPos currentPos) {
		IBlockState state = worldIn.getBlockState(currentPos);
		Block block = state.getBlock();
		if (currentPos.getY() < 0) { return false; }
		if (block.isAir(state, worldIn, currentPos)) {
			return true;
		}
		return false;
	}

	public static int[] calculXp(double totalXp) {
		return calculXp((int) Math.floor(totalXp));
	}

	public static int[] calculXp(int totalXp) {
		int level, nextLevel;
		/** level between 0 and 16 */
		if (totalXp <= 352) {
			level = 0;
			while (totalXp >= 2 * level + 7) {
				totalXp -= 2 * level + 7;
				level++;
			}
			nextLevel = 2 * level + 7;
			/** level between 16 and 31 */
		} else if (totalXp <= 1507) {
			totalXp -= 352;
			level = 16;
			while (totalXp >= 5 * level - 38) {
				totalXp -= 5 * level - 38;
				level++;
			}
			nextLevel = 5 * level - 38;
			/** level 31 and above */
		} else {
			totalXp -= 1507;
			level = 31;
			while (totalXp >= 9 * level - 158) {
				totalXp -= 9 * level - 158;
				level++;
			}
			nextLevel = 9 * level - 158;
		}
		int[] result = { level, totalXp, nextLevel };
		return result;
	}
	
	public static ItemStack addToInventoryWithLeftover(ItemStack stack, IInventory inventory, boolean simulate) {
		int left = stack.getCount();
		int minus = inventory instanceof InventoryPlayer ? 4 : 0;
		int max = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		for (int i = 0; i < inventory.getSizeInventory() - minus; i++) {
			ItemStack in = inventory.getStackInSlot(i);
			// if (!inventory.isItemValidForSlot(i, stack))
			// continue;
			if (!in.isEmpty() && stack.isItemEqual(in) && ItemStack.areItemStackTagsEqual(stack, in)) {
				int space = max - in.getCount();
				int add = Math.min(space, stack.getCount());
				if (add > 0) {
					if (!simulate)
						in.grow(add);
					left -= add;
					if (left <= 0)
						return ItemStack.EMPTY;
				}
			}
		}
		for (int i = 0; i < inventory.getSizeInventory() - minus; i++) {
			ItemStack in = inventory.getStackInSlot(i);
			// if (!inventory.isItemValidForSlot(i, stack))
			// continue;
			if (in.isEmpty()) {
				int add = Math.min(max, left);
				if (!simulate)
					inventory.setInventorySlotContents(i, copyStack(stack, add));
				left -= add;
				if (left <= 0)
					return ItemStack.EMPTY;
			}
		}
		return copyStack(stack, left);
	}

	private static ItemStack copyStack(ItemStack stack, int size) {
		if (stack.isEmpty() || size == 0)
			return ItemStack.EMPTY;
		ItemStack tmp = stack.copy();
		tmp.setCount(Math.min(size, stack.getMaxStackSize()));
		return tmp;
	}
	// TODO proxy
	public static void produceTombstoneParticles(BlockPos pos) {
		BlockPos currentPos = pos;
		double motionX = 0.0D;
		double motionY = 0.01D;
		double motionZ = 0.0D;
		int[] params = new int[4];
		params[0] = 0;
		params[1] = 0;
		params[2] = 66;
		params[3] = 66;
		for (int i = -1; i < 1; i++) {
			for (int j = -1; j < 1; j++) {
				currentPos = pos.south(j + 1).east(i + 1);
				Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, currentPos.getX(), currentPos.getY(), currentPos.getZ(), motionX, motionY, motionZ, params);
			}
		}
	}

}