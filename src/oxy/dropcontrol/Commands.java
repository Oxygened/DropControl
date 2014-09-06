package oxy.dropcontrol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor
{
	private Main plugin;
	public Commands(Main main)
	{
		this.plugin=main;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,String[] arg3) 
	{
		if(arg3.length>0)
		{
			if(arg3[0].equalsIgnoreCase("manager"))
			{
				if(sender instanceof Player)
				{
					final Player p = (Player)sender;
					if(p.isOp()||p.hasPermission("dropcontrol.admin"))
					{
						if(arg3.length<2)
							p.sendMessage(ChatColor.RED+"[DropControl] Veuillez spécifier un monde");
						for(World world:Bukkit.getWorlds())
						{
							if(arg3[1].equals(world.getName()))
							{
								p.openInventory(plugin.getEventClass().getGuiInv(world.getName()));
								return true;
							}
						}
						p.sendMessage(ChatColor.RED+"[DropControl] Ce monde n'existe pas");
						return true;
					}
				}
				//Si pas joueur
				else
				{
					sender.sendMessage("[DropControl] Vous devez etre en jeu pour utiliser cette commande");
				}
				return true;
			}
			else if(arg3[0].equalsIgnoreCase("reload"))
			{
				if(sender.hasPermission("dropcontrol.admin")||sender.isOp())
				{
					plugin.reloadConfig2();
					plugin.reloadConfig();
					plugin.saveItemsConfig();
					sender.sendMessage(ChatColor.DARK_GREEN+"[DropControl] Le plugin a redemarré");
					return true;
				}	
			}
		}
		Bukkit.dispatchCommand(sender,"help dropcontrol");
		return true;
		
	}
}
