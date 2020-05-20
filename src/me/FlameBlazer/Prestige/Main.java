package me.FlameBlazer.Prestige;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	TalkListener tl;
	public static Economy economy = null;
	public static Chat chat = null;
	public static Permission permission = null;
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    public void onEnable(){
    	getConfig().options().copyDefaults(true);
    	if (!setupEconomy() ) {
    		getServer().getConsoleSender().sendMessage(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    	if (!setupPermissions() ) {
    		getServer().getConsoleSender().sendMessage(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    	tl = new TalkListener(this);
    	getServer().getPluginManager().registerEvents(tl, this);
	}
	public void onDisable(){
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(commandLabel.equalsIgnoreCase("prestige")){
				if(args.length ==0){
					if(p.hasPermission("prestige.canprestige")){
						int prest = getPrestige(p);
						if(prest < 10){
							double price = (double) getConfig().getDouble("prestige."+(prest+1)+".price");
							if(economy.has(p, price)){
								economy.withdrawPlayer(p, price);
								permission.playerRemoveGroup(p, "Z");
								permission.playerAddGroup(p, "A");
								getConfig().set(p.getUniqueId().toString(), prest+1);
								p.sendMessage("§eSuccessfully prestiged to "+(prest+1)+"!");
								broadPrest(p);
							}else{
								p.sendMessage("§4You need $"+(String.valueOf(price).replace("E9", " billion"))+" to prestige");
							}
						}else{
							p.sendMessage("§4You are the max prestige");
						}
					}else{
						p.sendMessage("§4You cannot prestige yet.");
					}
				}else if(args.length ==1){
					if(args[0].equalsIgnoreCase("help")){
						if(sender.hasPermission("prestige.help")){
							sendHelp(sender);
						}else{
							sender.sendMessage("§4You do not have access to that command.");
						}
					}else if(args[0].equalsIgnoreCase("set")){
						if(sender.hasPermission("prestige.set")){
						    sender.sendMessage("§6/prestige set <Player> <Prestige>");
						}else{
							sender.sendMessage("§4You do not have access to that command.");
						}
					}else{
						if(sender.hasPermission("prestige.help")){
							sender.sendMessage("§6Do \"/prestige help\" for more info.");
						}else{
							sender.sendMessage("§4You do not have access to that command.");
						}						
					}
				}else if(args.length == 2){
					if(sender.hasPermission("prestige.help")){
						sender.sendMessage("§6Do \"/prestige help\" for more info.");
					}else{
						sender.sendMessage("§4You do not have access to that command.");
					}
				}else if(args.length == 3){
					if(args[0].equalsIgnoreCase("set")){
						try{
							@SuppressWarnings("deprecation")
							Player target = getServer().getPlayer(args[1]);
							try{
								int prestTo = Integer.parseInt(args[2]);
								getConfig().set(target.getUniqueId().toString(), prestTo);
								sender.sendMessage("§2Player "+target.getDisplayName()+" §2successfully prestiged to "+prestTo);
							}catch(Exception ex){
								sender.sendMessage("§4Invalid integer "+args[2]+".");
							}
						}catch(Exception ex){
							sender.sendMessage("§4Player is not online");
						}
					}else{
						if(sender.hasPermission("prestige.help")){
							sender.sendMessage("§6Do \"/prestige help\" for more info.");
						}else{
							sender.sendMessage("§4You do not have access to that command.");
						}
					}
				}else{
					if(sender.hasPermission("prestige.help")){
						sender.sendMessage("§6Do \"/prestige help\" for more info.");
					}else{
						sender.sendMessage("§4You do not have access to that command.");
					}
				}
				
			}else{
				p.sendMessage("§6Did you say prestige?");
			}
		}else{
			sender.sendMessage("§4You need to be a player to do that.");
		}
		return false;
	}
	public int getPrestige(Player p){
		try{
			int a = getConfig().getInt(p.getUniqueId().toString());
			return a;
		}catch(Exception ex){
			return 0;
		}
	}
	public void broadPrest(Player p){
		for(Player t : getServer().getOnlinePlayers()){
			t.sendMessage(ChatColor.GREEN + "Player "+p.getDisplayName() + ChatColor.GREEN +" just prestiged!");
		}
	}
	public void sendHelp(CommandSender s){
		s.sendMessage("§6--------------------§1§lPrestige§6--------------------");
		s.sendMessage("§6/prestige help "+ChatColor.AQUA+"- Displays this page");
		s.sendMessage("§6/prestige set <Player> <Prestige> "+ChatColor.AQUA+"- Set a players' prestige");
	}
	
}
