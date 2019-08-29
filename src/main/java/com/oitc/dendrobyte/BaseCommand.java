package com.oitc.dendrobyte;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by mobki, aka Dendrobyte, on 8/23/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class BaseCommand implements CommandExecutor {

    String prefix = Main.getInstance().getPrefix();
    ArenaManager am = ArenaManager.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage(prefix + ChatColor.RED + "You need to be a player to execute OITC commands.");
            return true;
        }
        Player player = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("oitc")){
            String purple = "" + ChatColor.DARK_PURPLE;
            String gray = "" + ChatColor.GRAY;
            if(args.length == 0){
                player.sendMessage(prefix + ChatColor.RED + ChatColor.ITALIC + "OITC Admin Commands");
                player.sendMessage(purple + "/oitc lobby" + gray + " - Set the OITC lobby (Applies to all maps)");
                player.sendMessage(purple + "/oitc create" + gray + " - Create an arena");
                player.sendMessage(purple + "/oitc stoptimer" + gray + " - Stop arena timer of arena you are in ");
                player.sendMessage(purple + "/oitc forcestart" + gray + " - Force start arena you are in");
                player.sendMessage(purple + "/oitc forcestop <player>" + gray + " - Force stop a game with <player> in it");
                return true;
            }
            if(args[0].equalsIgnoreCase("leave")){
                am.kickPlayerFromGame(player);
                return true;
            }
            if(args[0].equalsIgnoreCase("shout")){
                String temp = "";
                for(int i = 1; i < args.length ; i++){
                    temp += args[i] + " ";
                }
                Bukkit.broadcastMessage(prefix + ChatColor.DARK_GRAY + " " + player.getName() + " " + ChatColor.GOLD + ">> " + ChatColor.GRAY + temp);
            }
            else if(!(player.hasPermission("oitc.admin"))){
                player.sendMessage(prefix + ChatColor.RED + "Only admins can use the other OITC commands.\n" +
                        prefix + "Join games with the signs at " + ChatColor.GOLD + ChatColor.ITALIC +"/warp minigames");
                return true;
            }
            if(args[0].equalsIgnoreCase("lobby")){
                player.sendMessage(prefix + "Setting OITC lobby...");
                Location playerLoc = player.getLocation();
                Main.getInstance().getConfig().set("lobby.world", playerLoc.getWorld().getName());
                Main.getInstance().getConfig().set("lobby.x", playerLoc.getBlockX());
                Main.getInstance().getConfig().set("lobby.y", playerLoc.getBlockY());
                Main.getInstance().getConfig().set("lobby.z", playerLoc.getBlockZ());
                Main.getInstance().saveConfig();
                player.sendMessage(prefix + "Lobby set!");
            }
            if(args[0].equalsIgnoreCase("create")){
                if(am.isPlayerCreating(player)){
                    am.abandonCreation(player);
                    return true;
                }
                player.sendMessage(prefix + "Putting you into creation mode... Type this command again to leave.");
                am.initiatePlayerCreation(player);
                return true;
            }
            if(args[0].equalsIgnoreCase("stoptimer")){
                if(!am.isInGame(player)){
                    player.sendMessage(prefix + ChatColor.RED + "You need to be in a game to abort the timer.");
                    return true;
                }

                ArenaObject currentArena = am.getPlayersArena(player);
                am.stopArenaTimer(currentArena);
                return true;
            }
            if(args[0].equalsIgnoreCase("forcestart")){
                if(!am.isInGame(player)){
                    player.sendMessage(prefix + ChatColor.RED + "You need to be in a game to force start it.");
                    return true;
                }
                ArenaObject currentArena = am.getPlayersArena(player);
                am.forcestartArena(currentArena, player);
            }
            if(args[0].equalsIgnoreCase("forcestop")){
                if(args.length < 2){
                    player.sendMessage(prefix + ChatColor.RED + "Please provide a player whose game you would like to stop. (Or your own username for your own game)");
                    return true;
                }
                Player inGame = Bukkit.getPlayer(args[1]);
                if(inGame == null){
                    player.sendMessage(prefix + ChatColor.RED + "Player could not be found!");
                    return true;
                }
                if(!am.isInGame(inGame)){
                    player.sendMessage(prefix + ChatColor.RED + "That player is not in a game at the moment.");
                    return true;
                }

                // Otherwise, stop the game
                ArenaObject currentArena = am.getPlayersArena(inGame);
                am.forcestopArena(currentArena, player);
            }
        }
        return true;
    }
}
