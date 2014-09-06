package oxy.dropcontrol;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoadingBar implements Runnable
{
	private Main plugin;
    private final ItemMeta meta;
    private final ItemStack item;
    private int ticks = 0;
 
    public LoadingBar(final ItemStack item,Main main) 
    {
    	this.plugin=main;
    	this.item=item;
        this.meta = item.getItemMeta();
    }
 
    @Override
    public void run() 
    {
        if(ticks < 12) 
        {
            meta.setLore(Arrays.asList(ChatColor.AQUA + new String(new char[ticks]).replace("\0", "۞")));
            ticks++;
            item.setItemMeta(meta);
            Bukkit.getScheduler().runTaskLater(plugin,this,1);
        } 
        else 
        {
            meta.setLore(Arrays.asList(ChatColor.GREEN + "✔ Enregistré"));
            item.setItemMeta(meta);
        }
    }
}