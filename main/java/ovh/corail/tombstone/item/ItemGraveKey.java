package ovh.corail.tombstone.item;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.NBTStackHelper;
import ovh.corail.tombstone.core.TeleportUtils;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.handler.SoundHandler;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class ItemGraveKey extends Item implements ISoulConsumption {

	private static final String name = "grave_key";

	public ItemGraveKey() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(null);
		setMaxStackSize(1);
	}
	
	/** read/write the compound of the itemStack */
	public static boolean setTombPos(ItemStack stack, BlockPos tombPos, int tombDim) {
		if (!isStackValid(stack)) { return false; }
		NBTStackHelper.setBlockPos(stack, "tombPos", tombPos);
		NBTStackHelper.setInteger(stack, "tombDim", tombDim);
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
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotId, boolean isSelected) {
		if (!isStackValid(stack)) { return; }
		if (world.isRemote) { return; }
		if (entity == null || !(entity instanceof EntityPlayer)) { return; }
		EntityPlayer player = (EntityPlayer) entity;
		int tombDimId = getTombDim(stack);
		if (tombDimId != world.provider.getDimension()) { return; }
		BlockPos tombPos = getTombPos(stack);
		if (!world.isBlockLoaded(tombPos)) { return; } 
		TileEntity tile = world.getTileEntity(tombPos);
		if (tile == null || !(tile instanceof TileEntityTombstone)) {
			player.replaceItemInInventory(slotId, ItemStack.EMPTY);
			player.inventoryContainer.detectAndSendChanges();
		}
    }
	
	protected static boolean isStackValid(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemGraveKey;
	}
	
	@Override
    public String getItemStackDisplayName(ItemStack stack) {
        return hasEffect(stack) ? Helper.getTranslation("item.grave_key.nameUpgraded") : I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    }

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer player) {
		if (isEnchanted(stack)) {
			/** advancement upgrade_grave_key */
			Helper.grantAdvancement(player, "tutorial/upgrade_grave_key");
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return isEnchanted(stack);
	}

	public boolean isEnchanted(ItemStack stack) {
		return NBTStackHelper.getBoolean(stack, "enchant");
	}
	
	public boolean setEnchant(World world, BlockPos gravePos, EntityPlayer player, ItemStack stack) {
		if (!isStackValid(stack) || !ConfigurationHandler.upgradeTombKey) { return false; }
		NBTStackHelper.setBoolean(stack, "enchant", true);
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4) {
		if (stack.hasTagCompound()) {
			int tombDimId = getTombDim(stack);
			BlockPos tombPos = getTombPos(stack);
			String tombDim = (tombDimId==0 ? "The Overworld" : (tombDimId == -1 ? "The Nether" : (tombDimId==-1? "The End": "Unknown Land " + tombDimId)));
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.info.dimTitle") + " : " + tombDim);
			list.add(TextFormatting.WHITE + Helper.getTranslation("item.info.posTitle") + " :  " + tombPos.getX() + ", " + tombPos.getY() + ", " + tombPos.getZ());			if (hasEffect(stack)) {
				list.add(TextFormatting.AQUA + Helper.getTranslation("item.info.tele"));
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItemMainhand();
		if (isEnchanted(stack)) {
			BlockPos pos = getTombPos(stack);
			int dimId = getTombDim(stack);
			if (player.dimension != dimId && !ConfigurationHandler.teleportDim) {
				Helper.sendMessage("config.teleportDim", player, true);
			} else {
				stack.getTagCompound().setBoolean("enchant", false);
				TeleportUtils.teleportEntity(player, dimId, (double)pos.getX()+0.5d, (double)pos.getY()+1.05d, (double)pos.getZ()+0.5d);
				player.playSound(SoundHandler.magic_use01, 1.0F, 1.0F);
			}	
		}
		return super.onItemRightClick(world, player, hand);
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (!ConfigurationHandler.highlight) { return; }
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
		BufferBuilder renderer = tessellator.getBuffer();
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
