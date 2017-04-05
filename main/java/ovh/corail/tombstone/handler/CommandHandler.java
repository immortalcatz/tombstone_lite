package ovh.corail.tombstone.handler;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ovh.corail.tombstone.core.TeleportDim;

public class CommandHandler implements ICommand {
	
	private final List<String> aliases = Lists.newArrayList();
	private final List<String> commands = Lists.newArrayList();
			
	public CommandHandler() {
		aliases.add("tombstone");
		aliases.add("corail");
		commands.add("teleport");
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return aliases.get(0);
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO texte
		return "/teleport <posX> <posY> <posZ> <dimension>";
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = sender.getEntityWorld();
		EntityPlayerMP player = (EntityPlayerMP) sender;
		/*if (world.isRemote) {
			System.out.println("message.command.denyClient");
			return;
		}*/
		/*if (args.length != 1) {
			System.out.println("message.command.invalidArgument");
			return;
		} else*/ 
		if (args[0].equals(commands.get(0))) {
			/** command 0 */
			if (args.length == 5) {
				int posX = Integer.parseInt(args[1]);
				int posY = Integer.parseInt(args[2]);
				int posZ = Integer.parseInt(args[3]);
				int dimension = Integer.parseInt(args[4]);
				TeleportDim.getInstance().teleport((Entity) sender, new BlockPos(posX, posY, posZ), dimension);		
			}
		} else {
			System.out.println("message.command.invalidArgument");
			return;
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// TODO config/right
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return commands;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}

}
