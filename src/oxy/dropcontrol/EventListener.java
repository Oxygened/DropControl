package oxy.dropcontrol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

public class EventListener implements Listener
{
	private Main plugin;
	private ItemStack trash;
	private ItemStack save;
	private HashMap<String,Inventory> inventoriesPerWorld=new HashMap<String,Inventory>();
	private HashMap<String,List<ItemStack>> itemStacksPerWorld=new HashMap<String, List<ItemStack>>();
	@SuppressWarnings("unchecked")
	public EventListener(Main main)
	{
		this.plugin=main;
		///////////////////////////////
		//INSERTION ITEMS DEPUIS CONFIG
		///////////////////////////////
		for(World world:Bukkit.getWorlds())
		{
			itemStacksPerWorld.put(world.getName(),(List<ItemStack>) plugin.getConfig2().getList("Liste des items non-droppables - "+world.getName()));
		}
		/////////////////////
		//CREATION ITEMSTACKS
		/////////////////////
		trash=new ItemStack(Material.LAVA_BUCKET);
		ItemMeta metaTrash = trash.getItemMeta();
		metaTrash.setDisplayName(ChatColor.RED+"Poubelle");
		trash.setItemMeta(metaTrash);
		save=new ItemStack(Material.COMPASS);
		ItemMeta metaSave=save.getItemMeta();
		metaSave.setDisplayName(ChatColor.AQUA+"Sauvegarder");
		save.setItemMeta(metaSave);
		/////////////////////////
		//CREATION INVENTAIRES
		////////////////////////
		for(World world:Bukkit.getWorlds())
		{
			final String worldname=world.getName();
			String finalworldname=worldname;
			if(worldname.length()>12)
			{
				finalworldname=worldname.substring(0, 10);
				finalworldname=finalworldname+"..";
			}
			Inventory inv = Bukkit.createInventory(null, 54,ChatColor.BLUE+"Items no-drop - "+ChatColor.DARK_GREEN+finalworldname);
			List<ItemStack> liste=itemStacksPerWorld.get(world.getName());
			ItemStack items[]=liste.toArray(new ItemStack[liste.size()]);
			inv.setContents(items);
			inv.setItem(52,save);
			inv.setItem(53, trash);
			inventoriesPerWorld.put(world.getName(),inv);	
		}
		
	}
	protected Inventory getGuiInv(final String worldname)
	{
		final Inventory inv=inventoriesPerWorld.get(worldname);
		return inv;
	}
	public List<ItemStack> getItemList(final String worldname)
	{
		return itemStacksPerWorld.get(worldname);
	}
	////////////
	//Anti-drop
	////////////
	@EventHandler
	public void onDrop(final PlayerDropItemEvent e)
	{
		final boolean nameanalyze=plugin.getConfig().getBoolean("Analyse metadata");
		final Item item = e.getItemDrop();
		final ItemStack itemStackInGame=item.getItemStack().clone();
		final Player p=e.getPlayer();
		//SI L'ITEM EN JEU EST UN OUTIL/ARME
		if(Utils.hasDurability(itemStackInGame))
		{
			itemStackInGame.setDurability((short) 0);
		}
		//SI ANALYSE METADATA NON ACTIVEE
		if(!nameanalyze)
			itemStackInGame.setItemMeta(null);
		for(ItemStack itemstackOriginal:itemStacksPerWorld.get(item.getWorld().getName()))
		{	
			if(itemstackOriginal!=null)
			{
				ItemStack itemstackInInv = itemstackOriginal.clone();
				//SI L'ITEM CONFIGURE EST UN OUTIL/ARME
				if(Utils.hasDurability(itemstackInInv))
				{
					itemstackInInv.setDurability((short) 0);
				}
				//SI ANALYSE METADATA NON ACTIVEE
				if(!nameanalyze)
					itemstackInInv.setItemMeta(null);
				if(itemStackInGame.isSimilar(itemstackInInv)&&!p.isOp()&&!p.hasPermission("dropcontrol.bypass"))
				{
					e.setCancelled(true);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("Message d'erreur")));
				}
			}
			
		}
	}
	@EventHandler
	public void onDeath(final PlayerDeathEvent e)
	{
		if(!plugin.getConfig().getBoolean("ExpDrop.player"))
			return;
		final Player p=e.getEntity();
		if(!p.hasPermission("dropcontrol.bypass")&&!p.isOp())
		{
			e.setDroppedExp(0);
		}
	}
	@EventHandler
	public void onMobDeath(final EntityDeathEvent e)
	{
		if(!plugin.getConfig().getBoolean("ExpDrop.mobs"))
			return;
		e.setDroppedExp(0);
	}
	@EventHandler
	public void onBottleXp(final ExpBottleEvent e)
	{
		if(!plugin.getConfig().getBoolean("ExpDrop.bottles"))
			return;
		final ProjectileSource shooter=e.getEntity().getShooter();
		Player p=null;
		if(shooter instanceof Player)
			p=(Player) shooter;
		if(p!=null)
		{
			if(!p.hasPermission("dropcontrol.bypass")&&!p.isOp())
			{
				e.setExperience(0);
			}
		}
	}
	///////
	//GUI//
	///////
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e)
	{
		if(!e.getInventory().getName().startsWith(ChatColor.BLUE+"Items no-drop"))
		{
			return;
		}
		if(e.getRawSlot()==53)
		{
			e.setCursor(null);
			e.setCancelled(true);
		}
		else if(e.getRawSlot()==52)
		{
			Bukkit.getScheduler().runTask(plugin,new LoadingBar(e.getView().getTopInventory().getItem(52),plugin));
			saveInvToList();
			e.setCancelled(true);
		}
	}
	public void saveInvToList()
	{
		for(World world:Bukkit.getWorlds())
		{
			ItemStack[] contents=inventoriesPerWorld.get(world.getName()).getContents();
			itemStacksPerWorld.remove(world.getName());
			itemStacksPerWorld.put(world.getName(),Arrays.asList(contents));
		}
	}
}
