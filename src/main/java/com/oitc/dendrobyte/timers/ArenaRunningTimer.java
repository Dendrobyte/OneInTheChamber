package com.oitc.dendrobyte.timers;

import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.ArenaObject;
import com.oitc.dendrobyte.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by mobki, aka Dendrobyte, on 8/23/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class ArenaRunningTimer extends BukkitRunnable {

    ArenaManager am = ArenaManager.getInstance();
    String prefix = Main.getInstance().getPrefix();

    private int minutes;
    private ArenaObject arena;

    public ArenaRunningTimer(int minutes, ArenaObject arena){
        this.minutes = minutes; // The delay handles this bit (delay multiplied by 60)
        this.arena = arena;
    }

    public int getMinutes(){
        return minutes;
    }

    @Override
    public void run() {
        if(minutes == 0){
            cancel();
            endGame();
        }
        else if(minutes == 15){
            for(Player player : arena.getPlayers()){
                player.sendMessage(prefix + ChatColor.DARK_PURPLE + "There are 15 minutes remaining!");
            }
        }
        else if(minutes == 5){
            for(Player player : arena.getPlayers()){
                player.sendMessage(prefix + ChatColor.DARK_PURPLE + "There are 5 minutes remaining!");
            }
        }
        else if(minutes == 1){
            for(Player player : arena.getPlayers()){
                player.sendMessage(prefix + ChatColor.DARK_PURPLE + "There is 1 minute remaining!");
            }
        }
        minutes--;
    }

    private void endGame(){
        am.endGame(arena);
    }

    // Passthrough method for readability
    public void forceEndGame(){
        endGame();
    }
}
