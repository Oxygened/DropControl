package oxy.dropcontrol;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoadingBar implements Runnable
{
	private Main plugin;
	private String lore[]= new String[1];
	private ItemStack item;
	private int i;
	public LoadingBar(ItemStack item,int i,Main main)
	{
		this.plugin=main;
		this.item=item;
		this.i=i;
	}
	@Override
	public void run() 
	{
		ItemMeta meta=item.getItemMeta();
		if(i>8)
		{
			lore[0]=ChatColor.GREEN+"Enregistré ✔";
			meta.setLore(Arrays.asList(lore));
			item.setItemMeta(meta);
			return;
		}	
		if(!meta.hasLore())
		{
			lore[0]=ChatColor.AQUA+"";
		}
		else
		{
			if(meta.getLore().get(0).equals(ChatColor.GREEN+"Enregistré ✔"))
			{
				final String[] erase={ChatColor.AQUA+""};
				meta.setLore(Arrays.asList(erase));
			}
			lore=meta.getLore().toArray(new String[1]);
		}
		lore[0]=lore[0]+"۞";
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,new LoadingBar(item,i+1,plugin),1);
	}

}
