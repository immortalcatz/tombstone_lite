package ovh.corail.tombstone.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.tombstone.core.Helper;

public class ItemBlockGrave extends ItemBlock {

	public ItemBlockGrave(Block block) {
		super(block);
		this.setMaxStackSize(1);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if (!isEngraved(stack)) {
			/** advancement create_decorative_grave */
			Helper.grantAdvancement(player, "tutorial/create_decorative_grave");
		}
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
    	String customName = getEngravedName(stack);
    	if (customName.isEmpty()) {
    		super.addInformation(stack, world, tooltip, flag);
    	} else {
    		tooltip.clear();
	    	tooltip.add(0, I18n.translateToLocal(stack.getUnlocalizedName()+".name"));
	    	// TODO translate
	    	tooltip.add(TextFormatting.GRAY + "The grave seems to be engraved, a name can be read : \"" + customName + "\"");
    	}
    }
    
    public static boolean setEngravedName(ItemStack stack, String engraved_name) {
    	if (stack.isEmpty() || engraved_name.isEmpty()) { return false; }
    	NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
    	nbt.setString("engraved_name", engraved_name);
		nbt.removeTag("display");
		stack.setTagCompound(nbt);
    	return true;
    }
    
    public static boolean isEngraved(ItemStack stack) {
		return !getEngravedName(stack).isEmpty();
    }
    
    public static String getEngravedName(ItemStack stack) {
    	return stack.isEmpty() || !stack.hasTagCompound() ? "" : stack.getTagCompound().getString("engraved_name");
    }
}
