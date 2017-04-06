package ovh.corail.tombstone.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.block.BlockFacing;
import ovh.corail.tombstone.core.Main;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

@SideOnly(Side.CLIENT)
public class RenderTombstone extends TileEntitySpecialRenderer<TileEntityTombstone> {
	public static final ModelTombstone model = new ModelTombstone();
	public static final ResourceLocation textures = (new ResourceLocation("minecraft:textures/blocks/stone.png"));

	@Override
	public void renderTileEntityAt(TileEntityTombstone te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.getWorld().getBlockState(te.getPos()).getBlock() != Main.tombstone) { return; }
		EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockFacing.FACING);
		int rotationIndex = 0;
		float modX = 0.5f, modZ = 0.5f;
		switch (facing) {
		case SOUTH:
			rotationIndex = 0;
			modZ = 0.68f;
			break;
		case WEST:
			rotationIndex = -1;
			modX = 0.32f;
			break;
		case EAST:
			rotationIndex = 1;
			modX = 0.68f;
			break;
		case NORTH:
		default:
			rotationIndex = 2;
			modZ = 0.32f;
			break;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5f, (float) y + 1.5f, (float) z + 0.5f);
		GlStateManager.rotate(90f * rotationIndex, 0f, 1f, 0f);
		GlStateManager.rotate(180f, 1.0f, 0.0F, 1.0f);
		this.bindTexture(textures);
		RenderTombstone.model.render((Entity) null, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + modX, (float) y + 0.15f, (float) z + modZ);
		GlStateManager.rotate(90f * rotationIndex, 0f, 1f, 0f);
		/** string size */
		float scale = 0.007F;
		GlStateManager.scale(scale, -scale, scale);
		/** draw string */
		showString("R.I.P.", getFontRenderer(), 0, 0x000000);
		if (te.getOwnerName() != null) {
			showString(te.getOwnerName(), getFontRenderer(), 10, 0xffffff);
		}
		GlStateManager.popMatrix();

	}

	private void showString(String content, FontRenderer fontRenderer, int posY, int color) {
		String deathText = content;
		String[] splitString = new String[1];
		splitString[0] = deathText;
		for (int i = 0; i < splitString.length; i++) {
			fontRenderer.drawString(splitString[i], -fontRenderer.getStringWidth(splitString[i]) / 2, (i * 10) - 30 + posY, color, false);
		}
	}
}
