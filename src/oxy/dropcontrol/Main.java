package oxy.dropcontrol;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin 
{
	private EventListener eventClass;
	private File itemsConfigFile;
	private FileConfiguration itemsConfig;
	public void onEnable()
	{
		loadConfig();
		eventClass = new EventListener(this);
		this.getServer().getPluginManager().registerEvents(eventClass, this);
		this.getCommand("dropcontrol").setExecutor(new Commands(this));
	}
	public void onDisable()
	{
		this.saveConfig();
		saveItemsConfig();
	}
	public EventListener getEventClass()
	{
		return eventClass;
	}
	
	public FileConfiguration getConfig2()
	{
		return itemsConfig;
	}
	public void saveConfig2()
	{
		try
		{
			itemsConfig.save(itemsConfigFile);
		}
		catch(Exception e){e.printStackTrace();}
	}
	public void reloadConfig2()
	{
		try
		{
			itemsConfig.load(itemsConfigFile);
		}
		catch(Exception e){e.printStackTrace();}
	}
	public void loadConfig()
	{

        itemsConfigFile=new File(this.getDataFolder(),"data.yml");
        if(!itemsConfigFile.exists())//modified
        {
                File dir = this.getDataFolder();
                if(!dir.exists())
                        dir.mkdirs();
                try 
                {
                	itemsConfigFile.createNewFile();//added
                } 
                catch(final IOException ioe) 
                {
                	ioe.printStackTrace();
                	return;
                }
        }
        itemsConfig=YamlConfiguration.loadConfiguration(itemsConfigFile);//moved
		this.getConfig().addDefault("Analyse metadata",false);
		this.getConfig().addDefault("ExpDrop.player",false);
		this.getConfig().addDefault("ExpDrop.mobs",false);
		this.getConfig().addDefault("ExpDrop.bottles",false);
		this.getConfig().addDefault("Message d'erreur", "&4Vous n'avez pas la permission de lacher cet objet !");
		ItemStack[] items={new ItemStack(Material.APPLE), new ItemStack(Material.ANVIL)};
		for(World world : Bukkit.getWorlds())
		{
				this.getConfig2().addDefault("Liste des items non-droppables - "+world.getName(),Arrays.asList(items));
		}
		this.getConfig().options().copyDefaults(true);
		this.getConfig2().options().copyDefaults(true);
		this.saveConfig();
	    this.saveConfig2();
	}
	public void saveItemsConfig()
	{
		for(World world: Bukkit.getWorlds())
		{
			this.getConfig2().set("Liste des items non-droppables - "+world.getName(),eventClass.getItemList(world.getName()));
			this.saveConfig2();
		}
	}
}
