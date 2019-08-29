package com.oitc.dendrobyte.listeners;

import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * Created by mobki, aka Dendrobyte, on 8/25/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class BlockPlaceBreakListener implements Listener {

    ArenaManager am = ArenaManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(am.isInGame(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(am.isInGame(player)){
            event.setCancelled(true);
        }
    }
}
