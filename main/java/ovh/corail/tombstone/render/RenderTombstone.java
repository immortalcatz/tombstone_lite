package ovh.corail.tombstone.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.block.BlockTombstone;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

@SideOnly(Side.CLIENT)
public class RenderTombstone extends TileEntitySpecialRenderer<TileEntity> {

	@Override
	public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if  (te == null) { return; }
		if (!(te instanceof TileEntityTombstone)) { return; }
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if (state.getBlock() == null || !(state.getBlock() instanceof BlockTombstone)) { return; }
		BlockTombstone block = (BlockTombstone)state.getBlock();
		EnumFacing facing = state.getValue(block.FACING);
		int rotationIndex = 0;
		float modX = 0.5f, modZ = 0.5f;
		switch (facing) {
		case SOUTH:
			rotationIndex = 0;
			modZ = 0.73f;
			break;
		case WEST:
			rotationIndex = -1;
			modX = 0.27f;
			break;
		case EAST:
			rotationIndex = 1;
			modX = 0.73f;
			break;
		case NORTH:
		default:
			rotationIndex = 2;
			modZ = 0.27f;
			break;
		}
		if (te instanceof TileEntityTombstone) {
			TileEntityTombstone tile = (TileEntityTombstone) te;
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x + modX, (float) y + 0.15f, (float) z + modZ);
			GlStateManager.rotate(90f * rotationIndex, 0f, 1f, 0f);
			/** string size */
			float scale = 0.005f;
			GlStateManager.scale(scale, -scale, scale);
			GlStateManager.depthMask(false);
			/** draw string */
			showString(TextFormatting.BOLD + "R.I.P.", getFontRenderer(), 0, 14339760);
			showString(TextFormatting.ITALIC + tile.getOwnerName(), getFontRenderer(), 10, 2966992);
			showString(tile.getOwnerDeathDate(1), getFontRenderer(), 20, 14339760);
			showString(tile.getOwnerDeathDate(2), getFontRenderer(), 30, 14339760);
			GlStateManager.depthMask(true);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	private void showString(String content, FontRenderer fontRenderer, int posY, int color) {
		String deathText = content;
		String[] splitString = new String[1];
		splitString[0] = deathText;
		for (int i = 0; i < splitString.length; i++) {
			fontRenderer.drawString(splitString[i], -fontRenderer.getStringWidth(splitString[i]) / 2, (i * 10) - 30 + posY, color, true);
		}
	}
}
