package ovh.corail.tombstone.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.block.BlockGrave;
import ovh.corail.tombstone.block.BlockGrave.GraveType;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.handler.ConfigurationHandler;
import ovh.corail.tombstone.tileentity.TileEntityWritableGrave;

@SideOnly(Side.CLIENT)
public class RenderWritableGrave extends TileEntitySpecialRenderer<TileEntityWritableGrave> {

	@Override
	public void render(TileEntityWritableGrave te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if  (te == null || te.getOwnerName().isEmpty()) { return; }
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if (state.getBlock() == null || !(state.getBlock() instanceof BlockGrave)) { return; }
		BlockGrave block = (BlockGrave)state.getBlock();
		EnumFacing facing = state.getValue(block.FACING);
		int rotationIndex = 0;
		float modX = 0.5f, modY = 0.5f, modZ = 0.5f;
		
		float value;
		switch (block.getGraveType()) {
		case GRAVE_CROSS:
			value = 0.25f;
			modY = 0.06375f;
			break;
		case GRAVE_NORMAL:
			value = 0.12625f;
			modY = 0.5f;
			break;
		case GRAVE_SIMPLE:
			value = 0.18875f;
			modY = 0.4f;
			break;
		case TOMBSTONE:default:
			value = 0.56375f;
			modY = 0.25f;
			break;
		}
		boolean is_cross = block.getGraveType() == GraveType.GRAVE_CROSS;
		switch (facing) {
		case SOUTH:
			rotationIndex = 0;
			if (is_cross) {
				modZ = 1f - value;
			} else {
				modZ = value;
			}
			break;
		case WEST:
			rotationIndex = -1;
			if (is_cross) {
				modX = value;
			} else {
				modX = 1f - value;
			}
			break;
		case EAST:
			rotationIndex = 1;
			if (is_cross) {
				modX = 1f - value;
			} else {
				modX= value;				
			}
			break;
		case NORTH:default:
			rotationIndex = 2;
			if (is_cross) {
				modZ = value;
			} else {
				modZ= 1f - value;
			}
		}
		
		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.translate((float) x + modX, (float) y + modY, (float) z + modZ);
		GlStateManager.rotate(90f * rotationIndex, 0f, 1f, 0f);
		
		if (is_cross) {
			GlStateManager.rotate(90f, -1f, 0f, 0f);
		}
		
		/** string size */
		float scale = 0.007f;
		GlStateManager.scale(scale, -scale, scale);
		GlStateManager.depthMask(false);
		/** draw string */
		showString(TextFormatting.BOLD + "R.I.P.", getFontRenderer(), 0, ConfigurationHandler.textColorRIP+0xff000000);
		GlStateManager.scale(1/scale, 1/scale, 1/scale);
		
		scale = 0.005f;
		GlStateManager.scale(scale, scale, scale);
		int textPos=0;
		showString(TextFormatting.BOLD + te.getOwnerName(), getFontRenderer(), 11, ConfigurationHandler.textColorOwner+0xff000000);		
		GlStateManager.scale(1/scale, 1/scale, 1/scale);
		
		scale = 0.004f;
		GlStateManager.scale(scale, scale, scale);
		int textColorDeathDate = ConfigurationHandler.textColorDeathDate+0xff000000;
		showString(TextFormatting.BOLD + Helper.getTranslation("message.death_date.died_on"), getFontRenderer(), 26, textColorDeathDate);
		showString(TextFormatting.BOLD + te.getOwnerDeathDate(1, false), getFontRenderer(), 36, textColorDeathDate);
		showString(TextFormatting.BOLD + te.getOwnerDeathDate(2, true), getFontRenderer(), 46, textColorDeathDate);
		GlStateManager.scale(1/scale, 1/scale, 1/scale);
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		
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
