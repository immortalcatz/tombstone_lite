package ovh.corail.tombstone.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import ovh.corail.tombstone.handler.ConfigurationHandler;

public class Helper {
	
	private static Random random = new Random();
	
	public static int getRandom(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}
	
	public static Set<String> loadWhitelist(File whitelistFile) {
		Set<String> whitelist = new HashSet<String>();
		if (!whitelistFile.exists()) {
			/** default list */			
			whitelist.add("minecraft:tallgrass:0");
			whitelist.add("minecraft:tallgrass:1");
			whitelist.add("minecraft:tallgrass:2");
			whitelist.add("minecraft:deadbush:0");
			whitelist.add("minecraft:double_plant:0");
			whitelist.add("minecraft:double_plant:1");
			whitelist.add("minecraft:double_plant:2");
			whitelist.add("minecraft:double_plant:3");
			whitelist.add("minecraft:double_plant:4");
			whitelist.add("minecraft:double_plant:5");
			Helper.saveAsJson(whitelistFile, whitelist);
		} else {
			Type token = new TypeToken<Set<String>>() {}.getType();
			whitelist = (Set<String>) Helper.loadAsJson(whitelistFile, token);
		}
		return whitelist;
	}
	
	public static boolean saveAsJson(File file, Set<?> list) {
		if (file.exists()) { file.delete(); }
		try {
			if (file.createNewFile()) {
				FileWriter fw = new FileWriter(file);
				fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(list));
				fw.close();
				return true;
			}
		} catch (IOException e) { e.printStackTrace(); }
		return false;
	}
	
	public static Set<?> loadAsJson(File file, Type token) {
		Set<?> list = null;
		try {
			list = new Gson().fromJson(new BufferedReader(new FileReader(file)), token);
		} catch (Exception e) { e.printStackTrace(); }
		return list;
	}
	
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
	
	public static void sendLog(String message) {
		boolean develop = false;
		if (develop) {
			System.out.println(message);
		}
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
		render(Main.itemAchievement003);
		/** render items */
		render(Main.grave_key);
		render(Main.fake_fog);
		render(Main.soul);
		render(Main.scroll_of_recall);
	}
	
	public static void render(Block block) {
		render(Item.getItemFromBlock(block), 0);
	}
	
	public static void render(Item item) {
		render(item, 0);
	}

	public static void render(Block block, int meta) {
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
		register(Main.itemAchievement003);
		/** register items */
		register(Main.grave_key);
		register(Main.fake_fog);
		register(Main.soul);
		register(Main.scroll_of_recall);	
	}
	
	public static void register(Block block) {
		ForgeRegistries.BLOCKS.register(block);
		ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	public static void register(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}
	
	public static boolean isSafeBlock(World world, BlockPos currentPos) {
		IBlockState state = world.getBlockState(currentPos);
		Block block = state.getBlock();
		if (currentPos.getY() < 0) { return false; }
		if (world.isAirBlock(currentPos)) { return true; }
		/** replaceable blocks */
		if (Main.whitelist.contains(block.getRegistryName().toString() + ":" + block.getMetaFromState(state))) { return true; }
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
	
	public static void registerEncodedRecipes() {
		/** recipe to upgrade the tomb's key */
		if (ConfigurationHandler.upgradeTombKey) {
			String[] parts = ConfigurationHandler.ingredientToUpgradeKey.split(":");
			ItemStack ingredient;
			if (parts.length == 4) {
				ingredient = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1])), Integer.valueOf(parts[2]), Integer.valueOf(parts[3]));
			} else if (parts.length == 3) {
				ingredient = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1])), Integer.valueOf(parts[2]));
			} else {
				ingredient = new ItemStack(Items.ENDER_PEARL, 1);
			}
			ItemStack res = new ItemStack(Main.grave_key);
			res.setTagCompound(new NBTTagCompound());
			res.getTagCompound().setBoolean("enchant", true);

			NonNullList<Ingredient> inputList = NonNullList.create();
			inputList.add(Ingredient.fromItem(Main.grave_key));
			inputList.add(Ingredient.fromStacks(ingredient));
			RecipeSorter.register(ModProps.MOD_ID + ":upgrade_key", UpgradeGraveKeyRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
			ResourceLocation group = new ResourceLocation("tombstone", "grave");
			UpgradeGraveKeyRecipe recipe_upgrade_key = new UpgradeGraveKeyRecipe(group, res, inputList);
			recipe_upgrade_key.setRegistryName(new ResourceLocation(ModProps.MOD_ID, "upgrade_key"));
			ForgeRegistries.RECIPES.register(recipe_upgrade_key);
		}
	}

}
