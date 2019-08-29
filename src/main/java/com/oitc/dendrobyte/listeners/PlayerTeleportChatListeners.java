package com.oitc.dendrobyte.listeners;

import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.ArenaObject;
import com.oitc.dendrobyte.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;

/**
 * Created by mobki, aka Dendrobyte, on 8/23/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class PlayerTeleportChatListeners implements Listener {

    private ArenaManager am = ArenaManager.getInstance();
    private String prefix = Main.getInstance().getPrefix();

    // If a player teleports outside of the world, prevent it. Otherwise it's probably just a respawn
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        if(!am.isInGame(player)){
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if(!from.getWorld().equals(to.getWorld())){
            event.setCancelled(true);
            player.sendMessage(prefix + "You can't teleport away from a game!" + ChatColor.RED + " Use " + ChatColor.BOLD + "/oitc leave " + ChatColor.RED + " to leave");
        }
    }

    // Handle players falling outside of the world, into the void
    @EventHandler
    public void onPlayerFallIntoVoid(EntityDamageEvent event){
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player)event.getEntity();
        if(!am.isInGame(player)) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            event.setCancelled(true);
            am.spawnPlayer(player, am.getPlayersArena(player));
        }
    }

    // Cancel commands in-game, aside from oitc commands (don't want to make another class for this- fight me)
    @EventHandler
    public void onPlayerUseCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        String message = event.getMessage();
        if(!am.isInGame(player)){
            return;
        }
        if(message.substring(1, 5).equalsIgnoreCase("oitc")){
            return;
        }
        else {
            event.setCancelled(true);
            player.sendMessage(prefix + "Commands aren't allowed!" + ChatColor.RED + " Use " + ChatColor.BOLD + "/oitc leave " + ChatColor.RED + "to leave");
        }
    }

    // Another chat event I forgot about but am putting here bc I AM FREE TO DO SO >:(
    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if(!am.isInGame(player)){
            for(Player playerInGame : event.getRecipients()){
                if(am.isInGame(player)){
                    event.getRecipients().remove(playerInGame);
                }
            }
        } else if(am.isInGame(player)){
            event.setCancelled(true);
            ArenaObject arena = am.getPlayersArena(player);
            for(Player playerInGame : arena.getPlayers()){
                playerInGame.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "OITC" + ChatColor.DARK_GRAY + "] " +
                        ChatColor.DARK_GRAY + player.getName() + ChatColor.GOLD + " >> " + ChatColor.GRAY + event.getMessage());
            }
        }
    }

}
