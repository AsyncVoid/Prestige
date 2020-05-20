package me.FlameBlazer.Prestige;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;;


public class TalkListener implements Listener{
	Main plugin;
	public TalkListener(Main instance){
		plugin = instance;
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onTalk(AsyncPlayerChatEvent e){
		int prest = plugin.getPrestige(e.getPlayer());
		if(prest > 0){
			e.setFormat(e.getFormat().replace("<", "<"+plugin.getConfig().getString("prestige."+prest+".prefix").replaceAll("&", "§")));
		}
	}

}
