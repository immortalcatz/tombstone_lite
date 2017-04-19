package ovh.corail.tombstone.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.core.TeleportUtils;
import ovh.corail.tombstone.handler.AchievementHandler;
import ovh.corail.tombstone.handler.SoundHandler;

public class ItemScrollOfRecall extends Item {
	
	private static final String name = "scroll_of_recall";

	public ItemScrollOfRecall() {
		this(name);
	}
	
	public ItemScrollOfRecall(String name) {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(Main.tabTombstone);
		setMaxStackSize(1);
	}

	/** add a compound if needed */
	protected static void checkCompound(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
	}
	
	protected static boolean isStackValid(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemScrollOfRecall;
	}
	
	/** write the compound of the itemStack */
	public static boolean setTombPos(ItemStack stack, BlockPos tombPos, int tombDim) {
		if (!isStackValid(stack)) { return false; }
		checkCompound(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setLong("tombPos", tombPos.toLong());
		compound.setInteger("tombDim", tombDim);
		return true;
	}

	public static BlockPos getTombPos(ItemStack stack) {
		if (!isStackValid(stack) || !stack.hasTagCompound()) { return null; }
		NBTTagCompound compound = stack.getTagCompound();
		return BlockPos.fromLong(compound.getLong("tombPos"));
	}
	
	public static int getTombDim(ItemStack stack) {
		if (!isStackValid(stack) || !stack.hasTagCompound()) { return Integer.MAX_VALUE; }
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getInteger("tombDim");
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		playerIn.addStat(AchievementHandler.getAchievement("makeScroll"), 1);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return isEnchanted(stack);
	}
	
	public boolean isEnchanted(ItemStack stack) {
		return getTombPos(stack) != null;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if (!stack.hasTagCompound()) {
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.scroll_of_recall.desc1"));
		} else {
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.scroll_of_recall.desc2"));
			int tombDimId = getTombDim(stack);
			BlockPos tombPos = getTombPos(stack);
			String tombDim = (tombDimId==0 ? "The Overworld" : (tombDimId == -1 ? "The Nether" : (tombDimId==-1? "The End": "Unknown Land " + tombDimId)));
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.info.dimTitle") + " : " + tombDim);
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.info.posTitle") + " :  " + tombPos.getX() + ", " + tombPos.getY() + ", " + tombPos.getZ());
			list.add(TextFormatting.AQUA + Helper.getTranslation("item.info.tele"));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote && !player.getHeldItemMainhand().isEmpty()) {//&& player.isSneaking()
			ItemStack stack = player.getHeldItemMainhand();
			if (((ItemScrollOfRecall) stack.getItem()).hasEffect(stack)) {
				BlockPos tombPos = ItemScrollOfRecall.getTombPos(stack);
				int tombDim = ItemScrollOfRecall.getTombDim(stack);
				if (world.provider.getDimension() == tombDim && tombPos.distanceSq(player.posX, player.posY, player.posZ) < 10) {
					Helper.sendMessage("item.scroll_of_recall.message.tooClose", player, true);
					return super.onItemRightClick(world, player, hand);
				}
				player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
				TeleportUtils.teleportEntity(player, tombDim, (double)tombPos.getX()+0.5d, (double)tombPos.getY()+1.05d, (double)tombPos.getZ()+0.5d);
				player.playSound(SoundHandler.magic_use01, 1.0F, 1.0F);
				Helper.sendMessage("item.scroll_of_recall.message.success", player, true);
				player.addStat(AchievementHandler.getAchievement("recall"), 1);
			} else {
				Helper.sendMessage("item.scroll_of_recall.message.needSoul", player, true);
			}
		}
		return super.onItemRightClick(world, player, hand);
	}
}
