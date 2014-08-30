package oxy.dropcontrol;

import org.bukkit.inventory.ItemStack;

public abstract class Utils 
{
	@SuppressWarnings("deprecation")
	public static boolean hasDurability(final ItemStack item)
	{
		final int id = item.getTypeId();
		final int iDs[]={261,267,268,272,276,283,298,299,300,301,302,303,304,305,306,307,308,309,310,311,312,313,314,315,316,317,/*OUTILS*/256,257,258,259,269,270,271,273,274,275,277,278,279,284,285,286,290,291,292,293,294,346,359};
		for(int i=0;i<iDs.length;i++)
		{
			if(iDs[i]==id)
			{
				return true;
			}
		}
		return false;
	}
}
