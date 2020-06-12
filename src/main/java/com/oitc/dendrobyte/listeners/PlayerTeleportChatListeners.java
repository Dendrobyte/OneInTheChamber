package com.oitc.dendrobyte.listeners;

import com.oitc.dendrobyte.ArenaGameState;
import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.ArenaObject;
import com.oitc.dendrobyte.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
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

import static org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH;

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

    // Handle players falling outside of the world (void damage), drowning, and fire damage
    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent event){
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player)event.getEntity();
        if(!am.isInGame(player)) return;
        double health = player.getHealth();
        if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setDamage(0);
            if(am.getKiller(player) != null){
                am.eliminatePlayer(player, am.getKiller(player));
            } else {
                String voidDeath = ChatColor.GOLD + player.getName() + ChatColor.GRAY + " fell into the void!";
                if(am.getPlayersArena(player).getState() == ArenaGameState.RUNNING){
                    am.eliminatePlayer(player, voidDeath);
                } else {
                    am.spawnPlayer(player, am.getPlayersArena(player));
                    player.getInventory().clear();
                }
            }

        }
        else if(event.getCause() == EntityDamageEvent.DamageCause.FALL){
            event.setCancelled(true);
        }
        else if(event.getCause() == EntityDamageEvent.DamageCause.DROWNING){
            if(health <= (player.getAttribute(GENERIC_MAX_HEALTH).getDefaultValue()) / 4) {
                event.setCancelled(true);
                String drownedDeath = ChatColor.GOLD + player.getName() + " decided to sleep with the fishes!";
                player.sendMessage(ChatColor.RED + "Be careful! " + ChatColor.GRAY + "If drowning brings you below 25% health, you will be eliminated.");
                am.eliminatePlayer(player, drownedDeath);
            }
        }
        else if(event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA){
            if(health <= (player.getAttribute(GENERIC_MAX_HEALTH).getDefaultValue()) / 4){
                event.setCancelled(true);
                String burnedDeath = ChatColor.GOLD + player.getName() + " has burned out!";
                player.sendMessage(ChatColor.RED + "Be careful! " + ChatColor.GRAY + "If fire brings you below 25% health, you will be eliminated.");
                am.eliminatePlayer(player, burnedDeath);
            }
        }
    }

    // Cancel commands in-game, aside from oitc commands (don't want to make another class for this- fight me)
    @EventHandler
    public void onPlayerUseCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        String message = event.getMessage();
        if(!am.isInGame(player) || message.length() < 5){
            return;
        }
        if(!message.substring(1, 5).equalsIgnoreCase("oitc")){
            event.setCancelled(true);
            player.sendMessage(prefix + "Commands aren't allowed!" + ChatColor.RED + " Use" + ChatColor.BOLD + " /oitc leave " + ChatColor.RED + "to leave");
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
            if(event.getMessage().charAt(0) == '*') {
                event.setMessage(event.getMessage().substring(1));
                return;
            }
            event.setCancelled(true);
            ArenaObject arena = am.getPlayersArena(player);
            for(Player playerInGame : arena.getPlayers()){
                playerInGame.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "OITC" + ChatColor.DARK_GRAY + "] " +
                        ChatColor.DARK_GRAY + player.getName() + ChatColor.GOLD + " >> " + ChatColor.GRAY + event.getMessage());
            }
        }
    }

}
