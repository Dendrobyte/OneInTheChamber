package com.oitc.dendrobyte.listeners;

import com.oitc.dendrobyte.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by mobki, aka Dendrobyte, on 8/25/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class PlayerLeaveListener implements Listener {

    ArenaManager am = ArenaManager.getInstance();

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(am.isInGame(player)){
            am.kickPlayerFromGame(player);
        }
    }

}
