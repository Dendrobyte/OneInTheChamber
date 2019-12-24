package com.oitc.dendrobyte.listeners;

import com.oitc.dendrobyte.ArenaGameState;
import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.ArenaObject;
import com.oitc.dendrobyte.Main;
import org.bukkit.ChatColor;
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
public class SignClickListeners implements Listener {

    private ArenaManager am = ArenaManager.getInstance();
    private String prefix = Main.getInstance().getPrefix();

    // HEADS UP! Sign creation is managed in ArenaCreationClickListeners, when the player is prompted to choose the join sign.

    @EventHandler
    public void onArenaJoin(PlayerInteractEvent event){
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return; // Ensure it's a right click
        if(!event.getHand().equals(EquipmentSlot.HAND)) return;
        Block clickedBlock = event.getClickedBlock();
        if(!clickedBlock.getType().toString().contains("WALL_SIGN")) return; // Ensure it's a wall sign

        Sign wallSign = (Sign) clickedBlock.getState();
        String tag = "" + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_PURPLE + "OITC" + ChatColor.DARK_GRAY + "]";
        if(!wallSign.getLine(0).equals(tag)) return;
        Player player = event.getPlayer();
        String line2 = wallSign.getLine(1);
        if(!am.arenaExists(line2)){
            player.sendMessage(prefix + ChatColor.RED + "That arena doesn't appear to exist/isn't working.");
            return;
        }
        if(am.getLobbyLocation() == null){
            player.sendMessage(prefix + ChatColor.RED + "No lobby location is set! Contact an administrator.");
            return;
        }
        ArenaObject workingArena = am.getArenaFromName(line2);
        if(workingArena.getState() == ArenaGameState.WAITING){
            am.addPlayerToGame(workingArena, player);
            return;
        }
        if(workingArena.getState() != ArenaGameState.WAITING){
            player.sendMessage(prefix + "That arena is currently running!");
            return;
        }
    }

}
