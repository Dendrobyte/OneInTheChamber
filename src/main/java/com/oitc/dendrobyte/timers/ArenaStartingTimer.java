package com.oitc.dendrobyte.timers;

import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.ArenaObject;
import com.oitc.dendrobyte.Main;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by mobki, aka Dendrobyte, on 8/25/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class ArenaStartingTimer extends BukkitRunnable {

    private String prefix = Main.getInstance().getPrefix();

    private int seconds;
    private ArenaObject arena;

    public ArenaStartingTimer(int seconds, ArenaObject arena){
        this.seconds = seconds;
        this.arena = arena;
    }

    @Override
    public void run() {
        if(seconds == 0){
            for(Player player : arena.getPlayers()){
                player.sendMessage(prefix + ChatColor.GREEN + ChatColor.ITALIC + "Oink in the Chamber is now starting- watch your back!");
            }
            ArenaManager.getInstance().startGame(arena);
            cancel();
        }
        else if(seconds == 5){
            for(Player player : arena.getPlayers()){
                player.sendMessage(prefix + "The game is starting in " + ChatColor.GOLD + ChatColor.BOLD + "5" + ChatColor.getLastColors(prefix) + " seconds!");
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 3, 10);
            }
            seconds--;
        }else {
            seconds--;
        }
    }

    public void setSeconds(int seconds){
        this.seconds = seconds;
    }

    public int getSeconds(){
        return seconds;
    }
}
