package ovh.corail.tombstone.item;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.core.TeleportDim;
import ovh.corail.tombstone.handler.AchievementHandler;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.SoundHandler;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class ItemGraveKey extends Item {

	private static final String name = "grave_key";

	public ItemGraveKey() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(null);
		setMaxStackSize(1);
	}
	
	/** add a compound if needed */
	private static void checkCompound(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
	}
	
	private static boolean isGraveKey(ItemStack stack) {
		return stack.getItem() == Main.grave_key;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!(stack.getItem() instanceof ItemGraveKey)) { return; }
		int tombDimId = getTombDim(stack);
		if (tombDimId != world.provider.getDimension()) { return; }
		BlockPos tombPos = getTombPos(stack);
		TileEntity tile = world.getTileEntity(tombPos);
		if (tile == null || !(tile instanceof TileEntityTombstone)) {
			entity.replaceItemInInventory(itemSlot, ItemStack.EMPTY);
			if (world.isRemote) {
				Helper.sendMessage("item.grave_key.message.disappear", (EntityPlayer) entity, true);
			}
		}
    }

	/** write the compound of the itemStack */
	public static boolean setTombPos(ItemStack stack, BlockPos tombPos, int tombDim) {
		if (!isGraveKey(stack)) { return false; }
		checkCompound(stack);
		NBTTagCompound compound = stack.getTagCompound();
		compound.setLong("tombPos", tombPos.toLong());
		compound.setInteger("tombDim", tombDim);
		return true;
	}

	public static BlockPos getTombPos(ItemStack stack) {
		if (!isGraveKey(stack) || !stack.hasTagCompound()) { return null; }
		NBTTagCompound compound = stack.getTagCompound();
		return BlockPos.fromLong(compound.getLong("tombPos"));
	}

	public static int getTombDim(ItemStack stack) {
		if (!isGraveKey(stack) || !stack.hasTagCompound()) { return Integer.MAX_VALUE; }
		NBTTagCompound compound = stack.getTagCompound();
		return compound.getInteger("tombDim");
	}
	
	@Override
    public String getItemStackDisplayName(ItemStack stack) {
        return hasEffect(stack) ? Helper.getTranslation("item.grave_key.nameUpgraded") : I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    }

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		if (stack.hasEffect()) {
			playerIn.addStat(AchievementHandler.getAchievement("upgradedKey"), 1);
		}
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
		/**
		 * because it's possible to drop the key while leaving the gui of the
		 * empty tomb and to have a key without the related tomb
		 */
		return false;
	}

	private boolean upgraded(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("enchant") : false;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return upgraded(stack);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if (stack.getTagCompound() != null) {
			int tombDimId = getTombDim(stack);
			String worldType = DimensionManager.getWorld(tombDimId).provider.getDimensionType().getName();
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.grave_key.info.dimTitle") + " : " + worldType + "(" + tombDimId + ")");
			BlockPos tombPos = getTombPos(stack);
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.grave_key.info.posTitle") + " :  X=" + tombPos.getX() + "  Y=" + tombPos.getY());
			if (hasEffect(stack)) {
				list.add(TextFormatting.AQUA + Helper.getTranslation("item.grave_key.info.tele"));
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItemMainhand();
		if (upgraded(stack)) {
			BlockPos pos = getTombPos(stack);
			int dimId = this.getTombDim(stack);
			if (world.provider.getDimension() == dimId) {
				if (ConfigurationHandler.uniqueTeleport) {
					stack.getTagCompound().setBoolean("enchant", false);
				}
				player.setPositionAndUpdate(pos.getX() + .5, pos.getY() + 1.05, pos.getZ() + .5);
				player.playSound(SoundHandler.magic_use01, 1.0F, 1.0F);
			} else {
				TeleportDim.getInstance().teleport(player, pos, dimId);
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		/** show the target tomb when holding the tomb's key */
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player.getHeldItemMainhand().isEmpty() || player.getHeldItemMainhand().getItem() != this) {
			return;
		}
		if (getTombDim(player.getHeldItemMainhand()) != player.dimension) {
			return;
		}
		double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

		GlStateManager.pushMatrix();

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
		// code
		long c = (System.currentTimeMillis() / 15l) % 360l;
		Color color = Color.getHSBColor(c / 360f, 1f, 1f);

		BlockPos p = getTombPos(player.getHeldItemMainhand());
		float x = p.getX(), y = p.getY(), z = p.getZ();
		// RenderHelper.enableStandardItemLighting();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
		GL11.glLineWidth(2.5f);
		GlStateManager.pushAttrib();
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		float offset = 1f;
		renderer.pos(x, y, z).endVertex();
		renderer.pos(x + offset, y, z).endVertex();

		renderer.pos(x, y, z).endVertex();
		renderer.pos(x, y + offset, z).endVertex();

		renderer.pos(x, y, z).endVertex();
		renderer.pos(x, y, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x, y + offset, z).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();

		renderer.pos(x, y + offset, z).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x + offset, y, z).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x + offset, y, z).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x, y, z + offset).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x, y, z + offset).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();
		tessellator.draw();
		// RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);

		GlStateManager.popMatrix();
	}

}
