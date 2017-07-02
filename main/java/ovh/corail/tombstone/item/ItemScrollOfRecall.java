package ovh.corail.tombstone.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.core.NBTStackHelper;
import ovh.corail.tombstone.core.TeleportUtils;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.SoundHandler;

public class ItemScrollOfRecall extends Item implements ISoulConsumption {
	
	private static final String name = "scroll_of_recall";
	
	public ItemScrollOfRecall() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(Main.tabTombstone);
		setMaxStackSize(1);
	}
	
	protected static boolean isStackValid(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemScrollOfRecall;
	}
	
	/** write the compound of the itemStack */
	public static boolean setTombPos(ItemStack stack, BlockPos tombPos, int tombDim) {
		if (!isStackValid(stack)) { return false; }
		NBTStackHelper.setBlockPos(stack, "tombPos", tombPos);
		NBTStackHelper.setInteger(stack, "tombDim", tombDim);
		setUseCount(stack, ConfigurationHandler.scrollOfRecallUseCount);
		return true;
	}

	public static BlockPos getTombPos(ItemStack stack) {
		if (!isStackValid(stack)) { return BlockPos.ORIGIN; }
		return NBTStackHelper.getBlockPos(stack, "tombPos");
	}
	
	public static int getTombDim(ItemStack stack) {
		if (!isStackValid(stack)) { return Integer.MAX_VALUE; }
		return NBTStackHelper.getInteger(stack, "tombDim");
	}
	
	private static ItemStack setUseCount(ItemStack stack, int useCount) {
		NBTStackHelper.setInteger(stack, "useCount", useCount);
		return stack;
	}
	
	public static int getUseCount(ItemStack stack) {
		if (!isStackValid(stack)) { return 0; }
		return NBTStackHelper.getInteger(stack, "useCount");
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer player) {
		/** advancement create_magic_scroll */
		Helper.grantAdvancement(player, "tutorial/create_magic_scroll");
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return isEnchanted(stack);
	}
	
	public boolean isEnchanted(ItemStack stack) {
		return getTombPos(stack) != BlockPos.ORIGIN;
	}
	
	public boolean setEnchant(World world, BlockPos gravePos, EntityPlayer player, ItemStack stack) {
		boolean valid = setTombPos(stack, gravePos, world.provider.getDimension());
		if (valid) {
			/** advancement activate_magic_scroll */
			Helper.grantAdvancement(player, "tutorial/activate_magic_scroll");
			return true;
		}
		return false;
	}

	@Override 
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4) {
		if (!stack.hasTagCompound()) {
			list.add(TextFormatting.AQUA + Helper.getTranslation("item.scroll_of_recall.desc1"));
		} else {
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.scroll_of_recall.desc2"));
			int tombDimId = getTombDim(stack);
			BlockPos tombPos = getTombPos(stack);
			String tombDim = (tombDimId==0 ? "The Overworld" : (tombDimId == -1 ? "The Nether" : (tombDimId==-1? "The End": "Unknown Land " + tombDimId)));
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.info.dimTitle") + " : " + tombDim);
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.info.posTitle") + " :  " + tombPos.getX() + ", " + tombPos.getY() + ", " + tombPos.getZ());
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.info.useCount") + " : " + getUseCount(stack));
			list.add(TextFormatting.AQUA + Helper.getTranslation("item.info.tele"));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote && !player.getHeldItemMainhand().isEmpty()) {//&& player.isSneaking()
			ItemStack stack = player.getHeldItemMainhand();
			if (((ItemScrollOfRecall) stack.getItem()).isEnchanted(stack)) {
				BlockPos tombPos = ItemScrollOfRecall.getTombPos(stack);
				int tombDim = ItemScrollOfRecall.getTombDim(stack);
				if (world.provider.getDimension() == tombDim && tombPos.distanceSq(player.posX, player.posY, player.posZ) < 10) {
					Helper.sendMessage("item.scroll_of_recall.message.tooClose", player, true);
					return super.onItemRightClick(world, player, hand);
				}
				int useCount = getUseCount(stack)-1;
				if (useCount <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
				} else {
					setUseCount(stack, useCount);
					player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
				}
				if (useCount >= 0) {
					TeleportUtils.teleportEntity(player, tombDim, (double)tombPos.getX()+0.5d, (double)tombPos.getY()+1.05d, (double)tombPos.getZ()+0.5d);
					player.playSound(SoundHandler.magic_use01, 1.0F, 1.0F);
					Helper.sendMessage("item.scroll_of_recall.message.success", player, true);
				}
				/** advancement recall */
				Helper.grantAdvancement(player, "tutorial/recall");
			} else {
				Helper.sendMessage("item.scroll_of_recall.message.needSoul", player, true);
			}
		}
		return super.onItemRightClick(world, player, hand);
	}
}
