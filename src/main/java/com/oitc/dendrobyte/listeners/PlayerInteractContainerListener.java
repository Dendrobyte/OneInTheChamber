package com.oitc.dendrobyte.listeners;

import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.Main;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * OneInTheChamber created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class PlayerInteractContainerListener implements Listener {

    ArenaManager am = ArenaManager.getInstance();

    @EventHandler
    public void playerInOITCUsesContainer(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(am.isInGame(player)){
            if(event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
            if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
            Block clickedBlock = event.getClickedBlock();
            System.out.println("Block state: " + clickedBlock.getState());
            if(clickedBlock.getState() instanceof InventoryHolder){
                player.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "You can not use containers while in a game.");
            }
        }
    }

}
