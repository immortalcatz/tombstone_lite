package ovh.corail.tombstone.gui;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import ovh.corail.tombstone.core.Helper;
import ovh.corail.tombstone.core.ModProps;
import ovh.corail.tombstone.handler.PacketHandler;
import ovh.corail.tombstone.packet.TakeAllMessage;
import ovh.corail.tombstone.tileentity.TileEntityTombstone;

public class GuiTombstone extends GuiContainer {
	private TileEntityTombstone inventory;
	private BlockPos currentPos;
	private EntityPlayer playerIn;
	private ResourceLocation textureBg = new ResourceLocation("minecraft:textures/blocks/stone.png");
	private ResourceLocation textureSlot = new ResourceLocation(ModProps.MOD_ID + ":textures/gui/slot.png");

	public GuiTombstone(EntityPlayer playerIn, World world, BlockPos pos, TileEntityTombstone tileEntity) {
		super(new ContainerTombstone(playerIn, world, pos, tileEntity));
		this.xSize = 176;
		this.ySize = 190;
		this.inventory = tileEntity;
		this.currentPos = pos;
		this.playerIn = playerIn;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - xSize) / 2;
		this.guiTop = (this.height - ySize) / 2;
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, (this.width - 100) / 2, this.guiTop + 95, 100, 18, Helper.getTranslation("button.0.label")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		try {
			World world = playerIn.world;
			if (world.getTileEntity(currentPos) instanceof TileEntityTombstone) {
				TileEntityTombstone tile = (TileEntityTombstone) world.getTileEntity(currentPos);
				if (tile instanceof TileEntityTombstone && tile != null && !tile.getOwnerName().isEmpty()) {
					this.fontRenderer.drawString(Helper.getTranslation("gui.message.tombOf") + " " + tile.getOwnerName(), (20), (-20), 0xffffff);
				} else {
					this.fontRenderer.drawString(Helper.getTranslation("tile.tombstone.name"), (20), (-20), 0xffffff);
				}
				Date deathDate;
				deathDate = new SimpleDateFormat("ddMMyyyy_HHmmss").parse(tile.getOwnerDeathDate());
				String dateString = new SimpleDateFormat("dd/MM/yyyy").format(deathDate);
				String timeString = new SimpleDateFormat(Helper.getTranslation("HH:mm:ss")).format(deathDate);
				this.fontRenderer.drawString(TextFormatting.ITALIC + Helper.getTranslation("gui.message.diedOn") + " " + dateString + " " + Helper.getTranslation("gui.message.prefixAt") + " " + timeString, (10), (-10), 0x4876ff);
			}
		} catch (ParseException e) {
			//e.printStackTrace();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glScalef(1F, 1F, 1F);
		mc.renderEngine.bindTexture(textureBg);
		int posX = ((this.width - this.xSize) / 2);
		int posY = ((this.height - this.ySize) / 2);
		this.drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);
		zLevel = 100.0F;
		mc.renderEngine.bindTexture(textureSlot);
		int i, j;
		int dimCase = 16;
		List<Slot> slots = this.inventorySlots.inventorySlots;
		Slot slot;
		for (i = 0; i < slots.size(); i++) {
			slot = slots.get(i);
			this.drawTexturedModalRect(posX + slot.xPos, posY + slot.yPos, 0, 0, dimCase, dimCase);
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (par2 != 28 && par2 != 156) {
			if (par2 == 1) {
				this.mc.player.closeScreen();
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		switch (button.id) {
		case 0:
			/** take all */
			PacketHandler.INSTANCE.sendToServer(new TakeAllMessage(currentPos));
			break;
		}
	}
}
