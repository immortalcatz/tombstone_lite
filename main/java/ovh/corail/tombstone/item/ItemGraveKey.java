package ovh.corail.tombstone.item;

import java.awt.Color;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.handler.AchievementHandler;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.SoundHandler;

public class ItemGraveKey extends Item {

	private static final String name = "grave_key";

	/** add a compound if needed */
	public static boolean checkCompound(ItemStack stack) {
		if (stack.getItem() == Main.grave_key) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			return true;
		}
		return false;
	}

	/** write the compound of the itemStack */
	public static boolean setOwner(ItemStack stack, UUID tombId, String ownerName, BlockPos tombPos, int tombDim) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			compound.setString("tombId", tombId.toString());
			compound.setString("ownerName", ownerName);
			compound.setLong("tombPos", tombPos.toLong());
			compound.setInteger("tombDim", tombDim);
			return true;
		} else {
			return false;
		}
	}

	public static BlockPos getTombPos(ItemStack stack) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			return BlockPos.fromLong(compound.getLong("tombPos"));
		}
		return null;
	}

	public static UUID getTombId(ItemStack stack) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			return UUID.fromString(compound.getString("tombId"));
		}
		return null;
	}

	public static String getOwnerName(ItemStack stack) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			return compound.getString("ownerName");
		}
		return null;
	}

	public static int getTombDim(ItemStack stack) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			return compound.getInteger("tombDim");
		}
		return Integer.MAX_VALUE;
	}

	/** constructor */
	public ItemGraveKey() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(null);
		setMaxStackSize(1);
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		if (stack.hasEffect()) {
			playerIn.addStat(AchievementHandler.getAchievement("upgradedKey"), 1);
		}
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
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
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		if (itemStack.getTagCompound() != null) {
			String ownerName = itemStack.getTagCompound().getString("ownerName");
			BlockPos tombPos = BlockPos.fromLong(itemStack.getTagCompound().getLong("tombPos"));
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.grave_key.info.ownerTitle") + " : " + ownerName);
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.grave_key.info.posTitle") + " :  X=" + tombPos.getX() + "  Y=" + tombPos.getY());
			if (hasEffect(itemStack) && player.world.provider.getDimension() == getTombDim(itemStack))
				list.add(TextFormatting.WHITE + Helper.getTranslation("item.grave_key.info.tele"));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		if (upgraded(itemStackIn)) {
			if (worldIn.provider.getDimension() == getTombDim(itemStackIn)) {
				if (ConfigurationHandler.uniqueTeleport) {
					itemStackIn.getTagCompound().setBoolean("enchant", false);
				}
				BlockPos p = getTombPos(itemStackIn);
				playerIn.setPositionAndUpdate(p.getX() + .5, p.getY() + 1.05, p.getZ() + .5);
				playerIn.playSound(SoundHandler.magic_use01, 1.0F, 1.0F);
			} else {
				if (!worldIn.isRemote) {
					Helper.sendMessage("item.message.sameDimension", playerIn, true);
				}
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
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
		// Color color = Color.RED;

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
