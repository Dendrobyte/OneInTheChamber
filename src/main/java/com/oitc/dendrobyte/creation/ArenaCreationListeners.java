package com.oitc.dendrobyte.creation;

import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Created by mobki, aka Dendrobyte, on 8/23/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class ArenaCreationListeners implements Listener {

    ArenaManager am = ArenaManager.getInstance();
    String prefix = Main.getInstance().getPrefix();

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!am.isPlayerCreating(player)) return;
        if(event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        Block clickedBlock = event.getClickedBlock();
        if(am.getPlayerCreationState(player) == CreationStates.SPAWNLOCS){
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()){
                // Add a spawn location
                Location modifiedLoc = new Location(clickedBlock.getWorld(), clickedBlock.getLocation().getBlockX(), clickedBlock.getLocation().getBlockY()+2, clickedBlock.getLocation().getBlockZ());
                am.getPlayerCreationArena(player).addLocation(modifiedLoc);
                player.sendMessage(prefix + ChatColor.GREEN + ChatColor.ITALIC + "Location added!");
                return;
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking()){
                // Wrap everything up
                am.finishCreation(player);
            }
        } else {
            if (clickedBlock.getType().toString().contains("SIGN")) {
                Sign sign = (Sign) clickedBlock.getState();
                if (am.getPlayerCreationState(player) == CreationStates.SIGNLOC) {
                    if (sign.getLine(0).isEmpty()) {
                        player.sendMessage(prefix + "Please put the arena name on the first line of the sign.");
                        return;
                    } else {
                        String arenaName = sign.getLine(0);
                        // Tell the player the sign is clicked
                        player.sendMessage(prefix + "Alright! The arena " + arenaName + " has had a join sign added!");
                        event.setCancelled(true);
                        // TODO: Make sure the arena doesn't already exist
                        am.getPlayerCreationArena(player).setName(arenaName);
                        am.getPlayerCreationArena(player).setSignLoc(sign.getLocation());

                        // Make the sign details
                        String tag = "" + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "OITC" + ChatColor.DARK_GRAY + "]";
                        sign.setLine(0, tag);
                        sign.setLine(1, arenaName);
                        sign.setLine(2, "0/" + am.maxPlayers);
                        sign.setLine(3, "" + ChatColor.DARK_GRAY + ChatColor.ITALIC + "WAITING");
                        sign.update();

                        // Tell the player to click the spawn locations around the map and change their state
                        am.setPlayerCreationState(player, CreationStates.SPAWNLOCS);
                        player.sendMessage(prefix + "Alright, now fly around the map and RIGHT CLICK all the spawn locations!");
                        player.sendMessage(prefix + "To wrap up the selection (and overall arena creation), SHIFT + RIGHT CLICK on a block.");
                    }
                }
            }
        }
    }

}
